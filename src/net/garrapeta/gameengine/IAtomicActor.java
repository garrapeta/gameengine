package net.garrapeta.gameengine;

import android.graphics.PointF;

public interface IAtomicActor {

	/**
	 * @return posición en el mundo, en unidades del mundo
	 */
	public abstract PointF getWorldPos();
	
	public abstract void setWorldPos(float x, float y);
	
}
