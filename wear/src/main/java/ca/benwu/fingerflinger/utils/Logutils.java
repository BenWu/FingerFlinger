package ca.benwu.fingerflinger.utils;

import android.util.Log;

import ca.benwu.fingerflinger.BuildConfig;

/**
 * Created by Ben Wu on 12/15/2015.
 */
public class Logutils {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String tag, String msg) {
        if (DEBUG) Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG) Log.v(tag, msg);
    }

    public static void wtf(String tag, String msg) {
        if (DEBUG) Log.wtf(tag, msg);
    }
}
