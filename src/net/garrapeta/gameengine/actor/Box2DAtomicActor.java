package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.Viewport;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;


/**
 * Actor de un mundo físico Box2D
 * @author GaRRaPeTa
 */
public abstract class Box2DAtomicActor<T extends Box2DWorld> extends Box2DActor<T> implements IAtomicActor {

	// --------------------------------------------------- Constructor
	
	/**
	 * @param world
	 * @param dynamic
	 */
	public Box2DAtomicActor(T world) {
		this(world, 0);
	}
	
	/**
	 * @param world
	 * @param dynamic
	 */
	public Box2DAtomicActor(T world, int zIndex) {
		super(world, zIndex);
	}

	// --------------------------------------------------- métodos de Actor
	
	@Override
	public void processFrame(float gameTimeStep) {}
	
	// ------------------------------------------- métodos de IAtomicActor


	@Override
	public PointF getWorldPos() {
		Vector2 worldPos = mBodies.get(0).getWorldCenter();
		return Viewport.vector2ToPointF(worldPos);
	}
	
	@Override
	public void setWorldPos(float x, float y) {
		Log.e("world", "setWorldPos");
	}
	
	// ------------------------------------------------- métodos propios
	
	public PointF getLinearVelocity () {
		return Viewport.vector2ToPointF(mBodies.get(0).getLinearVelocity());
	}
}
