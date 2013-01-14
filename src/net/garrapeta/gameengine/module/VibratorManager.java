package net.garrapeta.gameengine.module;

import android.os.Vibrator;
import android.util.SparseArray;

/**
 * Manager de samples de sonido
 * 
 * @author GaRRaPeTa
 */
public class VibratorManager {

    // -------------------------------- Variables est�ticas

    private static VibratorManager sInstance;

    // ----------------------------- Variables de instancia

    private SparseArray<long[]> mPatterns;

    private Vibrator mVibrator;

    // ----------------------------------------------- Constructor

    /**
     * Constructor protegido
     */
    private VibratorManager() {
        mPatterns = new SparseArray<long[]>();
    }

    // --------------------------------------------- M�todos est�ticos

    /**
     * @return instancia de VibratorManager
     */
    public static VibratorManager getInstance() {
        if (sInstance == null) {
            sInstance = new VibratorManager();
        }
        return sInstance;
    }

    // ------------------------------------------ M�todos de instancia

    /**
     * Inicializaci�n
     * 
     * @param context
     */
    public void init(Vibrator vibrator) {
        // Get instance of Vibrator from current Context
        mVibrator = vibrator;
    }

    /**
     * A�ade un sample
     * 
     * @param patternId
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
            throw new IllegalArgumentException("No such sample id: " + patternId);
        }
    }

    /**
     * Frees resources
     */
    public void dispose() {
        if (mPatterns != null) {
            mPatterns.clear();
            mVibrator = null;
        }
    }

}