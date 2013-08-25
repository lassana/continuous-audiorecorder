package com.github.lassana.continuos_audiorecorder;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.github.lassana.continuos_audiorecorder.activity.MainActivity;
import com.github.lassana.continuos_audiorecorder.fragment.MainFragment;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author lassana
 * @since 8/25/13
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    private MainActivity mMainActivity;
    private MainFragment mMainFragment;
    private Button mStartButton;
    private Button mPauseButton;
    private Button mStopButton;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        solo = new Solo(getInstrumentation(), getActivity());

        mMainActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testDoubleRecorder() {
        //assertNotNull(null);

        solo.clickOnButton("Start record");
        solo.sleep(1000);
        solo.clickOnButton("Pause record");
        solo.sleep(1000);
        solo.clickOnButton("Start record");
        solo.sleep(1000);
        solo.clickOnButton("Pause record");
        solo.sleep(1000);
        solo.clickOnButton("Play record");
        solo.sleep(1000);
        solo.goBack();
    }

}
