package net.garrapeta.gameengine.module;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.util.SparseArray;

/**
 * Manager de samples de sonido
 * 
 * @author GaRRaPeTa
 */
public class SoundManager implements OnCompletionListener {

    // -------------------------------- Variables est�ticas

    /** Source trazas de log */
    public static final String LOG_SRC = "sound";

    // ----------------------------- Variables de instancia

    private SparseArray<PlayerSet> mPlayerSets;
    
    private boolean mSoundEnabled = true;

    // ----------------------------------------------- Constructor

    /**
     * Constructor protegido
     */
    public SoundManager() {
        mPlayerSets = new SparseArray<PlayerSet>();
    }

    // ------------------------------------------ métodos de instancia

    /**
     * @return if the sound is enabled
     */
    public final boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    /**
     * Sets whether or not the sound is enabled
     * @param soundEnabled
     */
    public final void setSoundEnabled(boolean soundEnabled) {
        mSoundEnabled = soundEnabled;
    }

    /**
     * Stops all players and releases resources.
     * 
     * @param context
     */
    public void releaseAll() {
        Log.i(LOG_SRC, "disposing");
        stopAll();
        int key = 0;
        for(int i = 0; i < mPlayerSets.size(); i++) {
            key = mPlayerSets.keyAt(i);
            PlayerSet playerSet = mPlayerSets.get(key);
            playerSet.releaseAll();
        }
        mPlayerSets.clear();
    }

    /**
     * A�ade un sample
     * 
     * @param resourceId
     * @param sampleId
     */
    public void add(int resourceId, int sampleId, Context context) {
        MediaPlayer player = MediaPlayer.create(context, resourceId);
        if (player != null) {
            player.setOnCompletionListener(this);
            /*
             * try { player.prepare(); } catch (IOException ioe) {
             * ioe.printStackTrace(); String msg = "Poblems adding sampleId " +
             * ioe.toString(); Log.e(LOG_SRC, msg); throw new
             * IllegalArgumentException(msg); }
             */
    
            PlayerSet set = mPlayerSets.get(sampleId);
            if (set != null) {
                set = mPlayerSets.get(sampleId);
            } else {
                set = new PlayerSet();
                mPlayerSets.put(sampleId, set);
            }
    
            set.add(player);
        } else {
            Log.w(LOG_SRC, "Could not load player for sample id: " + sampleId);
        }
    }

    /**
     * Reproduce uno de los samples identificados con sampleId
     * 
     * @param sampleId
     */
    public MediaPlayer play(int sampleId) {
        return play(sampleId, false, true);
    }

    /**
     * Reproduce uno de los samples identificados con sampleId
     * 
     * @param sampleId
     */
    public MediaPlayer play(int sampleId, boolean loop, boolean reset) {
        if (!mSoundEnabled) {
            return null;
        }
        PlayerSet set = mPlayerSets.get(sampleId);
        if (set != null) {
            MediaPlayer p = set.play(loop, reset);
            Log.d(LOG_SRC, "playing: " + sampleId + " " + p);
            return p;
        } else {
            throw new IllegalArgumentException("No such sample id: " + sampleId);
        }
    }

    /**
     * Pausa el player
     */
    public void pause(MediaPlayer p) {
        Log.d(LOG_SRC, "pause: " + p);
        if (p.isPlaying()) {
            p.pause();
        }
    }

    /**
     * Resume el player
     */
    public void resume(MediaPlayer p) {
        Log.d(LOG_SRC, "resume: " + p);
        if (p.getCurrentPosition() != 0) {
            p.start();
        }
    }

