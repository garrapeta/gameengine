package com.garrapeta.gameengine.utils;

/**
 * Utility class that allows to be disabled to avoid memory allocation
 */
public class L {

    public static boolean sEnabled = true;

    /**
     * Enables or disabled the logger
     * 
     * @param enabled
     */
    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
    }
}
