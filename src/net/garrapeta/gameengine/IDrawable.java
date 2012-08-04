package net.garrapeta.gameengine;

import android.graphics.Canvas;

public interface IDrawable {

	// ----------------------------------------------------- M�todos
	/**
	 * Pinta el drawable
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);
}
