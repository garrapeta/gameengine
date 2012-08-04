package net.garrapeta.gameengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;


/**
 * Manager de samples de sonido
 * @author GaRRaPeTa
 */
public class SoundManager {

	// -------------------------------- Variables estáticas
	
	private static SoundManager instance;
	
	/** Source trazas de log */
	public static final String LOG_SRC = "sound";
	
	// ----------------------------- Variables de instancia
	
	private HashMap<Integer, PlayerSet> playerSets;
	
	// ----------------------------------------------- Constructor
	
	/**
	 * Constructor protegido
	 */
	private SoundManager() {
		playerSets = new HashMap<Integer, PlayerSet>();
	}
	
	// --------------------------------------------- Métodos estáticos
	
	/**
	 * @return instancia de SoundManager
	 */
	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	// ------------------------------------------ Métodos de instancia
	
	/**
	 * Borra todo
	 * @param context
	 */
	public void clearAll() {
		Log.i(LOG_SRC, "disposing");
		stopAll();
		releaseAll();
		playerSets.clear();
	}
	

	/**
	 * Añade un sample
	 * @param resourceId
	 * @param sampleId
	 */
	public void add(int resourceId, int sampleId, Context context) {
		MediaPlayer player = MediaPlayer.create(context, resourceId);
		/*
		try {
			player.prepare();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			String msg = "Poblems adding sampleId " + ioe.toString();
			Log.e(LOG_SRC, msg);
			throw new IllegalArgumentException(msg);
		}
		*/
		
		PlayerSet set;
		
		if (playerSets.containsKey(sampleId)) {
			set = playerSets.get(sampleId);
		} else {
			set = new PlayerSet();
			playerSets.put(sampleId, set);
		}
		
		set.add(player);
	}
	
	/**
	 * Reproduce uno de los samples identificados con sampleId
	 * @param sampleId
	 */
	public MediaPlayer play(int sampleId) {
		return play(sampleId, false, true);
	}
	
	/**
	 * Reproduce uno de los samples identificados con sampleId
	 * @param sampleId
	 */
	public MediaPlayer play(int sampleId, boolean loop, boolean reset) {
		if (playerSets.containsKey(sampleId)) {
			PlayerSet set = playerSets.get(sampleId);
			MediaPlayer p = set.play(loop, reset);
			Log.d(LOG_SRC, "playing: " + sampleId + " " + p);
			return p;
		} else {
			throw new IllegalArgumentException("No such sample id: " + sampleId);
		}
	}
	
	/**
	 *  Pausa el player
	 */
	public void pause(MediaPlayer p) {
		Log.d(LOG_SRC, "pause: " + p);
		if (p.isPlaying()) {
			p.pause();
		}
	}

	/**
	 *  Resume el player
	 */
	public void resume(MediaPlayer p) {
		Log.d(LOG_SRC, "resume: " + p);
		if (!p.isPlaying() && p.getCurrentPosition() != 0) {
			p.start();
		}
	}
	/**
	 *  Para el player
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
		Iterator<Entry<Integer, PlayerSet>> it = playerSets.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Integer, PlayerSet> entry = it.next();
			PlayerSet playerSet = entry.getValue();
			playerSet.stopAll();
		}
	}
	
	/**
	 * Pause todos los reproductores
	 */
	public void pauseAll() {
		Iterator<Entry<Integer, PlayerSet>> it = playerSets.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Integer, PlayerSet> entry = it.next();
			PlayerSet playerSet = entry.getValue();
			playerSet.pauseAll();
	    }
	}
	
	/**
	 * Resume todos los reproductores
	 */
	public void resumeAll() {
		Iterator<Entry<Integer, PlayerSet>> it = playerSets.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Integer, PlayerSet> entry = it.next();
			PlayerSet playerSet = entry.getValue();
			playerSet.resumeAll();
		}
	}
	
	/**
	 * Disposea todos los players
	 */
	private void releaseAll() {
		Iterator<Entry<Integer, PlayerSet>> it = playerSets.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<Integer, PlayerSet> entry = it.next();
			PlayerSet playerSet = entry.getValue();
			playerSet.releaseAll();
		}
	}

}


/**
 * Método que encapsula diferentes players que pueden estar referenciados
 * por un mismo id
 * @author GaRRaPeTa
 */
class PlayerSet {
	
	// ----------------------------- Variables de instancia
	
	private MediaPlayer player  = null;
	private ArrayList<MediaPlayer>   players = null;
	
	// ----------------------------- Métodos
	
	/**
	 * Añade un player
	 * @param sample
	 */
	void add(MediaPlayer sample) {
		if (player == null && players == null) {
			
			player = sample;
		} else {
			
			if (players == null) {
				players = new ArrayList<MediaPlayer>();
				player = null;
			}
			players.add(sample);
		}
		
//		sample.prepareAsync();
	}
	


	/**
	 *  Hace sonar uno de los players de este PlayerSet
	 */
	MediaPlayer play(boolean loop, boolean reset) {
		MediaPlayer p = null;
		if (player != null) {
			p = player;
		} else {
			int index =  (int) Math.floor(Math.random() * players.size());
			p  = players.get(index);
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
		if (player != null) {
			SoundManager.getInstance().pause(player);
		}
		if (players != null) {
			int playersCount = players.size();
			for (int i = 0; i < playersCount; i++) {
				MediaPlayer player = players.get(i);
				SoundManager.getInstance().pause(player);
			}
		}
	}
	
	/**
 	 * Resume todos los reproductores de este set 
	 */
	public void resumeAll() {
		if (player != null) {
			SoundManager.getInstance().resume(player);
		}
		if (players != null) {
			int playersCount = players.size();
			for (int i = 0; i < playersCount; i++) {
				MediaPlayer player = players.get(i);
				SoundManager.getInstance().resume(player);
			}
		}
	}
	
	/**
 	 * Para todos los reproductores de este set 
	 */
	public void stopAll() {
		if (player != null) {
			SoundManager.getInstance().stop(player);
		}
		if (players != null) {
			int playersCount = players.size();
			for (int i = 0; i < playersCount; i++) {
				MediaPlayer player = players.get(i);
				SoundManager.getInstance().stop(player);
			}
		}
	}
	
	/**
 	 * Disposea todos los reproductores de este set 
	 */
	public void releaseAll() {
		if (player != null) {
			player.release();
			player = null;
		}
		if (players != null) {
			int playersCount = players.size();
			for (int i = 0; i < playersCount; i++) {
				MediaPlayer player = players.get(i);
				player.release();
				player = null;
			}		
		}
	}

}