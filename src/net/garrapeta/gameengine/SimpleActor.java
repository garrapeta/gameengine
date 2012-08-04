package net.garrapeta.gameengine;


import android.graphics.PointF;

/**
 * Actor del juego
 * @author GaRRaPeTa
 */
public abstract class SimpleActor extends Actor implements IAtomicActor {
	
	// --------------------------------------------------- Variables

	/**
	 *  Posición en el mundo, en las unidades propias del mundo
	 */
	protected PointF worldPos;

	/**
	 *  Velocidad lineal, expresadas en unidades lógicas del mundo por milisegundo 
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
	 * @param worldPosX posición X en el mundo, en las unidades del mundo
	 * @param worldPosY posición Y en el mundo, en las unidades del mundo
	 */
	public SimpleActor(GameWorld gameWorld, float worldPosX, float worldPosY) {
		this(gameWorld, 0, worldPosX, worldPosY);
	}
	
	/**
	 * Constructor
	 * @param worldPosX posición X en el mundo, en las unidades del mundo
	 * @param worldPosY posición Y en el mundo, en las unidades del mundo
	 */
	public SimpleActor(GameWorld gameWorld, int zIndex, float worldPosX, float worldPosY) {
		super(gameWorld, zIndex);
		
		this.worldPos 				= new PointF();
		this.worldLinearVelocity 	= new PointF();
		
		worldPos.x = worldPosX;
		worldPos.y = worldPosY;
	}
	
	// -------------------------------------------Métodos de Actor
	
	@Override
	public void doLogic(float frameTime) {
		updatePosition(frameTime);
	}
	
	// ------------------------------------------- Métodos de IAtomicActor
	@Override
	public PointF getWorldPos() {
		return worldPos;
	}

	@Override
	public void setWorldPos(float posX, float posY) {
		worldPos.x = posX;
		worldPos.y = posY;
	}
	
	// -------------------------------------------Métodos propios
	
	/**
	 * Actualiza la posición, teniendo en cuenta la velocidad del actor
	 * y el tiempo transcurrido
	 * @param frameTime
	 */
	public void updatePosition(float frameTime) {
		worldPos.x += worldLinearVelocity.x * frameTime;
		worldPos.y += worldLinearVelocity.y * frameTime;
	}
	
}
