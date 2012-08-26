package net.garrapeta.gameengine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Vista sobre la que se renderiza el juego
 * 
 * @author GaRRaPeTa
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // -----------------------------------------------------------------
    // Constants

    public static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".gameView";

    // -----------------------------------------------------------------
    // Variables
    /**
     * Holder del la SurfaceView
     */
    private SurfaceHolder holder;

    private GameWorld world;

    // private boolean waitingForDrawingDispatched = false;

    // ---------------------------------------------------------------
    // Constructor

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.holder = getHolder();
        this.holder.addCallback(this);

        setFocusableInTouchMode(true);
        requestFocus();
    }

    // ----------------------------------------------------------- M�todos
    // propios

    /**
     * @param gameWorld
     */
    final void setGameWorld(GameWorld gameWorld) {
        this.world = gameWorld;
    }

    /**
     * Pinta el mundo sobre la pantalla
     */
    final void draw() {
        if (holder != null) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                // pintado del frame
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        world.doDrawWorld(canvas);
    }

    // ------------------------------------------ M�todos de
    // SurfaceHolder.Callback

    @Override
    public final void surfaceCreated(SurfaceHolder holder) {
        Log.i(LOG_SRC, "surfaceCreated (" + getWidth() + ", " + getHeight() + ")");
        // Draw the view when it's been created, to avoid a black screen
        draw();
    }

    @Override
    public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(LOG_SRC, "surfaceChanged (" + width + ", " + height + ")");
        world.gameViewSizeChanged(width, height);
    }

    @Override
    public final void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(LOG_SRC, "surfaceDestroyed");
    }

}
