package net.garrapeta.gameengine;

import java.util.HashMap;

import android.content.Context;
import android.os.Vibrator;


/**
 * Manager de samples de sonido
 * @author GaRRaPeTa
 */
public class VibratorManager {

	// -------------------------------- Variables estáticas
	
	private static VibratorManager instance;
	
	// ----------------------------- Variables de instancia
	
	private HashMap<Integer, long[]> patterns;
	
	private Vibrator vibrator;
	
	// ----------------------------------------------- Constructor
	
	/**
	 * Constructor protegido
	 */
	private VibratorManager() {
		patterns = new HashMap<Integer, long[]>();
	}
	
	// --------------------------------------------- Métodos estáticos
	
	/**
	 * @return instancia de VibratorManager
	 */
	public static VibratorManager getInstance() {
		if (instance == null) {
			instance = new VibratorManager();
		}
		return instance;
	}
	
	// ------------------------------------------ Métodos de instancia
	
	/**
	 * Inicialización
	 * @param context
	 */
	public void init(Context context) {
		// Get instance of Vibrator from current Context
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	/**
	 * Borra todo
	 * @param context
	 */
	public void clearAll() {
		patterns.clear();
	}
	
	/**
	 * Añade un sample
	 * @param patternId
	 * @param patternId
	 */
	public void add(long[] pattern, int patternId) {
		if (vibrator == null) {
			throw new IllegalStateException("VibratorManager is not initialized.");
		}
		
		if (patterns.containsKey(patternId)) {
			throw new IllegalArgumentException("Pattern already existed: " + patternId);
		} else {
			patterns.put(patternId, pattern);
		}

	}
	
	/**
	 * Reproduce uno de los samples identificados con sampleId
	 * @param patternId
	 */
	public void play(int patternId) {
		if (patterns.containsKey(patternId)) {
			long[] pattern = patterns.get(patternId);
			vibrator.vibrate(pattern, -1);
		} else {
			throw new IllegalArgumentException("No such sample id: " + patternId);
		}
	}


	
}