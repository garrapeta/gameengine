package com.garrapeta.gameengine;

import android.content.Context;
import android.os.Vibrator;

/**
 * Manager of vibration patterns
 * 
 * @author GaRRaPeTa
 */
public class VibratorManager {

	private final CustomLevelBasedResourcesManager mCustomLevelBasedResourcesManager;
	private Vibrator mVibrator;

    public VibratorManager(Context context, short minimumLevel) {
    	mCustomLevelBasedResourcesManager = new CustomLevelBasedResourcesManager(minimumLevel);
		mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void add(short level, short key, long[] pattern) {
		if (mCustomLevelBasedResourcesManager.getCount(key) > 0) {
			throw new IllegalArgumentException("Already loaded: " + key);
		}
		mCustomLevelBasedResourcesManager.create(level, key).add(pattern);
	}
	
	public void vibrate(short key) {
		mCustomLevelBasedResourcesManager.executeOverOneResourceForKey(key);
	}
	
	public void release() {
		mCustomLevelBasedResourcesManager.releaseAll();
	}

	private class CustomLevelBasedResourcesManager extends LevelBasedResourcesManager<long[], long[], Integer>  {
		
		private CustomLevelBasedResourcesManager(short minimumLevel) {
			super(minimumLevel);
		}

		@Override
		protected long[] load(long[] pattern) {
			return pattern;
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

		@Override
		protected void onRelease(long[] pattern) {
			// nothing
		}
	}

}