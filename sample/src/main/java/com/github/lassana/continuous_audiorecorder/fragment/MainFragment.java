package com.github.lassana.continuous_audiorecorder.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.lassana.continuous_audiorecorder.R;
import com.github.lassana.recorder.AudioRecorder;

import java.io.File;

/**
 * @author lassana
 * @since 8/18/13
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private Button mStartButton;
    private Button mPauseButton;
    private Button mPlayButton;

    private AudioRecorder mAudioRecorder;

    private String mActiveRecordFileName;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonStartRecord:
                    start();
                    break;
                case R.id.buttonPauseRecord:
                    pause();
                    break;
                case R.id.buttonPlayRecord:
                    play();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAudioRecorder = AudioRecorder.build(getActivity(), getNextFileName());

        mStartButton = (Button) view.findViewById(R.id.buttonStartRecord);
        mStartButton.setOnClickListener(mOnClickListener);
        mPauseButton = (Button) view.findViewById(R.id.buttonPauseRecord);
        mPauseButton.setOnClickListener(mOnClickListener);
        mPlayButton = (Button) view.findViewById(R.id.buttonPlayRecord);
        mPlayButton.setOnClickListener(mOnClickListener);

        invalidateButtons();
    }

    private String getNextFileName() {
        return Environment.getExternalStorageDirectory()
                + File.separator
                + "Record_"
                + System.currentTimeMillis()
                + ".mp4";
    }

    private void invalidateButtons() {
        switch (mAudioRecorder.getStatus()) {
            case STATUS_UNKNOWN:
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_READY_TO_RECORD:
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_RECORDING:
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_RECORD_PAUSED:
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(true);
                break;
            default:
                break;
        }
    }

    private void start() {
        mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                invalidateButtons();
            }

            @Override
            public void onException(Exception e) {
                invalidateButtons();
                Toast.makeText(getActivity(), getString(R.string.toast_error_audio_recorder, e),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pause() {
        mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mActiveRecordFileName = activeRecordFileName;
                invalidateButtons();
            }

            @Override
            public void onException(Exception e) {
                invalidateButtons();
                Toast.makeText(getActivity(), getString(R.string.toast_error_audio_recorder, e),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void play() {
        File file = new File(mActiveRecordFileName);
        if ( file.exists() ) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(intent);
        }
    }
}
