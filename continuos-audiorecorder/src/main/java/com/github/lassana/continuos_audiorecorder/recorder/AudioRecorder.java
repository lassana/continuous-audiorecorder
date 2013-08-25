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
        public void onStarted();
    }

    public static interface OnStopListener extends OnError {
        public void onStarted();
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

    public void start(final OnStartListener listener) {

    }

    public void pause(final OnPauseListener listener) {

    }

    public void stop(final OnStopListener listener) {

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
