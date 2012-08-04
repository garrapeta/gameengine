package net.garrapeta.gameengine.box2d.actor;


import net.garrapeta.gameengine.box2d.BodyUserData;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import net.garrapeta.gameengine.box2d.ShapeBasedBodyDrawer;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Actor basado en un círculo
 * @author GaRRaPeTa
 */
public class Box2DCircleActor extends Box2DAtomicActor {
	
	
	// -------------------------------------------------- Constantes
	
	private final static int DEFAULT_STROKE_COLOR = Color.RED;
	private final static int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
	private final static int DEFAULT_LINE_COLOR = Color.GRAY;
	
	
	// ------------------------------------------ Variables estáticas
	
	private final static ShapeBasedBodyDrawer circleShapeDrawer;
	
	// -------------------------------------- Inicialización estática
	
	static {
		circleShapeDrawer = new ShapeBasedBodyDrawer(DEFAULT_STROKE_COLOR, 
				                                     DEFAULT_FILL_COLOR, 
				                                     DEFAULT_LINE_COLOR);
	}
	// -------------------------------------------------- Constructor
	
	/**
	 * Constructor
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 * @param radius, radio en unidades del mundo
	 * @param dynamic
	 */
	public Box2DCircleActor(Box2DWorld world, PointF worldPos, float radius, boolean dynamic) {
		super(world);
		
		this.strokeColor 	= DEFAULT_STROKE_COLOR;
		this.fillColor 		= DEFAULT_FILL_COLOR;
		this.lineColor 		= DEFAULT_LINE_COLOR;
		
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);
		Body body = world.createBody(this, worldPos, dynamic);
		// Assign shape to Body
		body.createFixture(circleShape, 1.0f);
		((BodyUserData)body.getUserData()).setBodyDrawer(circleShapeDrawer);
		circleShape.dispose();
		
	}


	// ------------------------------------------------------ Métodos propios


}
