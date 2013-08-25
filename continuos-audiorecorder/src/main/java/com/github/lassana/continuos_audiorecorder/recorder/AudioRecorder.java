package com.github.lassana.continuos_audiorecorder.recorder;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import com.github.lassana.continuos_audiorecorder.util.ApiHelper;

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

    public class StartRecordTask extends AsyncTask<OnStartListener, Void, Throwable> {

        private OnStartListener mOnStartListener;

        @Override
        protected Throwable doInBackground(OnStartListener... params) {
            mOnStartListener = params[0];
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioEncodingBitRate(64 * 1024);
            mMediaRecorder.setAudioChannels(2);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(mActiveRecordFileName);
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
            return throwable;
        }

        @Override
        protected void onPostExecute(Throwable throwable) {
            super.onPostExecute(throwable);
            if (throwable == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORD_PAUSED);
                mOnPauseListener.onPaused(mActiveRecordFileName);
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnPauseListener.onError(throwable);
            }
        }
    }

    private Status mStatus;
    private String mActiveRecordFileName;
    private MediaRecorder mMediaRecorder;

    private AudioRecorder() {
        mStatus = Status.STATUS_READY_TO_RECORD;
    }

    public static AudioRecorder build(final String targetFileName) {
        AudioRecorder rvalue = new AudioRecorder();
        rvalue.mActiveRecordFileName = targetFileName;
        return rvalue;
    }

    /**
     * Сontinues existing record or starts new one.
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
        return mActiveRecordFileName;
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
}
