package net.garrapeta.gameengine;


import android.graphics.PointF;

/**
 * Actor del juego
 * @author GaRRaPeTa
 */
public abstract class SimpleActor extends Actor implements IAtomicActor {
	
	// --------------------------------------------------- Variables

	/**
	 *  Posici�n en el mundo, en las unidades propias del mundo
	 */
	protected PointF worldPos;

	/**
	 *  Velocidad lineal, expresadas en unidades l�gicas del mundo por milisegundo 
	 */
	protected PointF worldLinearVelocity;
	
	// ----------------------------------------------- Constructores
	
	/**
	 * Constructor protegido
	 */
	protected SimpleActor(GameWorld gameWorld) {
		this(gameWorld, 0, 0, 0);
	}
	
	/**
	 * Constructor
	 * @param worldPosX posici�n X en el mundo, en las unidades del mundo
	 * @param worldPosY posici�n Y en el mundo, en las unidades del mundo
	 */
	public SimpleActor(GameWorld gameWorld, float worldPosX, float worldPosY) {
		this(gameWorld, 0, worldPosX, worldPosY);
	}
	
	/**
	 * Constructor
	 * @param worldPosX posici�n X en el mundo, en las unidades del mundo
	 * @param worldPosY posici�n Y en el mundo, en las unidades del mundo
	 */
	public SimpleActor(GameWorld gameWorld, int zIndex, float worldPosX, float worldPosY) {
		super(gameWorld, zIndex);
		
		this.worldPos 				= new PointF();
		this.worldLinearVelocity 	= new PointF();
		
		worldPos.x = worldPosX;
		worldPos.y = worldPosY;
	}
	
	// -------------------------------------------M�todos de Actor
	
	@Override
	public void doLogic(float frameTime) {
		updatePosition(frameTime);
	}
	
	// ------------------------------------------- M�todos de IAtomicActor
	@Override
	public PointF getWorldPos() {
		return worldPos;
	}

	@Override
	public void setWorldPos(float posX, float posY) {
		worldPos.x = posX;
		worldPos.y = posY;
	}
	
	// -------------------------------------------M�todos propios
	
	public void setLinearVelocity(float xVel, float yVel) {
	    worldLinearVelocity.x = xVel;
	    worldLinearVelocity.y = yVel;
	}
	
	
	/**
	 * Actualiza la posici�n, teniendo en cuenta la velocidad del actor
	 * y el tiempo transcurrido
	 * @param frameTime
	 */
	public void updatePosition(float frameTime) {
		worldPos.x += worldLinearVelocity.x * (frameTime / 1000);
		worldPos.y += worldLinearVelocity.y * (frameTime / 1000);
	}
	
}
