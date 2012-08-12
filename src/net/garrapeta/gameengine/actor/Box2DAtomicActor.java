package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.Viewport;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;


/**
 * Actor de un mundo f�sico Box2D
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
	
	// --------------------------------------------------- M�todos de Actor
	
	@Override
	public void processFrame(float gameTimeStep) {}
	
	// ------------------------------------------- M�todos de IAtomicActor


	@Override
	public PointF getWorldPos() {
		Vector2 worldPos = bodies.get(0).getWorldCenter();
		return Viewport.vector2ToPointF(worldPos);
	}
	
	@Override
	public void setWorldPos(float x, float y) {
		Log.e("world", "setWorldPos");
	}
	
	// ------------------------------------------------- M�todos propios
	
	public PointF getLinearVelocity () {
		return Viewport.vector2ToPointF(bodies.get(0).getLinearVelocity());
	}
}
