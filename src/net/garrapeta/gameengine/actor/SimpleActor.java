package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.gameengine.GameWorld;
import android.graphics.PointF;

/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class SimpleActor extends Actor implements IAtomicActor {

    // --------------------------------------------------- Variables

    /**
     * Posici�n en el mundo, en las unidades propias del mundo
     */
    protected PointF mWorldPos;

    /**
     * Velocidad lineal, expresadas en unidades l�gicas del mundo por
     * milisegundo
     */
    protected PointF mWorldLinearVelocity;

    // ----------------------------------------------- Constructores

    /**
     * Constructor protegido
     */
    protected SimpleActor(GameWorld gameWorld) {
        this(gameWorld, 0, 0, 0);
    }

    /**
     * Constructor
     * 
     * @param worldPosX
     *            posici�n X en el mundo, en las unidades del mundo
     * @param worldPosY
     *            posici�n Y en el mundo, en las unidades del mundo
     */
    public SimpleActor(GameWorld gameWorld, float worldPosX, float worldPosY) {
        this(gameWorld, 0, worldPosX, worldPosY);
    }

    /**
     * Constructor
     * 
     * @param worldPosX
     *            posici�n X en el mundo, en las unidades del mundo
     * @param worldPosY
     *            posici�n Y en el mundo, en las unidades del mundo
     */
    public SimpleActor(GameWorld gameWorld, int zIndex, float worldPosX, float worldPosY) {
        super(gameWorld, zIndex);

        mWorldPos = new PointF();
        mWorldLinearVelocity = new PointF();

        mWorldPos.x = worldPosX;
        mWorldPos.y = worldPosY;
    }

    // -------------------------------------------M�todos de Actor

    @Override
    public void processFrame(float frameTime) {
        updatePosition(frameTime);
    }

    // ------------------------------------------- M�todos de IAtomicActor
    @Override
    public PointF getWorldPos() {
        return mWorldPos;
    }

    @Override
    public void setWorldPos(float posX, float posY) {
        mWorldPos.x = posX;
        mWorldPos.y = posY;
    }

    @Override
    protected void dispose() {
        super.dispose();
        mWorldPos = null;
        mWorldLinearVelocity = null;
    }

    // -------------------------------------------M�todos propios

    public void setLinearVelocity(float xVel, float yVel) {
        mWorldLinearVelocity.x = xVel;
        mWorldLinearVelocity.y = yVel;
    }

    /**
     * Actualiza la posici�n, teniendo en cuenta la velocidad del actor y el
     * tiempo transcurrido
     * 
     * @param frameTime
     */
    public void updatePosition(float frameTime) {
        mWorldPos.x += mWorldLinearVelocity.x * (frameTime / 1000);
        mWorldPos.y += mWorldLinearVelocity.y * (frameTime / 1000);
    }

}
