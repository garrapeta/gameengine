package net.garrapeta.gameengine.actor;

import android.graphics.PointF;

public interface IAtomicActor {

	/**
	 * @return posici�n en el mundo, en unidades del mundo
	 */
	public abstract PointF getWorldPos();
	
	public abstract void setWorldPos(float x, float y);
	
}
