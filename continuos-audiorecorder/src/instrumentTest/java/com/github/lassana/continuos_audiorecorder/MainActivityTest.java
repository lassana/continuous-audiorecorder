package com.github.lassana.continuos_audiorecorder;

import android.test.ActivityInstrumentationTestCase2;

import com.github.lassana.continuos_audiorecorder.activity.MainActivity;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author lassana
 * @since 8/25/13
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    private MainActivity mMainActivity;

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
        assertNotNull(null);
    }

}
