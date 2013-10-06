package com.github.lassana.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

/**
 * @author lassana
 * @since 8/18/13
 */
public class AudioRecorder {

    public static enum Status {
        STATUS_UNKNOWN,
        STATUS_READY_TO_RECORD,
        STATUS_RECORDING,
        STATUS_RECORD_PAUSED
    }

    public static interface OnError {
        public void onError(Throwable th);
    }

    public static interface OnStartListener extends OnError {
        public void onStarted();
    }

    public static interface OnPauseListener extends OnError {
        public void onPaused(String activeRecordFileName);
    }

    /**
     * @author lassana
     * @since 10/06/2013
     */
    public static class MediaRecorderConfig {
        private final int mAudioEncodingBitRate;
        private final int mAudioChannels;
        private final int mAudioSource;
        private final int mAudioEncoder;

        public static final MediaRecorderConfig DEFAULT =
                new MediaRecorderConfig(64 * 1024,          /* 64 Kib per second */
                        2,                                  /* Stereo */
                        MediaRecorder.AudioSource.DEFAULT,  /* Default audio source.
                                                               (usually, phone microphone) */
                        ApiHelper.DEFAULT_AUDIO_ENCODER);   /* Default encoder
                                                               for target Android version */

        /**
         * Constructor.
         *
         * @param audioEncodingBitRate
         * Used for {@link android.media.MediaRecorder#setAudioEncodingBitRate}
         * @param audioChannels
         * Used for {@link android.media.MediaRecorder#setAudioChannels}
         * @param audioSource
         * Used for {@link android.media.MediaRecorder#setAudioSource}
         * @param audioEncoder
         * Used for {@link android.media.MediaRecorder#setAudioEncoder}
         */
        public MediaRecorderConfig(int audioEncodingBitRate, int audioChannels, int audioSource, int audioEncoder) {
            mAudioEncodingBitRate = audioEncodingBitRate;
            mAudioChannels = audioChannels;
            mAudioSource = audioSource;
            mAudioEncoder = audioEncoder;
        }

    }

    public class StartRecordTask extends AsyncTask<OnStartListener, Void, Throwable> {

        private OnStartListener mOnStartListener;

        @Override
        protected Throwable doInBackground(OnStartListener... params) {
            this.mOnStartListener = params[0];
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioEncodingBitRate(64 * 1024);
            mMediaRecorder.setAudioChannels(2);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(getTemporaryFileName());
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            Throwable throwable = null;
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (IOException e) {
                throwable = e;
            }
            return throwable;
        }

        @Override
        protected void onPostExecute(Throwable throwable) {
            super.onPostExecute(throwable);
            if (throwable == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORDING);
                mOnStartListener.onStarted();
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnStartListener.onError(throwable);
            }
        }
    }

    public class PauseRecordTask extends AsyncTask<OnPauseListener, Void, Throwable> {
        private OnPauseListener mOnPauseListener;

        @Override
        protected Throwable doInBackground(OnPauseListener... params) {
            mOnPauseListener = params[0];
            Throwable throwable = null;
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            } catch (Throwable th) {
                throwable = th;
            }
            if ( throwable == null ) {
                appendToFile(mTargetRecordFileName, getTemporaryFileName());
            }
            return throwable;
        }

        @Override
        protected void onPostExecute(Throwable throwable) {
            super.onPostExecute(throwable);
            if (throwable == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORD_PAUSED);
                mOnPauseListener.onPaused(mTargetRecordFileName);
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnPauseListener.onError(throwable);
            }
        }
    }

    private Status mStatus;
    private String mTargetRecordFileName;
    private MediaRecorder mMediaRecorder;
    private Context context;

    private AudioRecorder() {
        mStatus = Status.STATUS_UNKNOWN;
    }

    public static AudioRecorder build(Context context, final String targetFileName) {
        AudioRecorder rvalue = new AudioRecorder();
        rvalue.mTargetRecordFileName = targetFileName;
        rvalue.context = context;
        rvalue.mStatus = Status.STATUS_READY_TO_RECORD;
        return rvalue;
    }

    /**
     * Continues existing record or starts new one.
     */
    @SuppressLint("NewApi")
    public void start(final OnStartListener listener) {
        StartRecordTask task = new StartRecordTask();
        if (ApiHelper.HAS_EXECUTE_ON_EXECUTOR_METHOD) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
        } else {
            task.execute(listener);
        }
    }

    /**
     * Pauses active recording.
     */
    @SuppressLint("NewApi")
    public void pause(final OnPauseListener listener) {
        PauseRecordTask task = new PauseRecordTask();
        if (ApiHelper.HAS_EXECUTE_ON_EXECUTOR_METHOD) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
        } else {
            task.execute(listener);
        }
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getRecordFileName() {
        return mTargetRecordFileName;
    }

    public boolean isRecording() {
        return mStatus == Status.STATUS_RECORDING;
    }

    public boolean isReady() {
        return mStatus == Status.STATUS_READY_TO_RECORD;
    }

    public boolean isPaused() {
        return mStatus == Status.STATUS_RECORD_PAUSED;
    }

    private void setStatus(final Status status) {
        mStatus = status;
    }

    private String getTemporaryFileName() {
        return context.getCacheDir().getAbsolutePath() + File.separator + "tmprecord";
    }

    private void appendToFile(final String targetFileName, final String newFileName) {
        Mp4ParserWrapper.append(targetFileName, newFileName);
    }
}
