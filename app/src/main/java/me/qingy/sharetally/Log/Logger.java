package me.qingy.sharetally.Log;

import android.util.Log;

/**
 * Created by YangQ on 9/18/2014.
 */
public class Logger {
    private static final String TAG = "Tally";

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(Object obj, String msg) {
        Log.e(TAG, String.format("[%s] %s", obj.getClass().getName(), msg));
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }
}
