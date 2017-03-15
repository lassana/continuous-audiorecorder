package com.github.lassana.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 8/18/13
 */
public class AudioRecorder {

    private static final String TAG = "AudioRecorder";

    public enum Status {
        STATUS_UNKNOWN,
        STATUS_READY_TO_RECORD,
        STATUS_RECORDING,
        STATUS_RECORD_PAUSED
    }

    public interface OnException {
        void onException(Exception e);
    }

    public interface OnStartListener extends OnException {
        void onStarted();
    }

    public interface OnPauseListener extends OnException {
        void onPaused(String activeRecordFileName);
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
                new MediaRecorderConfig(
                        /* 64 Kib per second            */
                        64 * 1024,
                        /* Stereo                       */
                        2,
                        /* Default audio source (usually, device microphone)  */
                        MediaRecorder.AudioSource.DEFAULT,
                        /* Default encoder for the target Android version   */
                        ApiHelper.DEFAULT_AUDIO_ENCODER);

        /**
         * Constructor.
         *
         * @param audioEncodingBitRate Used for {@link android.media.MediaRecorder#setAudioEncodingBitRate}
         * @param audioChannels        Used for {@link android.media.MediaRecorder#setAudioChannels}
         * @param audioSource          Used for {@link android.media.MediaRecorder#setAudioSource}
         * @param audioEncoder         Used for {@link android.media.MediaRecorder#setAudioEncoder}
         */
        public MediaRecorderConfig(int audioEncodingBitRate, int audioChannels, int audioSource, int audioEncoder) {
            mAudioEncodingBitRate = audioEncodingBitRate;
            mAudioChannels = audioChannels;
            mAudioSource = audioSource;
            mAudioEncoder = audioEncoder;
        }

    }

    class StartRecordTask extends AsyncTask<OnStartListener, Void, Exception> {

        private OnStartListener mOnStartListener;

        @Override
        protected Exception doInBackground(OnStartListener... params) {
            this.mOnStartListener = params[0];
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioEncodingBitRate(mMediaRecorderConfig.mAudioEncodingBitRate);
            mMediaRecorder.setAudioChannels(mMediaRecorderConfig.mAudioChannels);
            mMediaRecorder.setAudioSource(mMediaRecorderConfig.mAudioSource);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(getTemporaryFileName());
            mMediaRecorder.setAudioEncoder(mMediaRecorderConfig.mAudioEncoder);

            Exception exception = null;
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORDING);
                mOnStartListener.onStarted();
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnStartListener.onException(e);
            }
        }
    }

    class PauseRecordTask extends AsyncTask<OnPauseListener, Void, Exception> {
        private OnPauseListener mOnPauseListener;
        private void appendToFile(@NonNull final String targetFileName, @NonNull final String newFileName) {
            Mp4ParserWrapper.append(targetFileName, newFileName);
        }
        @Override
        protected Exception doInBackground(OnPauseListener... params) {
            mOnPauseListener = params[0];
            Exception exception = null;
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            } catch (Exception e) {
                exception = e;
            }
            if (exception == null) {
                appendToFile(mTargetRecordFileName, getTemporaryFileName());
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORD_PAUSED);
                mOnPauseListener.onPaused(mTargetRecordFileName);
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnPauseListener.onException(e);
            }
        }
    }

    private Status mStatus;
    private MediaRecorder mMediaRecorder;
    private final String mTargetRecordFileName;
    private final Context mContext;
    private final MediaRecorderConfig mMediaRecorderConfig;
    private final boolean mIsLoggable;

    /* package-local */ AudioRecorder(@NonNull final Context context,
                          @NonNull final String targetRecordFileName,
                          @NonNull final MediaRecorderConfig mediaRecorderConfig,
                          final boolean isLoggable) {
        mTargetRecordFileName = targetRecordFileName;
        mContext = context;
        mMediaRecorderConfig = mediaRecorderConfig;
        mIsLoggable = isLoggable;

        mStatus = Status.STATUS_READY_TO_RECORD;
    }

    /**
     * Returns a ready-to-use AudioRecorder.
     * Uses {@link com.github.lassana.recorder.AudioRecorder.MediaRecorderConfig#DEFAULT} as
     * {@link android.media.MediaRecorder} config.
     *
     * @param context An Android context instance.
     * @param targetFileName A filename (full path) of a record.
     * @return A configured {@link AudioRecorder} instance.
     *
     * @deprecated Use AudioRecorderBuilder instead.
     */
    @Deprecated
    public static AudioRecorder build(@NonNull final Context context,
                                      @NonNull final String targetFileName) {
        return build(context, targetFileName, MediaRecorderConfig.DEFAULT);
    }

    /**
     * Returns a ready-to-use AudioRecorder.
     *
     * @param context An Android context instance.
     * @param targetFileName A filename (full path) of a record.
     * @param mediaRecorderConfig A record config.
     * @return A configured {@link AudioRecorder} instance.
     *
     * @deprecated Use AudioRecorderBuilder instead.
     */
    @Deprecated
    public static AudioRecorder build(@NonNull final Context context,
                                      @NonNull final String targetFileName,
                                      @NonNull final MediaRecorderConfig mediaRecorderConfig) {
        AudioRecorder rvalue = new AudioRecorder(
                context,
                targetFileName,
                mediaRecorderConfig,
                false);
        rvalue.mStatus = Status.STATUS_READY_TO_RECORD;
        return rvalue;
    }

    /**
     * Continues an existing record or starts a new one.
     *
     * @param listener The listener instance.
     */
    @SuppressLint("NewApi")
    public void start(@NonNull final OnStartListener listener) {
        StartRecordTask task = new StartRecordTask();
        task.execute(listener);

    }

    /**
     * Pauses an active recording.
     *
     * @param listener The listener instance.
     */
    @SuppressLint("NewApi")
    public void pause(@NonNull final OnPauseListener listener) {
        PauseRecordTask task = new PauseRecordTask();
        task.execute(listener);
    }

    /**
     * Returns the current recording status.
     *
     * @return The current recorder status.
     *
     * @see AudioRecorder.Status
     */
    public Status getStatus() {
        return mStatus;
    }

    /**
     * Returns the current record filename.
     *
     * @return The current filename (the full path).
     */
    public String getRecordFileName() {
        return mTargetRecordFileName;
    }

    /**
     * Returns true if record is started, false if not.
     *
     * @return true if record is started.
     */
    public boolean isRecording() {
        return mStatus == Status.STATUS_RECORDING;
    }

    /**
     * Returns true if record can be started, false if not.
     *
     * @return true if record can be started.
     */
    public boolean isReady() {
        return mStatus == Status.STATUS_READY_TO_RECORD;
    }

    /**
     * Returns true if record is paused, false if not.
     *
     * @return true if record is paused.
     */
    public boolean isPaused() {
        return mStatus == Status.STATUS_RECORD_PAUSED;
    }

    private void setStatus(@NonNull final Status status) {
        mStatus = status;
    }

    private String getTemporaryFileName() {
        return mContext.getCacheDir().getAbsolutePath() + File.separator + "tmprecord";
    }



    /**
     * Drops the current recording.
     */
    public void cancel() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            }
        } catch (Exception e) {
            error("Exception during record cancelling", e);
        }
        mStatus = Status.STATUS_UNKNOWN;
    }

    private void debug(@NonNull final String msg, @Nullable final Exception e) {
        if (mIsLoggable) Log.d(TAG, msg, e);
    }

    private void error(@NonNull final String msg, @Nullable final Exception e) {
        if (mIsLoggable) Log.e(TAG, msg, e);
    }

}
