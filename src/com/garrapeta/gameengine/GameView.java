package com.garrapeta.gameengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.garrapeta.gameengine.utils.LogX;

/**
 * Vista sobre la que se renderiza el juego
 * 
 * @author GaRRaPeTa
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // -----------------------------------------------------------------
    // Constants

    public static final String TAG = GameWorld.TAG_GAME_ENGINE + ".gameView";

    // -----------------------------------------------------------------
    // Variables

    private GameWorld mWorld;

    /**
     * Holder del la SurfaceView
     */
    private SurfaceHolder mHolder;

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

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusableInTouchMode(true);
        requestFocus();
    }

    // ----------------------------------------------------------- métodos
    // propios

    /**
     * @param gameWorld
     */
    final void setGameWorld(GameWorld gameWorld) {
        mWorld = gameWorld;
    }

    /**
     * Pinta el mundo sobre la pantalla
     */
    @SuppressLint("WrongCall")
    final void draw() {
        if (mHolder != null) {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                onDraw(canvas);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWorld != null) {
            mWorld.doDrawWorld(canvas);
        }
    }

    @Override
    public final void surfaceCreated(SurfaceHolder holder) {
        LogX.i(TAG, "surfaceCreated (" + getWidth() + ", " + getHeight() + ")");
        // Draw the view when it's been created, to avoid a black screen
        draw();
    }

    @Override
    public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogX.i(TAG, "surfaceChanged (" + width + ", " + height + ")");
        if (mWorld != null) {
            mWorld.gameViewSizeChanged(this, width, height);
        }
    }

    @Override
    public final void surfaceDestroyed(SurfaceHolder holder) {
        LogX.i(TAG, "surfaceDestroyed");
    }

}
