package com.github.lassana.recorder;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Builder that creates a ready-to-use AudioRecorder.
 *
 * @author Nikolai Doronin
 * @since 11/5/2015.
 */
public final class AudioRecorderBuilder {

    private Context mContext;
    private String mFileName;
    private AudioRecorder.MediaRecorderConfig mConfig;
    private boolean mIsLoggable;

    private AudioRecorderBuilder() {
    }

    public static AudioRecorderBuilder with(@NonNull Context context) {
        final AudioRecorderBuilder audioRecorderBuilder = new AudioRecorderBuilder();
        audioRecorderBuilder.mContext = context;
        return audioRecorderBuilder;
    }

    public AudioRecorderBuilder fileName(@NonNull String targetFileName) {
        mFileName = targetFileName;
        return this;
    }

    public AudioRecorderBuilder config(@NonNull AudioRecorder.MediaRecorderConfig config) {
        mConfig = config;
        return this;
    }

    public AudioRecorderBuilder loggable() {
        mIsLoggable = true;
        return this;
    }

    /**
     * Returns a ready-to-use AudioRecorder.<p>
     * Uses {@link com.github.lassana.recorder.AudioRecorder.MediaRecorderConfig#DEFAULT} as
     * {@link android.media.MediaRecorder} config by default.<p>
     * Logs are turned off by default.
     */
    public AudioRecorder build() {
        if (mFileName == null) {
            throw new RuntimeException("Target filename is not set: use `#fileName` method");
        }
        return new AudioRecorder(mContext, mFileName, mConfig, mIsLoggable);
    }

}
