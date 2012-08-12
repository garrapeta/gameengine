package net.garrapeta.gameengine;

import net.garrapeta.MathUtils;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public class Viewport {

    // -------------------------------------------------------- Constants

    public static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".viewport";
    
    
    // ------------------------------------------------------------ Types
    
    public enum ProjectionMode {EXPLICIT, FIT_WIDTH,FIT_HEIGHT};

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

    /** Logic density of the display */
    private float mDisplayDensity;

    /** Current projection mode */
    private ProjectionMode mProjectionMode;
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
    public Viewport(GameWorld world) {
        this.mWorld = world;
    }

    // ------------------------------------------------ Instance methods

    public void gameViewSizeChanged(int viewWidth, int viewHeight) {
        Log.i(LOG_SRC, "onGameViewSizeChanged(" + viewWidth + ", " + + viewHeight + ")");
        
        mViewWidth  = viewWidth;
        mViewHeight = viewHeight;
        mDisplayDensity = mWorld.getActivity().getResources().getDisplayMetrics().density;

        updateWorldBoundaries();
    }

    public void setWorldSize(float width, float height, float dpsInWorldUnit) {
        setWorldSize(width, height, dpsInWorldUnit, ProjectionMode.EXPLICIT);
    }

    public void setWorldSize(float width, float height, ProjectionMode mode) {
        setWorldSize(width, height, Float.MIN_VALUE, mode);
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
                
                mWorld.onGameWorldSizeChanged();
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
        return pixels / mDisplayDensity;
    }

    public float dpsToPixels(float dps) {
        return dps * mDisplayDensity;
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

    // ------------------------------------------------------ Static Variables

    private static Paint paint = new Paint();

    // ------------------------------------------------ Static initialisation block

    // ------------------------------------------------------------------- Methods


    public void draw(Canvas canvas, Shape shape, int strokeColor, int fillColor, int lineColor) {
        Type t = shape.getType();

        if (t == Type.Circle) {
            drawCircleShape(canvas, (CircleShape) shape, strokeColor, fillColor, lineColor);

        } else if (t == Type.Polygon) {
            drawPolygonShape(canvas, (PolygonShape) shape, strokeColor, fillColor, lineColor);

        } else {
            throw new IllegalArgumentException("Can not draw shape: " + shape);
        }

    }

    private void drawCircleShape(Canvas canvas, CircleShape circleShape, int strokeColor, int fillColor, int lineColor) {
        float screenRadius = mWorld.viewport.worldUnitsToPixels(circleShape.getRadius());
        Vector2 worldPosition = circleShape.getPosition();
        PointF screenPosition = mWorld.viewport.worldToScreen(worldPosition.x, worldPosition.y);
        screenPosition = new PointF(0, 0);
        // circulo
        drawCircle(canvas, screenPosition, screenRadius, strokeColor, fillColor);

        // linea
        if (lineColor != Color.TRANSPARENT) {
            paint.setStyle(Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawLine(screenPosition.x, screenPosition.y, screenPosition.x + screenRadius, screenPosition.y,
                    paint);
        }

    }

    private void drawCircle(Canvas canvas, PointF screenPosition, float screenRadius, int strokeColor, int fillColor) {

        // circulo
        if (fillColor != Color.TRANSPARENT) {
            paint.setStyle(Style.FILL);
            paint.setColor(fillColor);

            canvas.drawCircle(screenPosition.x, screenPosition.y, screenRadius, paint);
        }

        // circunferencia
        if (strokeColor != Color.TRANSPARENT) {
            paint.setStyle(Style.STROKE);
            paint.setColor(strokeColor);

            canvas.drawCircle(screenPosition.x, screenPosition.y, screenRadius, paint);
        }

    }

    private void drawPolygonShape(Canvas canvas, PolygonShape polygon, int strokeColor, int fillColor, int lineColor) {
        int count = polygon.getVertexCount();

        Vector2[] aux = new Vector2[count];
        for (int i = 0; i < count; i++) {
            Vector2 vertex = new Vector2();
            polygon.getVertex(i, vertex);
            aux[i] = vertex;
        }
        drawPath(canvas, aux, true, strokeColor, fillColor, lineColor);
    }

    private void drawPath(Canvas canvas, Vector2[] vertexes, boolean close, int strokeColor, int fillColor,
            int lineColor) {

        int count = vertexes.length;
        Path path = new Path();

        for (int i = 0; i < count; i++) {
            Vector2 aux = new Vector2(mWorld.viewport.worldUnitsToPixels(vertexes[i].x),
                    -mWorld.viewport.worldUnitsToPixels(vertexes[i].y));
            if (path.isEmpty()) {
                path.moveTo(aux.x, aux.y);
            }
            path.lineTo(aux.x, aux.y);
        }

        if (close) {
            path.close();
        }

        if (fillColor != Color.TRANSPARENT) {
            paint.setStyle(Style.FILL);
            paint.setColor(fillColor);
            canvas.drawPath(path, paint);
        }

        if (strokeColor != Color.TRANSPARENT) {
            paint.setStyle(Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
        }

        // linea
        if (lineColor != Color.TRANSPARENT) {
            canvas.save();
            canvas.clipPath(path);
            paint.setStyle(Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawLine(0, 0, Math.max(canvas.getWidth(), canvas.getHeight()), 0, paint);
            canvas.restore();
        }

    }

    // ----------------------------------------------------------------------------------

    // Methods to check if a point is in a shape

    public boolean isPointInShape(Shape shape, float worldX, float worldY) {
        Type t = shape.getType();

        if (t == Type.Circle) {
            return isPointInCircleShape((CircleShape) shape, worldX, worldY);

        } else if (t == Type.Polygon) {
            return isPointInPolygonShape((PolygonShape) shape, worldX, worldY);

        } else {
            throw new IllegalArgumentException("Can not verify is pointis in shape shape: " + shape);
        }
    }

    private static boolean isPointInCircleShape(CircleShape shape, float worldX, float worldY) {
        Vector2 pos = shape.getPosition();
        return MathUtils.isPointInCicle(worldX, worldY, pos.x, pos.y, shape.getRadius());
    }

    private boolean isPointInPolygonShape(PolygonShape shape, float worldX, float worldY) {
        Log.w(GameWorld.LOG_SRC, "Not implmented: isPointInPolygonShape");
        return false;
    }

    /**
     * Dibuja una cuadr�cula en pantalla
     * 
     * @param canvas
     * @param worldSpacing
     *            tama�o de las celdas en unidades del mundo (normalmente
     *            metros)
     */
    public void drawBoundaries(Canvas canvas) {
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

}
