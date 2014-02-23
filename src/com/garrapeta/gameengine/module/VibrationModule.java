package com.garrapeta.gameengine.module;

import android.content.Context;
import android.os.Vibrator;

/**
 * Manager of vibration patterns
 * 
 * @author GaRRaPeTa
 */
public class VibrationModule {

	private final VibrationModuleDelegate mDelegate;
	private Vibrator mVibrator;

    /**
     * Constructor
     * 
     * @param context
     * @param minimumLevel
     */
    public VibrationModule(Context context, short minimumLevel) {
    	mDelegate = new VibrationModuleDelegate(minimumLevel);
		mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void add(short level, short key, long[] pattern) {
		mDelegate.create(level, key).add(pattern);
	}
	
	public void vibrate(short key) {
		mDelegate.executeOverOneResourceForKey(key);
	}
	
	public void releaseAll() {
		mDelegate.releaseAll();
	}
	
	/**
	 * Delegate used by the module
	 * @author garrapeta
	 */
	private class VibrationModuleDelegate extends LevelActionsModule<long[], Integer>  {
		
		private VibrationModuleDelegate(short minimumLevel) {
			super(minimumLevel);
		}

		@Override
		protected void onExecute(long[] pattern, Integer... params) {
			final int repeat;
			if (params != null) {
				repeat = (params[0] != null) ? params[0] : -1;
			} else {
				repeat = -1;
			}
			mVibrator.vibrate(pattern, repeat);
		}
	}
}