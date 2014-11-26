package com.github.lassana.continuous_audiorecorder.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.github.lassana.continuous_audiorecorder.R;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 8/18/13
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
