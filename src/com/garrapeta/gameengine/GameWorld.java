package com.garrapeta.gameengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;

import com.garrapeta.gameengine.module.SoundModule;
import com.garrapeta.gameengine.module.VibrationModule;
import com.garrapeta.gameengine.utils.L;

/**
 * Clase que representa un universo de juego.
 * 
 * @author GaRRaPeTa
 */
public abstract class GameWorld {

    // -------------------------------------------------------------- Constantes
    /** Frames por segundo por defecto */
    private static final float DEFAULT_FPS = 36f;

    /** Source trazas de log */
    public static final String TAG_GAME_ENGINE = "game";

    /** Source trazas de log */
    public static final String TAG = TAG_GAME_ENGINE + ".world";

    /** Nombre del thread del game loop */
    public static final String LOOP_THREAD_NAME = "gameLoop";

    // --------------------------------------------------------------- Variables

    /** SurfaceView donde se renderiza el juego. */
    public GameView mGameView;

    /** Viewport activo */
    public Viewport mViewport;

    /** Hilo con el loop principal */
    private Thread mGameLoopThread;

    /** Actores del juego */
    public Vector<Actor<?>> mActors;

    private final List<GameMessage> mMessages;
    
    private final List<GameMessage> mMessagesAux;

    /** Frames por segundo que se intentan conseguir */
    private float mFps; //

    /** Milisegundos por frame (periodo) que se intentan conseguir */
    private float mspf;

    /** FPS achieved in last frame */
    private float mCurrentFps;

    /** If the game loop is running */
    private boolean mRunning = false;

    /** If the game loop is pause */
    private boolean mPaused = false;

    /** Paint usado para info de debug */
    Paint mDebugPaint;

    /** Ms que el mundo del juego a avanzado */
    private long mCurrentGameMillis;

    /** Si pintar la info de FPS, etc **/
    private boolean mDrawDebugInfo = false;

    /** Bitmap manager user by the world */
    private BitmapManager mBitmapManager;

    /** Sound manager user by the world */
    private SoundModule mSoundModule;
    
    /** Vibrator manager user by the world */
    private VibrationModule mVibrationModule;
    
    private final ThreadPoolExecutor mAsyncMessagesExecutor;

    // --------------------------------------------------------------
    // Constructor

    /**
     * Constructor
     * 
     * @param view
     * @param context 
     * @param soundLevel 
     * @param vibratorLevel 
     */
    public GameWorld(GameView view, Context context, short soundLevel, short vibratorLevel) {
        mViewport = new Viewport(this, context.getResources().getDisplayMetrics());

        mCurrentGameMillis = 0;

        mActors = new Vector<Actor<?>>();

        mMessages = new ArrayList<GameMessage>();
        mMessagesAux = new ArrayList<GameMessage>();

        setFPS(DEFAULT_FPS);

        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.RED);

        mGameLoopThread = new Thread(new GameLoopRunnable(), LOOP_THREAD_NAME);

        mAsyncMessagesExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        mBitmapManager = new BitmapManager();
        mSoundModule = new SoundModule(context, soundLevel);
        mVibrationModule = new VibrationModule(context, vibratorLevel);
        mGameView = view;
        view.setGameWorld(this);
    }

    // -------------------------------------------------------- Getters y
    // Setters

    /**
     * @return the BitmapManager
     */
    public final BitmapManager getBitmapManager() {
        return mBitmapManager;
    }

    /**
     * @return the SoundManager
     */
    public final SoundModule getSoundManager() {
        return mSoundModule;
    }

    /**
     * @return the VibratorManager
     */
    public final VibrationModule getVibratorManager() {
        return mVibrationModule;
    }
    
    /**
     * @param fPS
     *            the fPS to set
     */
    public void setFPS(float fps) {
        this.mFps = fps;
        mspf = 1000 / fps;
    }

    /**
     * @return the fPS
     */
    public float getFPS() {
        return mFps;
    }

    /**
     * @return the mspf
     */
    public float getMspf() {
        return mspf;
    }

    /**
     * @return los ms que el del juego ha avanzado
     */
    public long currentGameMillis() {
        return mCurrentGameMillis;
    }

    /**
     * @return the printDebugInfo
     */
    public boolean isDrawDebugInfo() {
        return mDrawDebugInfo;
    }

    /**
     * @param printDebugInfo
     *            the printDebugInfo to set
     */
    public void setDrawDebugInfo(boolean printDebugInfo) {
        mDrawDebugInfo = printDebugInfo;
    }

    // ---------------------------------------- métodos relativos al ciclo de
    // vida

    /**
     * Starts running the game loop
     */
    public void start() {
        if (L.sEnabled) Log.i(TAG, "Start running...");
        mRunning = true;
        if (!mGameLoopThread.isAlive()) {
            mGameLoopThread.start();
        }
    }

    /**
     * Stops running the game loop. This method blocks until the game loop is
     * finished.
     */
    public final void finish() {
        if (L.sEnabled) Log.i(TAG, "Stop running...");
        mRunning = false;
        // Interrupt the thread, in case it was paused
        mGameLoopThread.interrupt();
    }

    /**
     * If the game loop is running
     */
    public final boolean isRunning() {
        return mRunning;
    }

    /**
     * Pauses the game loop
     */
    public final void pause() {
        if (L.sEnabled) Log.i(TAG, "Pausing...");
        synchronized (mGameLoopThread) {
            mPaused = true;
        }
    }

    /**
     * Resumes the game loop
     */
    public final void resume() {
        // TODO: IllegalState is if not paused
        if (L.sEnabled) Log.i(TAG, "Resuming...");
        synchronized (mGameLoopThread) {
            mPaused = false;
            mGameLoopThread.notify();
        }
    }

    /**
     * Notified when the game is paused.
     */
    private final void onPaused() {
        // TODO: not only pause, but release the resources of the soundManager
    	mSoundModule.pauseAll();
    }

    /**
     * Notified when the game is resumed.
     */
    private final void onResumed() {
        mSoundModule.resumeAll();
    }

    /**
     * If the game loop is paused
     */
    public final boolean isPaused() {
        return mPaused;
    }

    // ----------------------------------- Game logic

    public void post(GameMessage message) {
        post(message, 0);
    }

    public void post(GameMessage message, float delay) {
    	if (mRunning) {
    		message.setDelay(delay);
    		message.onPosted(this);
    	}
    }

    void executeAsynchronously(Runnable runnable) {
    	mAsyncMessagesExecutor.execute(runnable);
    }
   
    void add(GameMessage message) {
        synchronized (mMessages) {
            int index = 0;
            for (GameMessage aux : mMessages) {
                if (aux.getPriority() > message.getPriority()) {
                    break;
                }
                index++;
            }
            mMessages.add(index, message);
        }
    }

    private void processMessages(float lastFrameLength) {
    	// collect the ones that are ready
    	synchronized (mMessages) {
    		int index = 0;
            while (index < mMessages.size()) {
            	final GameMessage message = mMessages.get(index);
	            if (message.isReadyToBeDispatched(lastFrameLength)) {
	            	mMessagesAux.add(message);
	            	mMessages.remove(message);
	            } else {
	            	index++;
	            }
            }
        }
    	
    	// process the ones that are ready
        if (!mMessagesAux.isEmpty()) {
	    	for (GameMessage message : mMessagesAux) {
	        	message.doInGameLoop(this);
	        }
	        mMessagesAux.clear();
        }

    }

    /**
     * Adds an actor
     * 
     * @param actor
     * 
     * @throws IllegalStateException if the actor is not initilialised
     */
    public final void addActor(final Actor<?> actor) {
        if (L.sEnabled) Log.d(TAG, "GameWorld.addActor(" + actor + "). Thread: " + Thread.currentThread().getName());

        actor.assertInnited();

        // TODO: let adding an actor in same frame?
        // if user catches MotionEvent, post a message to handle it, and it
        // there post the adition of
        // one actor, one cycle is lost
        post(new SyncGameMessage(GameMessage.MESSAGE_PRIORITY_MAX) {
            @Override
            public void doInGameLoop(GameWorld world) {
                int length = mActors.size();
                int index = length;

                while (index > 0) {
                    Actor<?> aux = mActors.get(index - 1);
                    if (aux.getZIndex() <= actor.getZIndex()) {
                        break;
                    }
                    index--;
                }

                mActors.add(index, actor);
                onActorAdded(actor);
                actor.doOnAddedToWorld();
            }
        });
    }

    public void onActorAdded(Actor<?> actor) {
    }

    public final void removeActor(final Actor<?> actor) {
        if (L.sEnabled) Log.d(TAG, "GameWorld.removeActor(" + actor + "). Thread: " + Thread.currentThread().getName());
        post(new SyncGameMessage(GameMessage.MESSAGE_PRIORITY_MAX) {
            @Override
            public void doInGameLoop(GameWorld world) {
                if (mActors.contains(actor)) {
                    onActorRemoved(actor);
                    actor.doOnRemovedFromWorld();
                    mActors.remove(actor);
                }
            }
        });
    }

    public void onActorRemoved(Actor<?> actor) {
    }

    /**
     * Remove all the actors
     * 
     * @param actor
     */
    // TODO message for this?
    public void removeAllActors() {
        for (Actor<?>actor : mActors) {
            removeActor(actor);
        }
    }

    /**
     * C�digo ejecutado para procesar la l�gica del juego en cada frame
     * 
     * @param lastFrameLength
     *            tiempo que dur� el frame anterior, en ms
     */
    public boolean processFrame(float lastFrameLength) {
        return false;
    }

    /**
     * 
     */
    protected void loadResources() {
    }

    /**
     * Called from the GameLoop when this has been created
     */
    protected void onBeforeRunning() {
    }

    // ---------------------------------------------- métodos relativos al
    // pintado

    final void doDrawWorld(Canvas canvas) {
        if (L.sEnabled) Log.v(TAG, "Drawing frame");

        // pintado del mundo
        drawWorld(canvas);

        // pintado de debug
        if (mDrawDebugInfo) {
            drawDebugInfo(canvas, mDebugPaint);
        }
    }

    /**
     * C�digo ejecutado para pintar el mundo en cada frame
     * 
     * @param canvas
     */
    public final void drawWorld(Canvas canvas) {
        drawBackground(canvas);
        drawActors(canvas);
    }

    /**
     * Pinta el background
     * 
     * @param canvas
     */
    protected void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        mViewport.drawBoundaries(canvas, mDebugPaint);
    }

    protected void drawActors(Canvas canvas) {
        int l = mActors.size();
        for (int i = 0; i < l; i++) {
            Actor<?> actor = mActors.elementAt(i);
            actor.draw(canvas);
        }
    }

    /**
     * Pinta informaci�n de debug
     * 
     * @param canvas
     * @param debugPaint
     */
    private final void drawDebugInfo(Canvas canvas, Paint debugPaint) {
        // se pintan los FPS actuales
        debugPaint.setTextAlign(Align.RIGHT);
        canvas.drawText(getDebugString(), mGameView.getWidth(), mGameView.getHeight() - 20, debugPaint);
    }

    protected String getDebugString() {
        return (int) mCurrentFps + " FPS";
    }

    /**
     * Stops the game loop and disposes the world. This method does not block
     * until the world is disposed.
     */
    protected  void dispose() {
        if (L.sEnabled) Log.i(TAG, "GameWorld.dispose()");

        synchronized (this) {
        	mAsyncMessagesExecutor.shutdownNow();
        }

        mViewport.dispose();
        for (Actor<?> actor : mActors) {
            actor.dispose();
        }
        mActors.clear();
        mMessages.clear();
        mMessagesAux.clear();
        mBitmapManager.releaseAll();
        mSoundModule.releaseAll();
        mVibrationModule.releaseAll();

    }

    // ---------------------------------- métodos relativos a la interacci�n

    final void gameViewSizeChanged(GameView gameView, int width, int height) {
        if (L.sEnabled) Log.i(TAG, "surfaceChanged (" + width + ", " + height + ")");
        onGameViewSizeChanged(width, height);
        mViewport.gameViewSizeChanged(gameView, width, height);
    }

    /**
     * Invoked when the size of game view changes
     * 
     * @param width, in pixels
     * @param height, in pixels
     */
    public abstract void onGameViewSizeChanged(int width, int height);

    /**
     * Invoked when the size of the viewport changes
     * @param worldBoundaries
     */
    public abstract void onGameWorldSizeChanged(RectF worldBoundaries);

    void doProcessFrame(float lastFrameLength) {
        processMessages(lastFrameLength);
        if (!processFrame(lastFrameLength)) {
            // TODO: do this with iterator
            int size = mActors.size();
            for (int i = 0; i < size; i++) {
                mActors.get(i).processFrame(mspf);
            }
        }
    }

    void checkExecutedInGameLoopThread() {
        if (Thread.currentThread() != mGameLoopThread) {
            throw new IllegalStateException("This operation needs to be executed in the game loop thread");
        }
    }
    
    /**
     * Method that can be overriden to receive errors that can happen in the 
     * processing of the games
     * </p>
     * Remember to dispose the world after receiving this event.
     * 
     * @param Throwable error
     */
    public void onError(Throwable error) {
    }

    // -------------------------------------- Clases internas

    /**
     * Runnable del GameLoop
     * 
     * @author GaRRaPeTa
     */
    class GameLoopRunnable implements Runnable {

        @Override
        public void run() {
        	try {
	            if (L.sEnabled) Log.i(TAG, "Game loop thread started. Thread: " + Thread.currentThread().getName());
	
	            float lastFrameLength = 0;
	
	            loadResources();
	            onBeforeRunning();
	
	            while (mRunning) {
	
	                long begin = System.currentTimeMillis();
	                doProcessFrame(lastFrameLength);
	                mGameView.draw();
	
	                long end = System.currentTimeMillis();
	                long elapsed = end - begin;
	
	                long diff = (long) (mspf - elapsed);
	                if (diff > 0) {
	                    try {
	                        Thread.sleep(diff);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	
	                }
	                lastFrameLength = System.currentTimeMillis() - begin;
	                mCurrentGameMillis += lastFrameLength;
	                mCurrentFps = 1000 / lastFrameLength;
	                if (L.sEnabled) Log.v(TAG, "Game loop frame. Desired FPS: " + mFps + " Actual: " + mCurrentFps);
	                Thread.yield();
	
	                synchronized (mGameLoopThread) {
	                    if (mPaused) {
	                        if (L.sEnabled) Log.d(TAG, "Game loop paused.");
	                        onPaused();
	                        try {
	                            mGameLoopThread.wait();
	                            if (L.sEnabled) Log.v(TAG, "Game loop resumed.");
	                            onResumed();
	                        } catch (InterruptedException e) {
	                        }
	                    }
	                }
	            }
	            
	            dispose();
	            if (L.sEnabled) Log.i(TAG, "Game loop thread ended");
	            
    	    } catch (Throwable t) {
    	    	if (L.sEnabled) Log.e(TAG, "Error happenend in the game loop", t);
    	    	onError(t);
    	    }
       }
    }

}
