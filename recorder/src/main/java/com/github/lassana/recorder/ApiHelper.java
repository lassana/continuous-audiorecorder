package com.github.lassana.recorder;

import android.os.Build;

/**
 * @author lassana
 * @since 8/25/13
 */
public class ApiHelper {

    public static final boolean HAS_EXECUTE_ON_EXECUTOR_METHOD =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

}
