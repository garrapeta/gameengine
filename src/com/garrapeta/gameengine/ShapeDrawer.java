package com.garrapeta.gameengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint.Style;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class ShapeDrawer {
    
    // -------------------------------------------------- Static vars
    
    static final Paint sPaint = new Paint();
    
    // -------------------------------------------------- Constantes

   
    private final static int DEFAULT_CIRCLE_STROKE_COLOR = Color.argb(0xFF, 0xFF, 0, 0);
    private final static int DEFAULT_CIRCLE_FILL_COLOR   = Color.argb(0x40, 0xFF, 0, 0);
    private final static int DEFAULT_CIRCLE_LINE_COLOR   = Color.GRAY;
    
    private final static int DEFAULT_EDGE_STROKE_COLOR = Color.YELLOW;
    private final static int DEFAULT_EDGE_FILL_COLOR = Color.TRANSPARENT;
    private final static int DEFAULT_EDGE_LINE_COLOR = Color.TRANSPARENT;
    
    private final static int DEFAULT_POLYGON_STROKE_COLOR = Color.argb(0xFF, 0, 0, 0xFF);
    private final static int DEFAULT_POLYGON_FILL_COLOR = Color.argb(0x40, 0, 0, 0xFF);
    private final static int DEFAULT_POLYGON_LINE_COLOR = Color.GRAY;
    
    private final static int DEFAULT_CHAIN_STROKE_COLOR = Color.GREEN;
    private final static int DEFAULT_CHAIN_FILL_COLOR   = Color.TRANSPARENT;
    private final static int DEFAULT_CHAIN_LINE_COLOR   = Color.GRAY;
    
    public static void draw(Canvas canvas, Viewport viewport, Shape shape) {
        int strokeColor;
        int fillColor;
        int lineColor;
        
        switch (shape.getType()) {
        case Circle:
            strokeColor = DEFAULT_CIRCLE_STROKE_COLOR;
            fillColor = DEFAULT_CIRCLE_FILL_COLOR;
            lineColor = DEFAULT_CIRCLE_LINE_COLOR;
            break;

        case Polygon:
            strokeColor = DEFAULT_POLYGON_STROKE_COLOR;
            fillColor = DEFAULT_POLYGON_FILL_COLOR;
            lineColor = DEFAULT_POLYGON_LINE_COLOR;
            break;
            
        case Edge:
            strokeColor = DEFAULT_EDGE_STROKE_COLOR;
            fillColor = DEFAULT_EDGE_FILL_COLOR;
            lineColor = DEFAULT_EDGE_LINE_COLOR;
            break;
            
        case Chain:
            strokeColor = DEFAULT_CHAIN_STROKE_COLOR;
            fillColor = DEFAULT_CHAIN_FILL_COLOR;
            lineColor = DEFAULT_CHAIN_LINE_COLOR;
            break;
            
        default:
            throw new IllegalArgumentException("Can not get default color for shape: " + shape);
        }

        draw(canvas, sPaint, viewport, shape, strokeColor, fillColor, lineColor);
    }
    
    public static void draw(Canvas canvas, Paint paint, Viewport viewport, Shape shape, int strokeColor, int fillColor, int lineColor) {
        switch (shape.getType()) {
        case Circle:
            drawCircleShape(canvas, viewport, paint, (CircleShape) shape, strokeColor, fillColor, lineColor);
            break;        
        case Polygon:
            drawPolygonShape(canvas, viewport, paint, (PolygonShape) shape, strokeColor, fillColor, lineColor);
            break;
        case Edge:
            drawEdgeShape(canvas, paint, viewport, (EdgeShape) shape, strokeColor, fillColor, lineColor);
            break;
        case Chain:
            drawChainShape(canvas, paint, viewport, (ChainShape) shape, strokeColor, fillColor, lineColor);
            break;
        default:
            throw new IllegalArgumentException("Can not draw shape: " + shape);
        }
    }

    private static void drawCircleShape(Canvas canvas, Viewport viewport, Paint paint, CircleShape circleShape, int strokeColor, int fillColor, int lineColor) {
        float screenRadius = viewport.worldUnitsToPixels(circleShape.getRadius());
        Vector2 worldPosition = circleShape.getPosition();
        PointF screenPosition = viewport.worldToScreen(worldPosition.x, worldPosition.y);
        screenPosition = new PointF(0, 0);
        // circulo
        drawCircle(canvas, paint, screenPosition, screenRadius, strokeColor, fillColor);

        // linea
        if (lineColor != Color.TRANSPARENT) {
            paint.setStyle(Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawLine(screenPosition.x, screenPosition.y, screenPosition.x + screenRadius, screenPosition.y, paint);
        }

    }

    private static void drawCircle(Canvas canvas, Paint paint, PointF screenPosition, float screenRadius, int strokeColor, int fillColor) {

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

    private static void drawPolygonShape(Canvas canvas, Viewport viewport, Paint paint, PolygonShape polygon, int strokeColor, int fillColor, int lineColor) {
        int count = polygon.getVertexCount();

        Vector2[] aux = new Vector2[count];
        for (int i = 0; i < count; i++) {
            Vector2 vertex = new Vector2();
            polygon.getVertex(i, vertex);
            aux[i] = vertex;
        }
        drawPath(canvas, paint, viewport, aux, strokeColor, fillColor, lineColor);
    }

    private static void drawChainShape(Canvas canvas, Paint paint, Viewport viewport, ChainShape chain, int strokeColor, int fillColor, int lineColor) {
        int count = chain.getVertexCount();

        Vector2[] aux = new Vector2[count];
        for (int i = 0; i < count; i++) {
            Vector2 vertex = new Vector2();
            chain.getVertex(i, vertex);
            aux[i] = vertex;
        }
        drawPath(canvas, paint, viewport, aux, strokeColor, fillColor, lineColor);
    }

    private static void drawEdgeShape(Canvas canvas, Paint paint, Viewport viewport, EdgeShape shape, int strokeColor, int fillColor, int lineColor) {
        Vector2[] aux = new Vector2[2];
        Vector2 v1 = new Vector2();
        shape.getVertex1(v1);
        aux[0] = v1;

        Vector2 v2 = new Vector2();
        shape.getVertex2(v2);
        aux[1] = v2;

        drawPath(canvas, paint, viewport, aux, strokeColor, fillColor, lineColor);
    }

    private static void drawPath(Canvas canvas, Paint paint, Viewport viewport, Vector2[] vertexes, int strokeColor, int fillColor, int lineColor) {

        int count = vertexes.length;
        Path path = new Path();

        for (int i = 0; i < count; i++) {
            Vector2 aux = new Vector2(viewport.worldUnitsToPixels(vertexes[i].x), -viewport.worldUnitsToPixels(vertexes[i].y));
            if (path.isEmpty()) {
                path.moveTo(aux.x, aux.y);
            }
            path.lineTo(aux.x, aux.y);
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

}
