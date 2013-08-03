package com.garrapeta.gameengine;

import android.content.Context;
import android.os.Vibrator;
import android.util.SparseArray;

/**
 * Manager de samples de sonido
 * 
 * @author GaRRaPeTa
 */
public class VibratorManager {

    // ----------------------------- Variables de instancia

    private SparseArray<long[]> mPatterns;

    private Vibrator mVibrator;

    // ----------------------------------------------- Constructor

    /**
     * Constructor
     * @param context
     */
    VibratorManager(Context context) {
    	mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        mPatterns = new SparseArray<long[]>();
    }


    // ------------------------------------------ métodos de instancia

    /**
     * Adds a sample
     * 
     * @param pattern
     * @param patternId
     */
    public void add(long[] pattern, int patternId) {
        if (mVibrator == null) {
            throw new IllegalStateException("VibratorManager is not initialized.");
        }

        if (mPatterns.get(patternId) != null) {
            throw new IllegalArgumentException("Pattern already existed: " + patternId);
        } else {
            mPatterns.put(patternId, pattern);
        }

    }

    /**
     * Play one of the samples mapped to the passed Id
     * 
     * @param patternId
     */
    public void play(int patternId) {
        long[] pattern = mPatterns.get(patternId);
        if (pattern != null) {
            mVibrator.vibrate(pattern, -1);
        } else {
            throw new IllegalArgumentException("No such vibration pattern id: " + patternId);
        }
    }

    /**
     * Frees resources
     */
    void releaseAll() {
        if (mPatterns != null) {
            mPatterns.clear();
            mVibrator = null;
        }
    }

}