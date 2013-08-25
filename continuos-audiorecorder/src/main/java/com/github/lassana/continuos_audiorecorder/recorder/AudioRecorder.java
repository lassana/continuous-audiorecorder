package com.github.lassana.continuos_audiorecorder.recorder;

/**
 * @author lassana
 * @since 8/18/13
 */
public class AudioRecorder {

    public static enum Status {
        STATUS_UNKNOWN,
        STATUS_READY,
        STATUS_RECORDING,
        STATUS_PAUSED
    }

    public static interface OnError {
        public void onError();
    }

    public static interface OnStartListener extends OnError {
        public void onStarted();
    }

    public static interface OnPauseListener extends OnError {
        public void onPaused();
    }

    private Status mStatus;
    private String mActiveRecordFileName;

    private AudioRecorder() {
        mStatus = Status.STATUS_UNKNOWN;
        mActiveRecordFileName = null;
    }

    public static AudioRecorder build() {
        return new AudioRecorder();
    }

    /**
     * Сontinues existing record or starts new one.
     */
    public void start(final OnStartListener listener) {

    }

    /**
     * Pauses active recording.
     */
    public void pause(final OnPauseListener listener) {

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
        return mStatus == Status.STATUS_READY;
    }

    public boolean isPaused() {
        return mStatus == Status.STATUS_PAUSED;
    }
}
