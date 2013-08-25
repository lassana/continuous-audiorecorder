package com.github.lassana.continuos_audiorecorder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.lassana.continuos_audiorecorder.R;
import com.github.lassana.continuos_audiorecorder.recorder.AudioRecorder;

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

        mAudioRecorder = AudioRecorder.build();

        mStartButton = (Button) view.findViewById(R.id.buttonStartRecord);
        mStartButton.setOnClickListener(mOnClickListener);
        mPauseButton = (Button) view.findViewById(R.id.buttonPauseRecord);
        mPauseButton.setOnClickListener(mOnClickListener);
        mPlayButton = (Button) view.findViewById(R.id.buttonPlayRecord);
        mPlayButton.setOnClickListener(mOnClickListener);

        invalidateButtons();
    }

    private void invalidateButtons() {
        switch (mAudioRecorder.getStatus()) {
            case STATUS_UNKNOWN:
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(mAudioRecorder.getRecordFileName() != null);
                break;
            case STATUS_READY:
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(mAudioRecorder.getRecordFileName() != null);
                break;
            case STATUS_RECORDING:
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mPlayButton.setEnabled(false);
                break;
            case STATUS_PAUSED:
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(mAudioRecorder.getRecordFileName() != null);
                break;
            default:
                break;
        }
    }

    private void start() {

    }

    private void pause() {

    }

    private void play() {

    }
}
