package com.github.lassana.continuous_audiorecorder;

import android.test.ActivityInstrumentationTestCase2;

import com.github.lassana.continuous_audiorecorder.activity.MainActivity;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author lassana
 * @since 8/25/13
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testDoubleRecorder() {
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
