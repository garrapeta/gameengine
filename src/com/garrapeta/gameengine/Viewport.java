package com.garrapeta.gameengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class Viewport {

    // -------------------------------------------------------- Constants

    public static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".viewport";
    
    
    // ------------------------------------------------------------ Types
    
    private enum ProjectionMode {EXPLICIT, FIT_WIDTH,FIT_HEIGHT};

    // ----------------------------------------------- Instance variables

    /** Associated world */
    public GameWorld mWorld;

    /**
     * Number of dps in one world unit (meters)
     */
    private float mDpsInWorldUnits;

    /** Physical width of the game view, in pixels */
    private int mViewWidth = Integer.MIN_VALUE;

    /** Physical height of the game view, in pixels */
    private int mViewHeight = Integer.MIN_VALUE;

    /** Width of the game world, in world units. */
    private float mWorldWidth;

    /** Height of the game world, in world units. */
    private float mWorldHeight;

    /** Visible world boundaries */
    private RectF mWorldBoundaries = new RectF();

    /** Current projection mode */
    private ProjectionMode mProjectionMode;
    
    /** Display metrics  */
    private DisplayMetrics mDisplayMetrics;
    // -------------------------------------------------- Static methods

    public static Vector2 pointFToVector2(PointF pf) {
        return new Vector2(pf.x, pf.y);
    }

    public static Vector2[] pointFToVector2(PointF[] pfs) {
        int l = pfs.length;
        Vector2[] v2s = new Vector2[l];

        for (int i = 0; i < l; i++) {
            v2s[i] = pointFToVector2(pfs[i]);
        }
        return v2s;
    }

    public static PointF vector2ToPointF(Vector2 v2) {
        return new PointF(v2.x, v2.y);
    }

    public static PointF[] vector2ToPointF(Vector2[] v2s) {
        int l = v2s.length;
        PointF[] pfs = new PointF[l];

        for (int i = 0; i < l; i++) {
            pfs[i] = vector2ToPointF(v2s[i]);
        }
        return pfs;
    }

    // ---------------------------------------------------- Constructor

    /**
     * @param world
     */
    public Viewport(GameWorld world, DisplayMetrics displayMetrics) {
        mWorld = world;
        mDisplayMetrics = displayMetrics;
    }

    // ------------------------------------------------ Instance methods

    public void gameViewSizeChanged(GameView gameView, int viewWidth, int viewHeight) {
        Log.i(LOG_SRC, "onGameViewSizeChanged(" + viewWidth + ", " + + viewHeight + ")");

        mViewWidth  = viewWidth;
        mViewHeight = viewHeight;
        updateWorldBoundaries();
    }

    public void setWorldSize(float width, float height, float dpsInWorldUnit) {
        setWorldSize(width, height, dpsInWorldUnit, ProjectionMode.EXPLICIT);
    }

    public void setWorldSizeAndFitWidth(float width, float height) {
        setWorldSize(width, height, Float.MIN_VALUE, ProjectionMode.FIT_WIDTH);
    }

    public void setWorldSizeAndFitHeight(float width, float height) {
        setWorldSize(width, height, Float.MIN_VALUE, ProjectionMode.FIT_HEIGHT);
    }

    public void setWorldWidth(float width, float dpsInWorldUnit) {
        setWorldSize(width, Float.MIN_VALUE, dpsInWorldUnit, ProjectionMode.EXPLICIT);
    }

    public void setWorldHeight(float height, float dpsInWorldUnit) {
        setWorldSize(Float.MIN_VALUE, height, dpsInWorldUnit, ProjectionMode.EXPLICIT);
    }

    public void setWorldWidth(float width) {
        setWorldSize(width, Float.MIN_VALUE, Float.MIN_VALUE, ProjectionMode.FIT_WIDTH);
    }
    
    public void setWorldHeight(float height) {
        setWorldSize(Float.MIN_VALUE, height, Float.MIN_VALUE, ProjectionMode.FIT_HEIGHT);
    }
    
    public void setWorldSizeGivenWorldUnitsPerInchX(float worldUnitsPerInchX) {
        float inchesX =  mDisplayMetrics.widthPixels / mDisplayMetrics.xdpi;;
        float  width = inchesX * worldUnitsPerInchX;
        setWorldWidth(width);
    }
    
    private void setWorldSize(float width, float height, float dpsInWorldUnit, ProjectionMode mode) {
        mWorldWidth  = width;
        mWorldHeight = height;
        mDpsInWorldUnits = dpsInWorldUnit;
        mProjectionMode  = mode;
        updateWorldBoundaries();
    }

    private void updateWorldBoundaries() {
        
        if (mViewWidth   != Integer.MIN_VALUE && mViewHeight != Integer.MIN_VALUE && mProjectionMode != null) {

            switch (mProjectionMode) {
            case FIT_WIDTH:
                if (mWorldWidth == Float.MIN_VALUE) {
                    throw new IllegalArgumentException("Projection mode is " + ProjectionMode.FIT_WIDTH + " but width is undefined");
                }
                mDpsInWorldUnits = pixelsToDps(mViewWidth) / mWorldWidth;
                break;
            case FIT_HEIGHT:
                if (mWorldHeight == Float.MIN_VALUE) {
                    throw new IllegalArgumentException("Projection mode is " + ProjectionMode.FIT_HEIGHT + " but height is undefined");
                }
                mDpsInWorldUnits = pixelsToDps(mViewHeight) / mWorldHeight;
                break;
            case EXPLICIT:
                break;
            }
        }

        // set of world size
        if (mWorldWidth == Float.MIN_VALUE && mDpsInWorldUnits != Float.MIN_VALUE) {
            mWorldWidth = pixelsToDps(mViewWidth) / mDpsInWorldUnits;
        }
        if (mWorldHeight == Float.MIN_VALUE && mDpsInWorldUnits != Float.MIN_VALUE) {
            mWorldHeight = pixelsToDps(mViewHeight) / mDpsInWorldUnits;
        }

        // update of boundaries
        if (mWorldWidth != Float.MIN_VALUE && mWorldHeight != Float.MIN_VALUE) {
            RectF prevWorldBoundaries = new RectF(mWorldBoundaries);

            mWorldBoundaries.left   = 0;
            mWorldBoundaries.right  = mWorldWidth;
            mWorldBoundaries.bottom = 0;
            mWorldBoundaries.top    = mWorldHeight;
            
            if (prevWorldBoundaries.left != mWorldBoundaries.left ||
                prevWorldBoundaries.right != mWorldBoundaries.right ||
                prevWorldBoundaries.top != mWorldBoundaries.top ||
                prevWorldBoundaries.bottom != mWorldBoundaries.bottom) {
                
                mWorld.onGameWorldSizeChanged(mWorldBoundaries);
            }
        }
        
    }

    /**
     * @return the worldBoundaries
     */
    public RectF getWorldBoundaries() {
        return mWorldBoundaries;
    }

    // Length conversion methods

    public float worldUnitsToPixels(float worldUnits) {
        return dpsToPixels(worldUnits * mDpsInWorldUnits);
    }

    public float pixelsToWorldUnits(float pixels) {
        return pixelsToDps(pixels / mDpsInWorldUnits);
    }

    public float pixelsToDps(float pixels) {
        return pixels / mDisplayMetrics.density;
    }

    public float dpsToPixels(float dps) {
        return dps * mDisplayMetrics.density;
    }

    // Coordinate conversion methods

    public PointF worldToScreen(PointF worldPos) {
        return worldToScreen(worldPos.x, worldPos.y);
    }

    public PointF screenToWorld(PointF screenPos) {
        return screenToWorld(screenPos.x, screenPos.y);
    }

    public PointF worldToScreen(float worldX, float worldY) {
        return new PointF(worldUnitsToPixels(worldX), mViewHeight - worldUnitsToPixels(worldY));
    }

    public PointF screenToWorld(float screenX, float screenY) {
        return new PointF(pixelsToWorldUnits(screenX), pixelsToWorldUnits(mViewHeight - screenY));
    }

        
    // ----------------------------------------------------------------------------------

    /**
     * Dibuja una cuadr�cula en pantalla
     * 
     * @param canvas
     * @param worldSpacing
     *            tama�o de las celdas en unidades del mundo (normalmente
     *            metros)
     */
    public void drawBoundaries(Canvas canvas, Paint paint) {
        // TODO: not sure if offsets are working
        float worldSpacing = 1;
        float screenSpacing = worldUnitsToPixels(worldSpacing);

        paint.setStyle(Style.STROKE);
        paint.setColor(Color.DKGRAY);

        PointF v0 = worldToScreen(mWorldBoundaries.left, mWorldBoundaries.bottom);
        PointF v1 = worldToScreen(mWorldBoundaries.right, mWorldBoundaries.top);

        float top = v0.y;
        float left = v0.x;
        float bottom = v1.y;
        float right = v1.x;

        // rayas verticales
        for (float i = left; i < right; i += screenSpacing) {
            canvas.drawLine(i, top, i, bottom, paint);
        }

        // rayas horizontales
        for (float i = bottom; i < top; i += screenSpacing) {
            canvas.drawLine(left, i, right, i, paint);
        }

    }

    /**
     * Frees resources
     */
    public void dispose() {
        mWorld = null;
        mWorldBoundaries = null;
        mDisplayMetrics = null;
    }

}
