package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.BodyUserData;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.ShapeBasedBodyDrawer;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.actor.Box2DAtomicActor;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Box2DPolygonActor extends Box2DAtomicActor {

	
	// -------------------------------------------------- Constantes
	
	private final static int DEFAULT_STROKE_COLOR 	= Color.CYAN;
	private final static int DEFAULT_FILL_COLOR	 	= Color.TRANSPARENT;
	private final static int DEFAULT_LINE_COLOR 	= Color.GRAY;
	
	
	// ------------------------------------------ Variables est�ticas
	
	private final static ShapeBasedBodyDrawer polygonShapeDrawer;
	
	// -------------------------------------- Inicializaci�n est�tica
	
	static {
		polygonShapeDrawer = new ShapeBasedBodyDrawer(DEFAULT_STROKE_COLOR, 
				                                      DEFAULT_FILL_COLOR, 
				                                      DEFAULT_LINE_COLOR);
	}
	
	// -------------------------------------------------- Constructor
	
	/**
	 * @param world
	 * @param worldPos, posici�n en el mundo, en unidades del mundo
	 * @param vertexes vertices, EN EL SENTIDO CONTRARIO A LAS AGUJAS DEL RELOJ, en unidades del mundo
	 * @param dynamic
	 */
	public Box2DPolygonActor(Box2DWorld world) {
		super(world);
		
		this.strokeColor 	= DEFAULT_STROKE_COLOR;
		this.fillColor 		= DEFAULT_FILL_COLOR;
		this.lineColor 		= DEFAULT_LINE_COLOR;
	}
	
	/**
	 * @param world
	 * @param worldPos, posici�n en el mundo, en unidades del mundo
	 * @param vertexes vertices, EN EL SENTIDO CONTRARIO A LAS AGUJAS DEL RELOJ, en unidades del mundo
	 * @param dynamic
	 */
	public Box2DPolygonActor(Box2DWorld world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
		this(world);

		// Create Shape with Properties
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set(Viewport.pointFToVector2(vertexes));
		Body body = world.createBody(this, worldPos, dynamic);
		// Assign shape to Body
		body.createFixture(polygonShape, 1.0f);
		((BodyUserData)body.getUserData()).setBodyDrawer(polygonShapeDrawer);
		polygonShape.dispose();
	}

	/**
	 * @param world
	 * @param worldPos,  posici�n en el mundo, en unidades del mundo
	 * @param halfWidth  en unidades del mundo
	 * @param halfHeight en unidades del mundo
	 * @param dynamic
	 */
	public Box2DPolygonActor(Box2DWorld world, PointF worldPos,
			float halfWidth, float halfHeight, boolean dynamic) {
		this(world);
	    
		// Create Shape with Properties
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(halfWidth, halfHeight);
		//Body body = createBody(world, worldPos, dynamic);
		
		Body body = world.createBody(this, worldPos, dynamic);
		// Assign shape to Body
		body.createFixture(polygonShape, 1.0f);
		((BodyUserData)body.getUserData()).setBodyDrawer(polygonShapeDrawer);
		polygonShape.dispose();
	}
	
	/**
	 * @param world
	 * @param worldPos, posici�n en el mundo, en unidades del mundo
	 * @param halfWidth  en unidades del mundo
	 * @param halfHeight en unidades del mundo
	 * @param angle      
	 * @param dynamic
	 */
	public Box2DPolygonActor(Box2DWorld world, PointF worldPos, float halfWidth, float halfHeight, 
			                                   float angle, boolean dynamic) {
		this(world);
		
	    // Create Shape with Properties
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(halfWidth, halfHeight, new Vector2(0,0), angle);
		Body body = world.createBody(this, worldPos, dynamic);
		// Assign shape to Body
		body.createFixture(polygonShape, 1.0f);
		((BodyUserData)body.getUserData()).setBodyDrawer(polygonShapeDrawer);
		polygonShape.dispose();
	}
	

}
