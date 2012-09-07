package net.garrapeta.gameengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.gameengine.module.SoundManager;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

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
    public static final String LOG_SRC_GAME_ENGINE = "game";

    /** Source trazas de log */
    public static final String LOG_SRC = LOG_SRC_GAME_ENGINE + ".world";

    /** Nombre del thread del game loop */
    public static final String LOOP_THREAD_NAME = "gameLoop";

    // --------------------------------------------------------------- Variables

    /** Actividad padre */
    protected Activity mActivity;

    /** SurfaceView donde se renderiza el juego. */
    public GameView mView;

    /** Viewport activo */
    public Viewport viewport;

    /** Hilo con el loop principal */
    private Thread mGameLoopThread;

    /** Actores del juego */
    public Vector<Actor> mActors;

    private List<GameMessage> mMessages;

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
    private boolean drawDebugInfo = false;

    /** Bitmap manager user by the world */
    private BitmapManager mBitmapManager;
    
    /** Sound manager user by the world */
    private SoundManager mSoundManager;

    // --------------------------------------------------------------
    // Constructor

    /**
     * Constructor privado
     */
    private GameWorld(Activity activity) {
        this.mActivity = activity;
        viewport = new Viewport(this);

        mCurrentGameMillis = 0;

        mActors = new Vector<Actor>();

        mMessages = new ArrayList<GameMessage>();

        setFPS(DEFAULT_FPS);

        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.RED);

        mGameLoopThread = new Thread(new GameLoopRunnable(), LOOP_THREAD_NAME);

        mBitmapManager = new BitmapManager(mActivity.getResources());
        mSoundManager = new SoundManager();
    }

    /**
     * Constructor
     * 
     * @param view
     *            vista del juego
     */
    public GameWorld(Activity activity, GameView view) {
        this(activity);
        mView = view;
        view.setGameWorld(this);
    }

    // -------------------------------------------------------- Getters y
    // Setters

    /**
     * @return la actividad padre
     */
    public final Activity getActivity() {
        return mActivity;
    }

    /**
     * @return the BitmaManager
     */
    public final BitmapManager getBitmapManager() {
        return mBitmapManager;
    }

    /**
     * @return the SoundManager
     */
    public final SoundManager getSoundManager() {
        return mSoundManager;
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
        return drawDebugInfo;
    }

    /**
     * @param printDebugInfo
     *            the printDebugInfo to set
     */
    public void setDrawDebugInfo(boolean printDebugInfo) {
        this.drawDebugInfo = printDebugInfo;
    }

    // ---------------------------------------- M�todos relativos al ciclo de
    // vida

    /**
     * Starts running the game loop
     */
    public void start() {
        Log.i(LOG_SRC, "Start running...");
        mRunning = true;
        if (!mGameLoopThread.isAlive()) {
            mGameLoopThread.start();
        }
    }

    /**
     * Stops running the game loop.
     * This method blocks until the game loop is finished. 
     */
    public final void finish() {
        Log.i(LOG_SRC, "Stop running...");
        mRunning = false;
        // Interrupt the thread, in case it was paused
        mGameLoopThread.interrupt();
//        synchronized (mGameLoopThread) {
//            if (mGameLoopThread.isAlive()) {
//                try {
//                    mGameLoopThread.wait();
//                } catch (InterruptedException ie) {}
//            }
//        }
    }

    /**
     * If the game loop is running
     */
    public final boolean isStarted() {
        return mRunning;
    }

    /**
     * Pauses the game loop
     */
    public final void pause() {
        Log.i(LOG_SRC, "Pausing...");
        synchronized (mGameLoopThread) {
            mPaused = true;
        }
    }

    /**
     * Resumes the game loop 
     */
    public final void resume() {
        // TODO: IllegalState is if not paused
        Log.i(LOG_SRC, "Resuming...");
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
        getSoundManager().pauseAll();
    }

    /**
     * Notified when the game is resumed.
     */
    private final void onResumed() {
        getSoundManager().resumeAll();
    }

    /**
     * If the game loop is paused
     */
    public final boolean isPaused() {
        return mPaused;
    }


    // ----------------------------------- M�todos relativos a la l�gica del
    // juego

    
    public void post(GameMessage message) {
        message.onPosted(this);
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

    private void processMessages() {
        GameMessage[] messages;
        synchronized (mMessages) {
            messages = new GameMessage[mMessages.size()];
            mMessages.toArray(messages);
            mMessages.clear();
        }
        for (GameMessage message : messages) {
            message.doInGameLoop(this);
        }
        

    }

    /**
     * A�ade un actor
     * 
     * @param actor
     */
    public final void addActor(final Actor actor) {
        Log.d(LOG_SRC, "GameWorld.addActor(" + actor + "). Thread: " + Thread.currentThread().getName());

        //TODO: let adding an actor in same frame?
        //       if user catches MotionEvent, post a message to handle it, and it there post the adition of
        //       one actor, one cycle is lost
        post(new SyncGameMessage(GameMessage.MESSAGE_PRIORITY_MAX) {
            @Override
            public void doInGameLoop(GameWorld world) {
                int length = mActors.size();
                int index = length;
        
                while (index > 0) {
                    Actor aux = mActors.get(index - 1);
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

    public void onActorAdded(Actor actor) {
    }

    public final void removeActor(final Actor actor) {
        Log.d(LOG_SRC, "GameWorld.removeActor(" + actor + "). Thread: " + Thread.currentThread().getName());
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

    public void onActorRemoved(Actor actor) {
    }

    /**
     * Remove all the actors
     * 
     * @param actor
     */
    // TODO message for this?
    public void removeAllActors() {
        for (Actor actor : mActors) {
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
     * Called from the GameLoop. Load your resources here.
     */
    protected void loadResources() {
    }

    /**
     * Called from the GameLoop when this has been created
     */
    protected void onBeforeRunning() {
    }
    
    // ---------------------------------------------- M�todos relativos al
    // pintado

    final void doDrawWorld(Canvas canvas) {
        Log.v(LOG_SRC, "Drawing frame");

        // pintado del mundo
        drawWorld(canvas);

        // pintado de debug
        if (drawDebugInfo) {
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
        viewport.drawBoundaries(canvas, mDebugPaint);
    }

    protected void drawActors(Canvas canvas) {
        int l = mActors.size();
        for (int i = 0; i < l; i++) {
            Actor actor = mActors.elementAt(i);
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
        canvas.drawText(getDebugString(), mView.getWidth(), mView.getHeight() - 20, debugPaint);
    }

    protected String getDebugString() {
        return (int) mCurrentFps + " FPS";
    }

    /**
     * Stops the game loop and disposed the world.
     * This method does not block until the world is disposed.
     */
    void dispose() {
        Log.i(LOG_SRC, "GameWorld.dispose()");
        mBitmapManager.releaseAll();
        mSoundManager.releaseAll();
    }

    // ---------------------------------- M�todos relativos a la interacci�n

    final void gameViewSizeChanged(int width, int height) {
        Log.i(LOG_SRC, "surfaceChanged (" + width + ", " + height + ")");
        onGameViewSizeChanged(width, height);
        viewport.gameViewSizeChanged(width, height);
    }

    /**
     * Invoked when the size of game view changes
     * 
     * @param width
     *            , in pixels
     * @param height
     *            , in pixels
     */
    public abstract void onGameViewSizeChanged(int width, int height);

    /**
     * Invoked when the size of the viewport changes
     */
    public abstract void onGameWorldSizeChanged();

    void doProcessFrame(float lastFrameLength) {
        processMessages();
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

    // -------------------------------------- Clases internas

    /**
     * Runnable del GameLoop
     * 
     * @author GaRRaPeTa
     */
    class GameLoopRunnable implements Runnable {

        @Override
        public void run() {
            Log.i(LOG_SRC, "Game loop thread started. Thread: " + Thread.currentThread().getName());

            float lastFrameLength = 0;

            loadResources();
            onBeforeRunning();
            
            while (mRunning) {
                
                long begin = System.currentTimeMillis();
                doProcessFrame(lastFrameLength);
                mView.draw();

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
                Log.v(LOG_SRC, "Game loop frame. Desired FPS: " + mFps + " Actual: " + mCurrentFps);
                Thread.yield();

                synchronized (mGameLoopThread) {
                    if (mPaused) {
                        Log.d(LOG_SRC, "Game loop paused.");
                        onPaused();
                        try {
                            mGameLoopThread.wait();
                            Log.v(LOG_SRC, "Game loop resumed.");
                            onResumed();
                        } catch (InterruptedException e) {}
                    }
                }
            }

            dispose();

            Log.i(LOG_SRC, "Game loop thread ended");
        }
    }
}
