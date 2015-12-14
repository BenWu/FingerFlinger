package ca.benwu.slippyfinger.utils;

import android.util.Log;

import ca.benwu.slippyfinger.BuildConfig;

/**
 * Created by Ben Wu on 12/13/2015.
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
