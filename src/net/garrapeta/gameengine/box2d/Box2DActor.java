package net.garrapeta.gameengine.box2d;


import java.util.ArrayList;
import java.util.Iterator;

import net.garrapeta.gameengine.Actor;
import android.graphics.Canvas;
import android.graphics.Color;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Shape;


/**
 * Actor de un mundo físico Box2D
 * @author GaRRaPeTa
 */
public abstract class Box2DActor extends Actor {

	// ----------------------------------------- Variables estáticas
	
	protected static ShapeBasedBodyDrawer defaultBodyDrawer;
	
	// --------------------------------------------------- Variables
	/**
	 *  Cuerpos del actor
	 */
	protected ArrayList<Body> bodies;
	
	/**
	 *  Joints del actor
	 */
	protected ArrayList<Joint> joints;
	
	// --------------------------------------------------- Inicialización estática

	static  {
		int strokeColor  = Color.GRAY;
		int fillColor    = Color.DKGRAY;
		
		defaultBodyDrawer = new ShapeBasedBodyDrawer(strokeColor, fillColor, Color.TRANSPARENT);
	}
	
	// --------------------------------------------------- Constructor
	
	/**
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 */
	public Box2DActor(Box2DWorld gameWorld) {
		this(gameWorld, 0);
	}
	
	/**
	 * @param world
	 * @param worldPos, posición en el mundo, en unidades del mundo
	 */
	public Box2DActor(Box2DWorld gameWorld, int zIndex) {
		super(gameWorld, zIndex);
	}
	
	// ------------------------------------------------- Métodos propios
	
	public float getMass() {
		float mass = 0;
		
		for (int i = 0; i < bodies.size(); i++) {
			mass += bodies.get(i).getMass();
		}
		
		return mass;
	}

	/**
	 * Callback invocado cuando el actor empieza a ser contactado por otro actor
	 * @param thisBody  body de este actor que ha contactado
	 * @param otherBody body del otro actor que ha contactado
	 * @param contact 
	 */
	public void onBeginContact(Body thisBody, Box2DActor other, Body otherBody, Contact contact) {
	}
	

	/**
	 * @param bodyA
	 * @param actorB
	 * @param bodyB
	 * @param contact
	 * @param oldManifold
	 */
	public void onPreSolveContact(Body bodyA, Box2DActor actorB, Body bodyB,
			Contact contact, Manifold oldManifold) {
	}


	/**
	 * @param bodyA
	 * @param actorB
	 * @param bodyB
	 * @param contact
	 * @param impulse
	 */
	public void onPostSolveContact(Body bodyA, Box2DActor actorB, Body bodyB,
			Contact contact, ContactImpulse impulse) {
	}
	
	/**
	 * Callback invocado cuando el actor termina de ser contactado por otro actor
	 * @param contact 
	 * @param thisBody  body de este actor que ha contactado
	 * @param otherBody body del otro actor que ha contactado
	 */
	public void onEndContact(Body bodyA, Box2DActor actorB, Body bodyB, Contact contact) {
	}
	
	// --------------------------------------------------- Métodos de Actor
	
	@Override
	public void draw(Canvas canvas) {
		drawShapes(canvas);
	}

	/**
	 * Pinta las shapes de este objeto usando los shapedrawers declarados
	 * @param canvas
	 */
	public void drawShapes(Canvas canvas) {
		if (bodies != null) {
			int size = bodies.size();
			for (int i = 0; i < size; i++) {
				Body body = bodies.get(i);
				IBodyDrawer drawer = ((BodyUserData) body.getUserData()).getBodyDrawer();
				if (drawer == null) {
					drawer = defaultBodyDrawer;
				}
				
				drawer.draw(canvas, this, body);
				
			}
		}
	}
	
	@Override
	public boolean isPointInActor(float worldX, float worldY) {
		int l = bodies.size();
		for (int i = 0; i < l; i++) {
			Body body = bodies.get(i);
			
			ArrayList<Fixture> fixtures = body.getFixtureList();
			Iterator<Fixture> ite = fixtures.iterator();
	        while (ite.hasNext()) {
	        	Fixture f = ite.next();
	        	Shape shape = f.getShape();
	        	
	        	if (gameWorld.viewport.isPointInShape(shape, worldX, worldY)) {
	        		return true;
	        	}
	        	
	        }
	        
		}
		return false;
	}
	
	
	@Override
	public void onRemovedFromWorld() {
		super.onRemovedFromWorld();
		destroyAllBodies();
	}

	public void addBody(Body body) {
		if (bodies == null) {
			bodies = new ArrayList<Body>();
		}
		BodyUserData ud = (BodyUserData)body.getUserData();
		
		// si este body pertenecía a otro actor, se le quita como body
		Box2DActor oldActor = ud.getActor(); 
		if (oldActor != null) {
			oldActor.removeBody(body);
		}
		
		// se pone el body en este actor
		bodies.add(body);
		((BodyUserData)body.getUserData()).setActor(this);
	}
	
	public void removeBody(Body body) {
		if (bodies != null) {
			bodies.remove(body);
		}
		((BodyUserData)body.getUserData()).setActor(null);
	}

	public void destroyAllBodies() {
		synchronized (gameWorld) {
			int l = bodies.size();
			for (int i = l -1 ; i >= 0; i--) {
				((Box2DWorld) gameWorld).destroyBody(this, bodies.get(i));
			}
		}
	}
	
	public void addJoint(Joint joint) {
		if (joints == null) {
			joints = new ArrayList<Joint>();
		}
		joints.add(joint);
	}

	public void removeJoint(Joint joint) {
		if (joints != null) {
			joints.remove(joint);
		}
	}

	public ArrayList<Body> getBodies() {
		return bodies;
	}
	
	public ArrayList<Shape> getShapes() {
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		
		int l0 = bodies.size();
		for (int i = 0; i < l0; i++) {
			Body b = bodies.get(i);
			
			ArrayList<Fixture> fixtures = b.getFixtureList();
			
			int l1 = fixtures.size();
			for (int j = 0; j < l1; j++) {
				shapes.add(fixtures.get(j).getShape());
			}
		}
		
		return shapes;
	}
		
		
}
