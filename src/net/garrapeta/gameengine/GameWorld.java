package net.garrapeta.gameengine;

import java.util.Vector;

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
     * Actores marcados para eliminaci�n. En el pr�ximo frame estos actores
     * ser�n destru�dos y eliminados del juego.
     */
    private Vector<Actor> markedForRemovalActors;

    /** Frames por segundo que se intentan conseguir */
    private float mFps; //

    /** Milisegundos por frame (periodo) que se intentan conseguir */
    private float mspf;

    /** FPS achieved in last frame */
    private float mCurrentFps;

    /** Si el game loop est� corriendo */
    protected boolean running;

    /** Si se est� procesando la l�gica del juego */
    protected boolean playing;

    /** Paint usado para info de debug */
    protected Paint debugPaint;

    /** Ms que el mundo del juego a avanzado */
    private long mCurrentGameMillis;

    /** Si pintar la info de FPS, etc **/
    private boolean drawDebugInfo = false;

    // --------------------------------------------------------------
    // Constructor

    /**
     * Constructor privado
     */
    private GameWorld(Activity activity) {
        this.activity = activity;
        viewport = new Viewport(this);

        mCurrentGameMillis = 0;

        actors = new Vector<Actor>();
        markedForRemovalActors = new Vector<Actor>();

        setFPS(DEFAULT_FPS);

        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);

        loopThread = new Thread(new GameLoopRunnable(), LOOP_THREAD_NAME);
    }

    /**
     * Constructor
     * 
     * @param view
     *            vista del juego
     */
    public GameWorld(Activity activity, GameView view) {
        this(activity);
        this.view = view;
        view.setGameWorld(this);
    }

    // -------------------------------------------------------- Getters y
    // Setters

    /**
     * @return la actividad padre
     */
    public Activity getActivity() {
        return activity;
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
     * Inicia el game loop
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
     * Comienza a procesar la l�gica del juego
     */
    public void play() {
        this.playing = true;
    }

    /**
     * @return si se est� procesando l�gica del juego
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Pausa el proceso de l�gica del juego
     */
    public void pause() {
        this.playing = false;
    }

    // ----------------------------------- M�todos relativos a la l�gica del
    // juego

    /**
     * A�ade un actor
     * 
     * @param actor
     */
    public synchronized void addActor(Actor actor) {
        Log.d(LOG_SRC, "GameWorld.addActor(" + actor + "). Thread: " + Thread.currentThread().getName());
        addInOrder(actor);
    }

    /**
     * A�ade un actor justo detr�s del actor especificado
     * 
     * @param actor
     */
    public synchronized void addActorAfter(Actor newActor, Actor existingActor) {
        Log.d(LOG_SRC, "GameWorld.addActorAfter(" + newActor + "). Thread: " + Thread.currentThread().getName());
        int index = actors.indexOf(existingActor);
        if (index == -1) {
            throw new IllegalArgumentException("Actor not in actor list: " + existingActor);
        }
        actors.insertElementAt(newActor, index + 1);
    }

    /**
     * M�todo invocado cuando el z-index de un actor cambia
     * 
     * @param actor
     */
    synchronized void onZindexChanged(Actor actor) {
        if (actors.contains(actor)) {
            actors.remove(actor);
            addInOrder(actor);
        }
    }

    /**
     * A�ade un actor ordenadamente
     * 
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
     * 
     * @param actor
     */
    public synchronized void removeActor(Actor actor) {
        if (!markedForRemovalActors.contains(actor)) {
            Log.d(LOG_SRC, "GameWorld.removeActor(" + actor + "). Thread: " + Thread.currentThread().getName());
            markedForRemovalActors.add(actor);
        }
    }

    /**
     * Remove all the actors
     * 
     * @param actor
     */
    public synchronized void removeAllActors() {
        for (Actor actor : actors) {
            removeActor(actor);
        }
    }

    /**
     * C�digo ejecutado al eliminar un actor
     * 
     * @param actor
     */
    protected synchronized void onRemoveFromWorld(Actor actor) {
        Log.d(LOG_SRC, "GameWorld.onRemoveFromWorld(" + actor + "). Thread: " + Thread.currentThread().getName());
        actors.remove(actor);
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
     * C�digo ejecutado antes de procesar la l�gica del juego en cada frame
     * 
     * @param stepTime
     *            tiempo del frame anterior, en ms
     */
    public synchronized void preProcessFrame() {
        // Se destruyen actores marcados como muertos
        for (int i = 0; i < markedForRemovalActors.size(); i++) {
            onRemoveFromWorld(markedForRemovalActors.elementAt(i));
        }
        markedForRemovalActors.removeAllElements();
    }

    // ---------------------------------------------- M�todos relativos al
    // pintado

    final void doDrawWorld(Canvas canvas) {
        Log.v(LOG_SRC, "Drawing frame");

        // pintado del mundo
        drawWorld(canvas);

        // pintado de debug
        if (drawDebugInfo) {
            drawDebugInfo(canvas, debugPaint);
        }
    }

    
    /**
     * C�digo ejecutado para pintar el mundo en cada frame
     * 
     * @param canvas
     */
    protected synchronized void drawWorld(Canvas canvas) {
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
        viewport.drawBoundaries(canvas);
    }

    protected void drawActors(Canvas canvas) {
        int l = actors.size();
        for (int i = 0; i < l; i++) {
            Actor actor = actors.elementAt(i);
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
        canvas.drawText(getDebugString(), view.getWidth(), view.getHeight() - 20, debugPaint);
    }

    protected String getDebugString() {
        return (int) mCurrentFps + " FPS";
    }

    /**
     * Finaliza el mundo
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
        if (!processFrame(lastFrameLength)) {
            preProcessFrame();
            // TODO: do this with iterator
            int size = actors.size();
            for (int i = 0; i < size; i++) {
                actors.get(i).processFrame(mspf);
            }
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

            while (running) {

                long begin = System.currentTimeMillis();
                if (playing) {
                    synchronized (GameWorld.this) {
                        doProcessFrame(lastFrameLength);
                        view.draw();
                    }
                }

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
            }

            synchronized (loopThread) {
                loopThread.notify();
            }

            Log.i(LOG_SRC, "Game loop thread ended");
        }
    }
}
