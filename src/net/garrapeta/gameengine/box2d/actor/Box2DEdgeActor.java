package net.garrapeta.gameengine.box2d.actor;

import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.box2d.BodyUserData;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import net.garrapeta.gameengine.box2d.ShapeBasedBodyDrawer;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class Box2DEdgeActor extends Box2DAtomicActor {

	
	// -------------------------------------------------- Constantes
	
	private final static int DEFAULT_STROKE_COLOR = Color.GREEN;
	private final static int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
	private final static int DEFAULT_LINE_COLOR = Color.TRANSPARENT;
	
	
	// ------------------------------------------ Variables estáticas
	
	private final static ShapeBasedBodyDrawer edgeShapeDrawer;
	
	// -------------------------------------- Inicialización estática
	
	static {
		edgeShapeDrawer = new ShapeBasedBodyDrawer(DEFAULT_STROKE_COLOR, 
				                                     DEFAULT_FILL_COLOR, 
				                                     DEFAULT_LINE_COLOR);
	}
	
	// -------------------------------------------------- Constructor
	
	/**
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 * @param vertexes vertices, EN EL SENTIDO CONTRARIO A LAS AGUJAS DEL RELOJ, en unidades del mundo
	 * @param dynamic
	 */
	public Box2DEdgeActor(Box2DWorld world) {
		super(world);
		
		this.strokeColor 	= DEFAULT_STROKE_COLOR;
		this.fillColor 		= DEFAULT_FILL_COLOR;
		this.lineColor 		= DEFAULT_LINE_COLOR;
	}
	
	/**
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 * @param p0 vertice inicial, en unidades del mundo
	 * @param p1 vertice final, en unidades del mundo
	 * @param dynamic
	 */
	public Box2DEdgeActor(Box2DWorld world, PointF worldPos, PointF p0, PointF p1, boolean dynamic) {
		this(world);
	    
		Body body = world.createBody(this, worldPos, dynamic);
	    setEdges(body,new Vector2[] {Viewport.pointFToVector2(p0), Viewport.pointFToVector2(p1)});
	    ((BodyUserData)body.getUserData()).setBodyDrawer(edgeShapeDrawer);
	}
	
	
	/**
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 * @param vertexes, vértices, en unidades del mundo
	 * @param dynamic
	 */
	public Box2DEdgeActor(Box2DWorld world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
		this(world);
	    
		Body body = world.createBody(this, worldPos, dynamic);
	    setEdges(body, Viewport.pointFToVector2(vertexes));
	    ((BodyUserData)body.getUserData()).setBodyDrawer(edgeShapeDrawer);
	}
	
	private void setEdges(Body body, Vector2[] vertexes) {
		// Create Shape with Properties
		for (int i = 0; i < vertexes.length - 1; i++) {
			Vector2 p0 = vertexes[i];
			Vector2 p1 = vertexes[i + 1];
			
			PolygonShape edgeShape = new PolygonShape();
			edgeShape.setAsEdge(p0, p1);	
			
			body.createFixture(edgeShape, 1.0f);
			edgeShape.dispose();
		}
	}
	
}
