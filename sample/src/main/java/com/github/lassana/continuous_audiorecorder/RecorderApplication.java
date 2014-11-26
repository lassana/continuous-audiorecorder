package com.github.lassana.continuous_audiorecorder;

import android.app.Application;
import android.content.Context;
import com.github.lassana.recorder.AudioRecorder;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 11/26/14.
 */
public class RecorderApplication extends Application {

    public static RecorderApplication getApplication(Context context) {
        if (context instanceof RecorderApplication) {
            return (RecorderApplication) context;
        }
        return (RecorderApplication) context.getApplicationContext();
    }

    private AudioRecorder mAudioRecorder;

    public AudioRecorder createRecorder(String targetFileName) {
        mAudioRecorder = AudioRecorder.build(this, targetFileName);
        return mAudioRecorder;
    }

    public AudioRecorder getRecorder() {
        return mAudioRecorder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
