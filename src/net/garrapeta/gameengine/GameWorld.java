package net.garrapeta.gameengine;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Clase que representa un universo de juego.
 * 
 * @author GaRRaPeTa
 */
public abstract class GameWorld {

	// -------------------------------------------------------------- Constantes
	/** Frames por segundo por defecto */
	private static final float DEFAULT_FPS = 33; 
	
	/** Factor de aceleración de tiempo por defecto */
	private static final float DEFAULT_TIME_FACTOR = 1; 
	
	/** Source trazas de log */
	public static final String LOG_SRC = "world";
	
	/** Nombre del thread del game loop	 */
	public static final String LOOP_THREAD_NAME = "gameLoop";
	
	// --------------------------------------------------------------- Variables
	
	/** Actividad padre */
	protected Activity activity;
	
	/** SurfaceView donde se renderiza el juego. */
	public GameView view;
	
	/** Viewport activo */
	public Viewport viewport;
	
	/** Hilo con el loop principal */
	private Thread loopThread;

	/** Actores del juego */
	public Vector<Actor> actors; 
	
	/**
	 *  Actores marcados para eliminación.
	 *  En el próximo frame estos actores serán destruídos y eliminados del juego.
	 */
	private Vector<Actor> markedForRemovalActors;

	/** Frames por segundo que se intentan conseguir */
	private float fps;						//
	
	/** Milisegundos por frame (periodo) que se intentan conseguir */
	private float mspf; 
	
	/** Milisegundos que duró el último frame */
	protected float frameTime;				
	
	/** Factor de aceleración de tiempo. Sirve para crear "efecto bala" y "fastforward" */
	protected float timeFactor;
	
	/** Si el game loop está corriendo */
	protected boolean running;
	
	/**  Si se está procesando la lógica del juego */
	protected boolean playing;
	
	/** Paint usado para info de debug */
	protected Paint debugPaint;
	
	/** Ms que el mundo del juego a avanzado */
	private long currentGameMillis;
	
	/** Si pintar la info de FPS, etc **/
	private boolean drawDebugInfo = false;
	
	// -------------------------------------------------------------- Constructor
	
	/**
	 *  Constructor privado
	 */
	private GameWorld(Activity activity) {
		this.activity = activity;
		viewport = new Viewport(this);
		
		currentGameMillis = 0;
		
		actors 					= new Vector<Actor>();
		markedForRemovalActors 	= new Vector<Actor>();
		
		setFPS(DEFAULT_FPS);
		
		setTimeFactor(DEFAULT_TIME_FACTOR);
		
		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);

