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
import com.garrapeta.gameengine.utils.L;

public class Viewport {

    // -------------------------------------------------------- Constants

    public static final String TAG = GameWorld.TAG_GAME_ENGINE + ".viewport";
    
    
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
    
    private boolean mReverseYAxis = false;
    
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
        if (L.sEnabled) Log.i(TAG, "onGameViewSizeChanged(" + viewWidth + ", " + + viewHeight + ")");

        mViewWidth  = viewWidth;
        mViewHeight = viewHeight;
        updateWorldBoundaries();
    }

    void setReverseYAxis(boolean reverse) {
        mReverseYAxis = reverse;
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
    
    public void setWorldSizeDpsPerWorldUnit(float dpsPerWorldUnit) {
        final float worldWidth = mDisplayMetrics.widthPixels / mDisplayMetrics.density / dpsPerWorldUnit;
        final float worldHeight = mDisplayMetrics.heightPixels / mDisplayMetrics.density / dpsPerWorldUnit;
        setWorldSize(worldWidth, worldHeight, dpsPerWorldUnit);
    }
    
    public void setWorldSizeGivenWorldUnitsPerInchX(float worldUnitsPerInchX) {
  	    // Due to bug in some devices (samsung) we cannot trust mDisplayMetrics.xdpi,
    	// so we use mDisplayMetrics.densityDpi, which is more reliable albeit less accurate
  	    // https://groups.google.com/forum/#!topic/android-developers/g56jV0Hora0
    	
  	    //final float xPixelsPerInch = mDisplayMetrics.xdpi;
    	final float xPixelsPerInch = mDisplayMetrics.densityDpi;
    	
    	final float widthPixels = mDisplayMetrics.widthPixels;

    	final float inchesX =  widthPixels / xPixelsPerInch;
  	    final float  worldWidth = inchesX * worldUnitsPerInchX;
        
  	    setWorldWidth(worldWidth);
    }
    
    public void setWorldSizeGivenWorldUnitsPerInchY(float worldUnitsPerInchY) {
  	    // Due to bug in some devices (samsung) we cannot trust mDisplayMetrics.ydpi,
    	// so we use mDisplayMetrics.densityDpi, which is more reliable albeit less accurate
  	    // https://groups.google.com/forum/#!topic/android-developers/g56jV0Hora0    	

  	    //final float xPixelsPerInch = mDisplayMetrics.ydpi;
    	final float yPixelsPerInch = mDisplayMetrics.densityDpi;
    	
    	final float heightPixels = mDisplayMetrics.heightPixels;

    	final float inchesY =  heightPixels / yPixelsPerInch;
  	    final float  worldHeight = inchesY * worldUnitsPerInchY;
        
  	    setWorldHeight(worldHeight);
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
            
            if (!mReverseYAxis) {
                mWorldBoundaries.top    = 0;
                mWorldBoundaries.bottom = mWorldHeight;
            } else {
                mWorldBoundaries.top    = mWorldHeight;
                mWorldBoundaries.bottom = 0;
            }

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

    public float screenToWorldX(float screenX) {
        return pixelsToWorldUnits(screenX);
    }
    
    public float screenToWorldY(float screenY) {
        if (!mReverseYAxis) {
            return pixelsToWorldUnits(screenY);
        } else {
            return pixelsToWorldUnits(mViewHeight - screenY);
        }
    }

    public float worldToScreenX(float worldX) {
    	return worldUnitsToPixels(worldX);
    }
    
    public float worldToScreenY(float worldY) {
        if (!mReverseYAxis) {
            return worldUnitsToPixels(worldY);
        } else {
            return mViewHeight - worldUnitsToPixels(worldY);
        }
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

        float left = worldToScreenX(mWorldBoundaries.left);
        float top = worldToScreenY(mWorldBoundaries.bottom);
        float right = worldToScreenX(mWorldBoundaries.right);
        float bottom = worldToScreenY(mWorldBoundaries.top);

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
