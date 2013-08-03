package com.garrapeta.gameengine;

import android.content.Context;
import android.os.Vibrator;

/**
 * Manager of vibration patterns
 * 
 * @author GaRRaPeTa
 */
public class VibratorManager {

	private final CustomLevelBasedResourcesModule mCustomLevelBasedResourcesModule;
	private Vibrator mVibrator;

    public VibratorManager(Context context, short minimumLevel) {
    	mCustomLevelBasedResourcesModule = new CustomLevelBasedResourcesModule(minimumLevel);
		mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void add(short level, short key, long[] pattern) {
		if (mCustomLevelBasedResourcesModule.getCount(key) > 0) {
			throw new IllegalArgumentException("Already loaded: " + key);
		}
		mCustomLevelBasedResourcesModule.create(level, key).add(pattern);
	}
	
	public void vibrate(short key) {
		mCustomLevelBasedResourcesModule.executeOverOne(key);
	}
	
	public void release() {
		mCustomLevelBasedResourcesModule.releaseAll();
	}

	private class CustomLevelBasedResourcesModule extends LevelBasedResourcesModule<long[], long[], Integer>  {
		
		private CustomLevelBasedResourcesModule(short minimumLevel) {
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