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
 
	// -------------------------------------------------------- Constantes
	
	public static final float DEFAULT_DP_IN_METER = 30;
	
	// -------------------------------------------- Variables de instancia
	
	/** Mundo asociado */
	public GameWorld world;
	
	/**
	 *  Candidad de dps que hay en una unidad del mundo (metros)
	 */
	private float dpsInWorldUnits = DEFAULT_DP_IN_METER;
	
	/** Ancho físico del viewport, en píxeles */
	private float viewportWidthPixels;
	
	/** Alto físico del viewport, en píxeles */
	private float viewportHeightPixels;
	
	/** Ancho del viewport, en unidades del mundo. */
	private float viewportWidthWorldUnits;
	
	/** Alto del viewport, en unidades del mundo. */
	private float viewportHeightWorldUnits;
	
	public float offsetX;
	public float offsetY;
	
	/** Límites del mundo visible */
	private RectF boundaries;
	
	/** Densidad lógica del display */
	private float density;

	// -------------------------------------------------- Métodos estáticos
	
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
	
	// ------------------------------------------------ Constructor
	
	/**
	 * @param world
	 */
	public Viewport(GameWorld world) {
		this.world      		 = world;
	}
	
	// ------------------------------------------------ Métodos de instancia
	
	public void onGameViewSizeChanged(float screenWidth, float screenHeight) {
		this.viewportWidthPixels   = screenWidth;
		this.viewportHeightPixels  = screenHeight;
		
		density = world.getActivity().getResources().getDisplayMetrics().density;
		
		update();
	}
	
	
	void setViewportWidthInWorldUnits(float worldUnits) {
		this.dpsInWorldUnits = pixelsToDps(viewportWidthPixels) / worldUnits;
		update();
	}
	
	public float getViewportWidthInWorldUnits() {
		return viewportWidthWorldUnits;
	}
	
	public void setViewportHeightInWorldUnits(float worldUnits) {
		this.dpsInWorldUnits = pixelsToDps(viewportHeightPixels) / worldUnits;
		
		update();
	}
	
	public float getViewportHeightInWorldUnits() {
		return viewportHeightWorldUnits;
	}
	
	private void update() {
		viewportWidthWorldUnits  = pixelsToDps(viewportWidthPixels)  / dpsInWorldUnits;
		viewportHeightWorldUnits = pixelsToDps(viewportHeightPixels) / dpsInWorldUnits;
		
		float left   = 0;
		float bottom = 0;
		float right  = getViewportWidthInWorldUnits();
		float top    = getViewportHeightInWorldUnits();
		
		boundaries = new RectF(left, top, right, bottom);
	}
	
	/**
	 * @return the worldBoundaries
	 */
	public RectF getBoundaries() {
		return boundaries;
	}
	
	// Métodos de conversión de longitudes
	

	public float worldUnitsToPixels(float worldUnits) {
		return dpsToPixels(worldUnits * dpsInWorldUnits);
	}
	
	public float pixelsToWorldUnits(float pixels) {
		return  pixelsToDps(pixels / dpsInWorldUnits);
	}
	
	public float pixelsToDps (float pixels) {
		return pixels / density;
	}
	
	public float dpsToPixels (float dps) {
		return dps * density;
	}
	
	// Métodos de conversión de coordenadas
	
	public PointF worldToScreen(PointF worldPos) {
	    return worldToScreen(worldPos.x, worldPos.y);  
	} 
	
	public PointF screenToWorld(PointF screenPos) {
	    return screenToWorld(screenPos.x, screenPos.y);  
	} 
	
	public PointF worldToScreen(float worldX, float worldY) {
	    return new PointF(worldUnitsToPixels(worldX), 
	    		          viewportHeightPixels - worldUnitsToPixels(worldY));  
	} 
	
	public PointF screenToWorld(float screenX, float screenY) {
	    return new PointF(pixelsToWorldUnits(screenX), 
	    		          pixelsToWorldUnits(viewportHeightPixels - screenY));  
	}
	
	
	// ---------------------------------------------------------- Variables estáticas
	
	private static Paint paint = new Paint();
	
	// ------------------------------------------------ Bloque inicialización estática
	

	
	//------------------------------------------------------------------------ Métodos
	
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
		float   screenRadius 	  =  world.viewport.worldUnitsToPixels(circleShape.getRadius());
		Vector2 worldPosition     =  circleShape.getPosition();
		PointF  screenPosition    =  world.viewport.worldToScreen(worldPosition.x, worldPosition.y);
		screenPosition = new PointF(0,0);
        // circulo
        drawCircle(canvas, 
                   screenPosition,
        		   screenRadius ,
        		   strokeColor, fillColor); 
        
    
        // linea
        if (lineColor != Color.TRANSPARENT) {
			paint.setStyle(Style.STROKE);
        	paint.setColor(lineColor);
        	canvas.drawLine(screenPosition.x, screenPosition.y, 
        			        screenPosition.x + screenRadius, screenPosition.y, paint);
        }

	}
	
	private void drawCircle(Canvas canvas, PointF screenPosition, float screenRadius, int strokeColor, int fillColor) {

		// circulo
		if (fillColor != Color.TRANSPARENT) {
			paint.setStyle(Style.FILL);
			paint.setColor(fillColor);
			
	        canvas.drawCircle(screenPosition.x, 
  		           			  screenPosition.y, 
  		           			  screenRadius, 
  		           			  paint); 	
        }
		
		// circunferencia
		if (strokeColor != Color.TRANSPARENT) {
			paint.setStyle(Style.STROKE);
			paint.setColor(strokeColor);
			
	        canvas.drawCircle(screenPosition.x, 
	        				  screenPosition.y, 
  		                      screenRadius, 
                              paint); 	
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
	
	private void drawPath(Canvas canvas, Vector2[] vertexes, boolean close, int strokeColor, int fillColor, int lineColor) {
		
		int count = vertexes.length;
        Path path = new Path();
       
        
        for (int i = 0; i < count; i++) {
        	Vector2 aux = new Vector2(world.viewport.worldUnitsToPixels(vertexes[i].x), 
        			                 -world.viewport.worldUnitsToPixels(vertexes[i].y));
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
        	canvas.drawLine(0, 0, 
        			        Math.max(canvas.getWidth(), canvas.getHeight()), 
        			        0, paint);
        	canvas.restore();
        }
		
	}
	


	//----------------------------------------------------------------------------------
	
	// Métodos de verificar si un punto está en la silueta
	
	public boolean isPointInShape(Shape shape, float worldX, float worldY) {
		Type t = shape.getType();
		
		if (t == Type.Circle) {
			return isPointInCircleShape((CircleShape)  shape, worldX, worldY);
		
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
	 * Dibuja una cuadrícula en pantalla
	 * 
	 * @param canvas
	 * @param worldSpacing  tamaño de las celdas en unidades del mundo (normalmente metros)
	 */
	public void drawBoundaries(Canvas canvas) {
		float worldSpacing = 1;
		float screenSpacing = worldUnitsToPixels(worldSpacing);
		
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.DKGRAY);
		


		float top 		= worldUnitsToPixels(boundaries.top    - offsetY);
		float left 		= worldUnitsToPixels(boundaries.left   - offsetX);
		float bottom 	= worldUnitsToPixels(boundaries.bottom - offsetY - (offsetY % worldSpacing));
		float right 	= worldUnitsToPixels(boundaries.right  - offsetX - (offsetX % worldSpacing));
		
		
		// rayas verticales
		for (float i = left; i < right; i += screenSpacing ) {
			canvas.drawLine(i, top, i, bottom, paint);
		}
		
		// rayas horizontales
		for (float i = bottom; i < top; i += screenSpacing) {
			canvas.drawLine(left, i, right, i, paint);
		}
		
	}
	
}

