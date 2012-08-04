package net.garrapeta.gameengine.box2d;

import android.graphics.Canvas;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Interfaz para pintar un body.
 * Los métodos que implementen esta interfaz serán "estrategias" de pintado 
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
