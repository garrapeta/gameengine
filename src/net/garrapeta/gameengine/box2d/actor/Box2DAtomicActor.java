package net.garrapeta.gameengine.box2d.actor;

import net.garrapeta.gameengine.IAtomicActor;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.box2d.Box2DActor;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;


/**
 * Actor de un mundo físico Box2D
 * @author GaRRaPeTa
 */
public abstract class Box2DAtomicActor extends Box2DActor implements IAtomicActor {

	// --------------------------------------------------- Constructor
	
	/**
	 * @param world
	 * @param dynamic
	 */
	public Box2DAtomicActor(Box2DWorld gameWorld) {
		this(gameWorld, 0);
	}
	
	/**
	 * @param world
	 * @param dynamic
	 */
	public Box2DAtomicActor(Box2DWorld gameWorld, int zIndex) {
		super(gameWorld, zIndex);
	}
	
	
	// --------------------------------------------------- Variable
	
	protected int strokeColor;
	protected int fillColor;
	protected int lineColor;
	
	// --------------------------------------------------- Métodos de Actor
	
	@Override
	public void doLogic(float gameTimeStep) {}
	
	// ------------------------------------------- Métodos de IAtomicActor


	@Override
	public PointF getWorldPos() {
		Vector2 worldPos = bodies.get(0).getWorldCenter();
		return Viewport.vector2ToPointF(worldPos);
	}
	
	@Override
	public void setWorldPos(float x, float y) {
		Log.e("world", "setWorldPos");
	}
	
	// ------------------------------------------------- Métodos propios
	
	public PointF getLinearVelocity () {
		return Viewport.vector2ToPointF(bodies.get(0).getLinearVelocity());
	}
}