		loopThread = new Thread(new GameLoopRunnable(), LOOP_THREAD_NAME);
	}
	
	/**
	 * Constructor 
	 * @param view vista del juego
	 */
	public GameWorld(Activity activity, GameView view) {
		this(activity);
		this.view = view;
		view.setGameWorld(this);
	}

	// -------------------------------------------------------- Getters y Setters
	
	/**
	 * @return la actividad padre
	 */
	public Activity getActivity() {
		return activity;
	}
	
	/**
	 * @param fPS the fPS to set
	 */
	public void setFPS(float fps) {
		this.fps = fps;
		mspf = 1000 / fps;
	}

	/**
	 * @return the fPS
	 */
	public float getFPS() {
		return fps;
	}

	/**
	 * @return the mspf
	 */
	public float getMspf() {
		return mspf;
	}

	/**
	 * @return the frameTime
	 */
	public float getFrameTime() {
		return frameTime;
	}
	
	public float getTimeFactor() {
		return timeFactor;
	}

	public void setTimeFactor(float timeFactor) {
		this.timeFactor = timeFactor;
	}
	
    /**
     * @return los ms que el del juego ha avanzado
     */
    public long currentGameMillis() {
    	return currentGameMillis;
    }
    
	/**
	 * @return iterador a los actores
	 */
	public synchronized Iterator<Actor> actors() {
		return actors.iterator();
	}

	/**
	 * @return the printDebugInfo
	 */
	public boolean isDrawDebugInfo() {
		return drawDebugInfo;
	}

	/**
	 * @param printDebugInfo the printDebugInfo to set
	 */
	public void setDrawDebugInfo(boolean printDebugInfo) {
		this.drawDebugInfo = printDebugInfo;
	}
	
	// ---------------------------------------- Métodos relativos al ciclo de vida 

	/**
	 * Establece las dimensiones de la pantalla
	 * @param activity
	 * @param width
	 * @param height
	 */
	public synchronized void surfaceChanged(float width, float height) {
		viewport.onGameViewSizeChanged(width, height);
	}
	
	/**
	 *  Inicia el game loop
	 */
	public void startLooping() {
		running = true;
		if (!loopThread.isAlive()) {
			loopThread.start();
		}
	}

	/** 
	 * Detiene y cancela el game loop
	 */
	public void stopLooping() {
		running = false;
	}
	
	/**
	 *  Comienza a procesar la lógica del juego 
	 */
	public void play() {
		this.playing = true;
	}

	/**
	 * @return si se está procesando lógica del juego 
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 *  Pausa el proceso de lógica del juego 
	 */
	public void pause() {
		this.playing = false;
	}
	
	
	
	// ----------------------------------- Métodos relativos a la lógica del juego

	/**
	 * Añade un actor
	 * @param actor
	 */
	public synchronized void addActor(Actor actor) {
		Log.d(LOG_SRC, "GameWorld.addActor(" + actor + "). Thread: " + Thread.currentThread().getName());
		addInOrder(actor);
	}
	
	/**
	 * Añade un actor justo detrás del actor especificado
	 * @param actor
	 */
	public synchronized void addActorAfter(Actor newActor, Actor existingActor) {
		Log.d(LOG_SRC, "GameWorld.addActorAfter(" + newActor + "). Thread: " + Thread.currentThread().getName());
		int index = actors.indexOf(existingActor);
		if (index == -1) {
			throw new IllegalArgumentException("Actor not in actor list: " + existingActor);
		}
		actors.insertElementAt(newActor, index+1);
	}
	
	/**
	 * Método invocado cuando el z-index de un actor cambia
	 * @param actor
	 */
	synchronized void onZindexChanged(Actor actor) {
		if (actors.contains(actor)) {
			actors.remove(actor);
			addInOrder(actor);
		}
	}
	
	/**
	 * Añade un actor ordenadamente
	 * @param actor
	 */
	private synchronized void addInOrder(Actor actor) {
		
		int length = actors.size();
		int index = length;
		
		while (index > 0) {
			Actor aux = actors.get(index - 1);
			if (aux.getZIndex() <= actor.getZIndex()) {
				break;
			}
			index--;
		}
		
		actors.add(index, actor);
	}
	
	/**
	 * Elimina un actor
	 * @param actor
	 */
	public synchronized void removeActor(Actor actor) {
		if (!markedForRemovalActors.contains(actor)) {
			Log.d(LOG_SRC, "GameWorld.removeActor(" + actor + "). Thread: " + Thread.currentThread().getName());
			markedForRemovalActors.add(actor);
		}
	}

	/**
	 * Código ejecutado al eliminar un actor
	 * @param actor
	 */
	protected synchronized void onRemoveFromWorld(Actor actor) {
		Log.d(LOG_SRC, "GameWorld.onRemoveFromWorld(" + actor + "). Thread: " + Thread.currentThread().getName());
		actors.remove(actor);
	}
	
	/**
	 * Código ejecutado para procesar la lógica del juego en cada frame
	 * @param gameTimeStep tiempo que duró el frame anterior, en ms
	 */
	public abstract void processFrame(float gameTimeStep);
	
	/**
	 * Código ejecutado antes de procesar la lógica del juego en cada frame
	 * @param stepTime tiempo del frame anterior, en ms
	 */
	public synchronized void preProcessFrame() {
		// Se destruyen actores marcados como muertos
		for (int i = 0; i < markedForRemovalActors.size(); i++) {
			onRemoveFromWorld(markedForRemovalActors.elementAt(i));
		}
		markedForRemovalActors.removeAllElements();
	}
	
	
	// ---------------------------------------------- Métodos relativos al pintado
	
	
	/**
	 * Pinta un frame
	 * @param canvas
	 */
	protected final synchronized void drawFrame(Canvas canvas) {
		Log.v(LOG_SRC, "Drawing frame");
		// pintado del fondo
		drawBackground(canvas);
		
    	canvas.save();
    	float tx = viewport.worldUnitsToPixels(viewport.offsetX);
    	float ty = viewport.worldUnitsToPixels(viewport.offsetY);

    	canvas.translate(tx, ty);
    	
    	// pintado del mundo
    	drawWorld(canvas);
    	
    	canvas.restore();
    	
		// pintado de debug
    	if (drawDebugInfo) {
    		drawDebugInfo(canvas, debugPaint);
    	}
	}
	
	/**
	 * Código ejecutado para pintar el mundo en cada frame
	 * @param canvas
	 */
	protected synchronized void drawWorld(Canvas canvas) {
		// pintado de los actores
		drawActors(canvas);
	}
	
	/**
	 * Pinta a los actores
	 * @param canvas
	 */
	public synchronized void drawActors(Canvas canvas) {
//		Log.v(LOG_SRC, "GameWorld.drawActors(). Thread: " + Thread.currentThread().getName());
		// actores
		int l = actors.size();
		for (int i = 0; i < l; i++) {
			Actor actor = actors.elementAt(i);
			actor.draw(canvas);
		}
	}
	
	/**
	 * Pinta el background
	 * @param canvas
	 */
	protected void drawBackground(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
	}
	
	/**
	 * Pinta información de debug
	 * @param canvas
	 * @param debugPaint
	 */
	protected void drawDebugInfo(Canvas canvas, Paint debugPaint) {
		// se pintan los FPS actuales
		debugPaint.setTextAlign(Align.RIGHT);
		canvas.drawText((1000 / frameTime) +" FPS" , view.getWidth(), 20, debugPaint);
	}
	
	/**
	 *  Finaliza el mundo
	 */
	public void dispose() {
		Log.i(LOG_SRC, "GameWorld.dispose()");
		if (loopThread != null && loopThread.isAlive()) {
			synchronized (loopThread) {
				try {
					loopThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// TODO: dispose del mundo
	}
	
	// ---------------------------------- Métodos relativos a la interacción
	
	/**
	 * Llamado cuando el usuario realiza acción táctil sobre pantalla
	 * @param event
	 */
	public void onTouchEvent(MotionEvent event) {
		checkPressedActors(event);
	}
	
	/**
	 * Recorres los actores comprobando cuáles pueden estar pesionados
	 * @param event
	 */
	final synchronized void checkPressedActors(MotionEvent event) {
		

		for (int i = 0; i < actors.size(); i++) {
			Actor actor = actors.elementAt(i);
			boolean wasPressed = actor.isPressed();
			boolean isPressed  = actor.checkPressed(event);
		
			if (wasPressed) {
				if (!isPressed) {
					//levantado
					actor.setPressed(false);
				}
			} else if (isPressed) {
				//presionado
				actor.setPressed(true);
			}
		}
			

	}
	
	// -------------------- Métodos relativos a unidades lógicas / pantalla

	public int inchesToPixels(float inches) {
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		// los píxeles por pulgada pueden ser diferentes en la horizontal / vertical
		// hacemos la media
		float scale = (dm.xdpi + dm.ydpi) / 2;
		return (int) (scale * inches);
	}
	
	public int dpToPixels(float dp) {
		// Get the screen's density scale
		final float scale = activity.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dp * scale + 0.5f);
	}
	
	// -------------------------------------- Clases internas
	
	/**
	 * Runnable del GameLoop
	 * @author GaRRaPeTa
	 */
	class GameLoopRunnable implements Runnable {
		
		@Override
		public void run() {
			Log.i(LOG_SRC, "Game loop started. Thread: " + Thread.currentThread().getName());

			while (running) {
				long prevTimeStamp = System.currentTimeMillis();
				
				if (playing) {
					synchronized (GameWorld.this) {
						// tareas de antes de procesar el frame
						preProcessFrame();
						
						// procesamiento del frame
						float gameTimeStep = frameTime * timeFactor;
						processFrame(gameTimeStep);
						
						// lógica de los actores
						int size = actors.size();
						for (int i = 0; i< size; i++) {
							actors.get(i).doLogic(gameTimeStep);
						}
						currentGameMillis += gameTimeStep;
						// pintado
						view.draw();
					}
				}
				
				long diff = (long) mspf - (System.currentTimeMillis() - prevTimeStamp); 
				if (diff > 0) {
					try {
						Thread.sleep(diff);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				frameTime = System.currentTimeMillis() - prevTimeStamp;

				if (playing) {
					Log.v(LOG_SRC, "Frame time step: " + frameTime + " FPS:" + (1000 / frameTime));
				}
				
				Thread.yield();
			}
			
			synchronized (loopThread) {
				loopThread.notify();
			}
			
			Log.i(LOG_SRC, "Game loop ended");
		}
		
	}



}
