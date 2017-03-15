package com.github.lassana.continuous_audiorecorder.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.github.lassana.continuous_audiorecorder.R;
import com.github.lassana.continuous_audiorecorder.RecorderApplication;
import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 8/18/13
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 0x1;

    private Button mStartButton;
    private Button mPauseButton;
    private Button mPlayButton;
    private ImageView mCassetteImage;

    private Uri mAudioRecordUri;
    private String mActiveRecordFileName;

    private AudioRecorder mAudioRecorder;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @TargetApi(Build.VERSION_CODES.M)
        private void tryStart() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int checkAudio = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
                final int checkStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkAudio != PackageManager.PERMISSION_GRANTED || checkStorage != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                        showNeedPermissionsMessage();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showNeedPermissionsMessage();
                    } else {
                        requestPermissions(new String[]{
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS);
                    }
                } else {
                    start();
                }
            } else {
                start();
            }
        }
        private void pause() {
            mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
                @Override
                public void onPaused(String activeRecordFileName) {
                    mActiveRecordFileName = activeRecordFileName;

                    getActivity().setResult(Activity.RESULT_OK,
                            //new Intent().setData(Uri.parse(mActiveRecordFileName)));
                            new Intent().setData(saveCurrentRecordToMediaDB(mActiveRecordFileName)));
                    invalidateViews();
                }

                @Override
                public void onException(Exception e) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    invalidateViews();
                    message(getString(R.string.error_audio_recorder, e));
                }
            });
        }

        private void play() {
            File file = new File(mActiveRecordFileName);
            if (file.exists()) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonStartRecording:
                    v.setEnabled(false);
                    tryStart();
                    break;
                case R.id.buttonPauseRecording:
                    v.setEnabled(false);
                    pause();
                    break;
                case R.id.buttonPlayRecording:
                    play();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        if (mAudioRecorder.isRecording()) {
            mAudioRecorder.cancel();
            getActivity().setResult(Activity.RESULT_CANCELED);
        }
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecorderApplication application = RecorderApplication.getApplication(getActivity());
        mAudioRecorder = application.getRecorder();
        if (mAudioRecorder == null
                || mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_UNKNOWN) {
            mAudioRecorder = AudioRecorderBuilder.with(application)
                    .fileName(getNextFileName())
                    .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                    .loggable()
                    .build();
            application.setRecorder(mAudioRecorder);
        }

        mCassetteImage = (ImageView) view.findViewById(R.id.image_cassette);
        mStartButton = (Button) view.findViewById(R.id.buttonStartRecording);
        mStartButton.setOnClickListener(mOnClickListener);
        mPauseButton = (Button) view.findViewById(R.id.buttonPauseRecording);
        mPauseButton.setOnClickListener(mOnClickListener);
        mPlayButton = (Button) view.findViewById(R.id.buttonPlayRecording);
        mPlayButton.setOnClickListener(mOnClickListener);

        invalidateViews();
    }

    private String getNextFileName() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .getAbsolutePath()
                + File.separator
                + "Record_"
                + System.currentTimeMillis()
                + ".mp4";
    }

    private void invalidateViews() {
        switch (mAudioRecorder.getStatus()) {
            case STATUS_UNKNOWN:
                mCassetteImage.clearAnimation();
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_READY_TO_RECORD:
                mCassetteImage.clearAnimation();
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_RECORDING:
                mCassetteImage.startAnimation(
                        AnimationUtils.loadAnimation(getActivity(), R.anim.animation_pulse));
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_RECORD_PAUSED:
                mCassetteImage.clearAnimation();
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(true);
                break;
            default:
                break;
        }
    }



    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                boolean userAllowed = true;
                for (final int result : grantResults) {
                    userAllowed &= result == PackageManager.PERMISSION_GRANTED;
                }
                if (userAllowed) {
                    start();
                } else {
                    /*
                     * Cannot show dialog from here
                     * https://code.google.com/p/android-developer-preview/issues/detail?id=2823
                     */
                    showNeedPermissionsMessage();
                }
                break;
            default:
                break;
        }
    }

    private void showNeedPermissionsMessage() {
        invalidateViews();
        message(getString(R.string.error_no_permissions));
    }

    private void message(String message) {
        final View root = getView();
        if (root != null) {
            final Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    private void start() {
        mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                invalidateViews();
            }

            @Override
            public void onException(Exception e) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                invalidateViews();
                message(getString(R.string.error_audio_recorder, e));
            }
        });
    }



    /**
     * Creates new item in the system's media database.
     *
     * @see <a href="https://github.com/android/platform_packages_apps_soundrecorder/blob/master/src/com/android/soundrecorder/SoundRecorder.java">Android Recorder source</a>
     */
    public Uri saveCurrentRecordToMediaDB(final String fileName) {
        if (mAudioRecordUri != null) return mAudioRecordUri;

        final Activity activity = getActivity();
        final Resources res = activity.getResources();
        final ContentValues cv = new ContentValues();
        final File file = new File(fileName);
        final long current = System.currentTimeMillis();
        final long modDate = file.lastModified();
        final Date date = new Date(current);
        final String dateTemplate = res.getString(R.string.audio_db_title_format);
        final SimpleDateFormat formatter = new SimpleDateFormat(dateTemplate, Locale.getDefault());
        final String title = formatter.format(date);
        final long sampleLengthMillis = 1;
        // Lets label the recorded audio file as NON-MUSIC so that the file
        // won't be displayed automatically, except for in the playlist.
        cv.put(MediaStore.Audio.Media.IS_MUSIC, "0");

        cv.put(MediaStore.Audio.Media.TITLE, title);
        cv.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        cv.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        cv.put(MediaStore.Audio.Media.DATE_MODIFIED, (int) (modDate / 1000));
        cv.put(MediaStore.Audio.Media.DURATION, sampleLengthMillis);
        cv.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        cv.put(MediaStore.Audio.Media.ARTIST, res.getString(R.string.audio_db_artist_name));
        cv.put(MediaStore.Audio.Media.ALBUM, res.getString(R.string.audio_db_album_name));

        Log.d(TAG, "Inserting audio record: " + cv.toString());

        final ContentResolver resolver = activity.getContentResolver();
        final Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "ContentURI: " + base);

        mAudioRecordUri = resolver.insert(base, cv);
        if (mAudioRecordUri == null) {
            return null;
        }
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mAudioRecordUri));
        return mAudioRecordUri;
    }
}
