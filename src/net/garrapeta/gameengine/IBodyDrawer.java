package net.garrapeta.gameengine;

import android.graphics.Canvas;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Interfaz para pintar un body.
 * Los m�todos que implementen esta interfaz ser�n "estrategias" de pintado 
 * de bodys.
 * 
 * @author GaRRaPeTa
 */
public interface IBodyDrawer {

	/**
	 * Pinta el body
	 * @param canvas
	 * @param body
	 */
	public void draw(Canvas canvas, Box2DActor actor, Body body);

}
