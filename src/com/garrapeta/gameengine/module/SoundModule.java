package com.garrapeta.gameengine.module;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;

import com.garrapeta.gameengine.utils.LogX;

/**
 * Sound samples manager
 * 
 * TODO: Use {@link SoundPool}
 */
public class SoundModule implements OnCompletionListener {

    private final static String TAG = SoundModule.class.getSimpleName();

    private final SoundModuleDelegate mDelegate;

    private final static short ACTION_PLAY = 0;
    private final static short ACTION_PAUSE = 1;
    private final static short ACTION_RESUME = 2;
    private final static short ACTION_STOP = 3;

    /**
     * Constructor
     * 
     * @param context
     * @param minimumLevel
     */
    public SoundModule(Context context, short minimumLevel) {
        mDelegate = new SoundModuleDelegate(context, minimumLevel);
    }

    public SoundModuleDelegate.ResourceData create(short level, short key) {
        LogX.v(TAG, "Creating: " + key);
        return mDelegate.create(level, key);
    }

    public void play(short key) {
        play(key, false);
    }

    public void play(short key, boolean repeat) {
        LogX.v(TAG, "Play: " + key);
        try {
            short repeatShort = (short) ((repeat) ? 1 : 0);
            mDelegate.executeOverOneResourceForKey(key, ACTION_PLAY, repeatShort);
        } catch (Exception e) {
            LogX.e(TAG, "Error playing resource " + key, e);
        }
    }

    public void stop(short key) {
        LogX.v(TAG, "Stopping: " + key);
        try {
            mDelegate.executeOverAllResourcesForKey(key, ACTION_STOP);
        } catch (Exception e) {
            LogX.e(TAG, "Error stoping resource " + key, e);
        }
    }

    public void pauseAll() {
        LogX.i(TAG, "Pausing all...");
        try {
            mDelegate.executeOverAllResources(ACTION_PAUSE);
        } catch (Exception e) {
            LogX.e(TAG, "Error pausing all the resources", e);
        }
    }

    public void resumeAll() {
        LogX.i(TAG, "Resuming all...");
        try {
            mDelegate.executeOverAllResources(ACTION_RESUME);
        } catch (Exception e) {
            LogX.e(TAG, "Error resuming all the resources", e);
        }

    }

    public void releaseAll() {
        LogX.i(TAG, "Releasing all...");
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
        try {
            if (player.isPlaying()) {
                player.stop();
                player.prepare();
                player.seekTo(0);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error stopping the player", e);
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        player.seekTo(0);
    }

    /**
     * Delegate used by the module
     * 
     * @author garrapeta
     */
    private class SoundModuleDelegate extends LoadedLevelActionsModule<Integer, MediaPlayer, Short> {

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
                LogX.e(TAG, "Could not load player for sample id: " + resId);
            }
            return player;
        }

        @Override
        protected void onExecute(MediaPlayer player, Short... params) {
            if (player == null) {
                LogX.e(TAG, "Audio player is null!!");
                return;
            }
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
            LogX.v(TAG, "Releasing: " + player);
            player.release();
        }
    }

}
