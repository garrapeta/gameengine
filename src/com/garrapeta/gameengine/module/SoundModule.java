package com.garrapeta.gameengine.module;


import java.io.IOException;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

/**
 * Sound samples manager
 * 
 * @author GaRRaPeTa
 */
public class SoundModule implements OnCompletionListener {

    // -------------------------------- Variables est�ticas

    /** Source trazas de log */
    private static final String TAG = SoundModule.class.getSimpleName();
    
    private final SoundModuleDelegate mDelegate;
    
    private final static short ACTION_PLAY = 0;
    private final static short ACTION_PAUSE = 1;
    private final static short ACTION_RESUME = 2;
    private final static short ACTION_STOP = 3;

    // ----------------------------------------------- Constructor

    public SoundModule(Context context, short minimumLevel) {
    	mDelegate = new SoundModuleDelegate(context, minimumLevel);
    }

	public SoundModuleDelegate.ResourceData create(short level, short key) {
		return mDelegate.create(level, key);
	}

	public void play(short key) {
		play(key, false);
	}

	public void play(short soundId, boolean repeat) {
		short repeatShort = (short) ((repeat) ? 1 : 0);
		mDelegate.executeOverOneResourceForKey(soundId, ACTION_PLAY, repeatShort);
	}

	public void stop(short key) {
		mDelegate.executeOverAllResourcesForKey(key, ACTION_STOP);
	}

	public void pauseAll() {
		mDelegate.executeOverAllResources(ACTION_PAUSE);
	}

	public void resumeAll() {
		mDelegate.executeOverAllResources(ACTION_RESUME);
	}
	
	public void release() {
		mDelegate.releaseAll();
	}
	
	private void onPlay(MediaPlayer player, boolean repeat) {
		player.setLooping(repeat);
        if (player.isPlaying()) {
        	player.seekTo(0);
        }
        player.start();
	}

	private void onPause(MediaPlayer player) {
        if (player.isPlaying()) {
        	player.pause();
        }
	}
	
	private void onResume(MediaPlayer player) {
        if (player.getCurrentPosition() != 0) {
        	player.start();
        }
	}
	
	private void onStop(MediaPlayer player) {
        if (player.isPlaying()) {
        	player.stop();
            try {
            	player.prepare();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                String msg = "Poblems stopping sampleId " + ioe.toString();
                Log.e(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
        }
	}
	
	@Override
	public void onCompletion(MediaPlayer player) {
		player.seekTo(0);
	}

	private class SoundModuleDelegate extends LoadedLeveledActionsModule<Integer, MediaPlayer, Short>  {

		private final Context mContext;
		
		private SoundModuleDelegate(Context context, short minimumLevel) {
			super(minimumLevel);
			mContext = context;
		}

		@Override
		protected MediaPlayer obtain(Integer resId) {
	        MediaPlayer player = MediaPlayer.create(mContext, resId);
	        if (player != null) {
	            player.setOnCompletionListener(SoundModule.this);
	        } else {
	            Log.e(TAG, "Could not load player for sample id: " + resId);
	        }
	        
	        Short n = 1;
	        n.intValue();
	        return player;
		}

		@Override
		protected void onExecute(MediaPlayer player, Short... params) {
			switch (params[0]) {
			case ACTION_PLAY:
		        onPlay(player, params[1] > 0);
				break;
			case ACTION_PAUSE:
				onPause(player);
				break;
			case ACTION_RESUME:
				onResume(player);
				break;
			case ACTION_STOP:
				onStop(player);
				break;
			}
		}

		@Override
		protected void onRelease(MediaPlayer player) {
			player.release();
		}
	}


}