    /**
     * Para el player
     */
    public void stop(MediaPlayer p) {
        Log.d(LOG_SRC, "stop: " + p);
        if (p.isPlaying()) {
            p.stop();
            try {
                p.prepare();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                String msg = "Poblems stopping sampleId " + ioe.toString();
                Log.e(LOG_SRC, msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Para todos los reproductores
     */
    public void stopAll() {
        int key = 0;
        for(int i = 0; i < mPlayerSets.size(); i++) {
            key = mPlayerSets.keyAt(i);
            PlayerSet playerSet = mPlayerSets.get(key);
            playerSet.stopAll();
        }
    }

    /**
     * Pause todos los reproductores
     */
    public void pauseAll() {
        int key = 0;
        for(int i = 0; i < mPlayerSets.size(); i++) {
            key = mPlayerSets.keyAt(i);
            PlayerSet playerSet = mPlayerSets.get(key);
            playerSet.pauseAll();
        }
    }

    /**
     * Resume todos los reproductores
     */
    public void resumeAll() {
        int key = 0;
        for(int i = 0; i < mPlayerSets.size(); i++) {
            key = mPlayerSets.keyAt(i);
            PlayerSet playerSet = mPlayerSets.get(key);
            playerSet.resumeAll();
        }
    }

    
    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.seekTo(0);
    }


    /**
     * método que encapsula diferentes players que pueden estar referenciados
     * por un mismo id
     * 
     * @author GaRRaPeTa
     */
    class PlayerSet {

        // ----------------------------- Variables de instancia

        private MediaPlayer mPlayer = null;
        private ArrayList<MediaPlayer> mPlayers = null;

        // ----------------------------- métodos

        /**
         * A�ade un player
         * 
         * @param player
         */
        void add(MediaPlayer player) {
            if (mPlayer == null && mPlayers == null) {

                mPlayer = player;
            } else {

                if (mPlayers == null) {
                    mPlayers = new ArrayList<MediaPlayer>();
                    mPlayer = null;
                }
                mPlayers.add(player);
            }

            // sample.prepareAsync();
        }

        /**
         * Hace sonar uno de los players de este PlayerSet
         */
        MediaPlayer play(boolean loop, boolean reset) {
            MediaPlayer p = null;
            if (mPlayer != null) {
                p = mPlayer;
            } else {
                int index = (int) Math.floor(Math.random() * mPlayers.size());
                p = mPlayers.get(index);
            }

            if (p.isPlaying()) {
                if (reset) {
                    p.seekTo(0);
                } else {
                    return p;
                }
            }

            p.setLooping(loop);
            p.start();

            return p;
        }

        /**
         * Pausa todos los reproductores de este set
         */
        public void pauseAll() {
            if (mPlayer != null) {
                SoundManager.this.pause(mPlayer);
            }
            if (mPlayers != null) {
                int playersCount = mPlayers.size();
                for (int i = 0; i < playersCount; i++) {
                    MediaPlayer player = mPlayers.get(i);
                    SoundManager.this.pause(player);
                }
            }
        }

        /**
         * Resume todos los reproductores de este set
         */
        public void resumeAll() {
            if (mPlayer != null) {
                SoundManager.this.resume(mPlayer);
            }
            if (mPlayers != null) {
                int playersCount = mPlayers.size();
                for (int i = 0; i < playersCount; i++) {
                    MediaPlayer player = mPlayers.get(i);
                    SoundManager.this.resume(player);
                }
            }
        }

        /**
         * Para todos los reproductores de este set
         */
        public void stopAll() {
            if (mPlayer != null) {
                SoundManager.this.stop(mPlayer);
            }
            if (mPlayers != null) {
                int playersCount = mPlayers.size();
                for (int i = 0; i < playersCount; i++) {
                    MediaPlayer player = mPlayers.get(i);
                    SoundManager.this.stop(player);
                }
            }
        }

        /**
         * Disposea todos los reproductores de este set
         */
        public void releaseAll() {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            if (mPlayers != null) {
                int size = mPlayers.size();
                for (int i = size - 1; i >= 0; i--) {
                    MediaPlayer player = mPlayers.get(i);
                    player.release();
                    player = null;
                }
            }
        }

    }

}
