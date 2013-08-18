package com.github.lassana.continuos_audiorecorder.recorder;

/**
 * @author lassana
 * @since 8/18/13
 */
public class AudioRecorder {

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

    private AudioRecorder() {

    }

    public static AudioRecorder build() {
        return null;
    }

    public void start(final OnStartListener listener) {

    }

    public void pause(final OnPauseListener listener) {

    }

    public void stop(final OnStopListener listener) {

    }
}
