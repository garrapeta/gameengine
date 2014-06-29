package com.garrapeta.gameengine.utils;

import android.util.Log;

/**
 * Utility class to avoid printing log traces depending on configuration
 */
public class LogX {

    public static boolean sEnabled = true;

    /**
     * Enables or disabled the logger
     * 
     * @param enabled
     */
    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
    }

    /**
     * {@inheritDoc Log#v(String, String)
     */
    public static void v(String tag, String string) {
        if (sEnabled) {
            Log.v(tag, string);
        }
    }

    /**
     * {@inheritDoc Log#d(String, String)
     */
    public static void d(String tag, String string) {
        if (sEnabled) {
            Log.d(tag, string);
        }
    }

    /**
     * {@inheritDoc Log#i(String, String)
     */
    public static void i(String tag, String string) {
        if (sEnabled) {
            Log.i(tag, string);
        }
    }

    /**
     * {@inheritDoc Log#w(String, String)
     */
    public static void w(String tag, String string) {
        if (sEnabled) {
            Log.w(tag, string);
        }
    }

    /**
     * {@inheritDoc Log#e(String, String)}
     */
    public static void e(String tag, String string) {
        if (sEnabled) {
            Log.e(tag, string);
        }
    }

    /**
     * {@inheritDoc Log#e(String, String, Throwable)}
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (sEnabled) {
            Log.e(tag, msg, tr);
        }
    }
}
