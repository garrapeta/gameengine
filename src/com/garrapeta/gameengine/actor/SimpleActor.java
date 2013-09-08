package com.garrapeta.gameengine.actor;

import android.graphics.PointF;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.gameengine.GameWorld;

/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class SimpleActor<T extends GameWorld> extends Actor<T> implements IAtomicActor {

    // --------------------------------------------------- Variables

    /**
     * posición en el mundo, en las unidades propias del mundo
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
    protected SimpleActor(T world) {
        this(world, 0, 0, 0);
    }

    /**
     * Constructor
     * 
     * @param worldPosX
     *            posición X en el mundo, en las unidades del mundo
     * @param worldPosY
     *            posición Y en el mundo, en las unidades del mundo
     */
    public SimpleActor(T world, float worldPosX, float worldPosY) {
        this(world, 0, worldPosX, worldPosY);
    }

    /**
     * Constructor
     * 
     * @param worldPosX
     *            posición X en el mundo, en las unidades del mundo
     * @param worldPosY
     *            posición Y en el mundo, en las unidades del mundo
     */
    public SimpleActor(T world, int zIndex, float worldPosX, float worldPosY) {
        super(world, zIndex);

        mWorldPos = new PointF();
        mWorldLinearVelocity = new PointF();

        mWorldPos.x = worldPosX;
        mWorldPos.y = worldPosY;
    }

    // -------------------------------------------métodos de Actor

    @Override
    public void processFrame(float frameTime) {
        updatePosition(frameTime);
    }

    // ------------------------------------------- métodos de IAtomicActor
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

    // -------------------------------------------métodos propios

    public void setLinearVelocity(float xVel, float yVel) {
        mWorldLinearVelocity.x = xVel;
        mWorldLinearVelocity.y = yVel;
    }

    /**
     * Actualiza la posición, teniendo en cuenta la velocidad del actor y el
     * tiempo transcurrido
     * 
     * @param frameTime
     */
    public void updatePosition(float frameTime) {
        mWorldPos.x += mWorldLinearVelocity.x * (frameTime / 1000);
        mWorldPos.y += mWorldLinearVelocity.y * (frameTime / 1000);
    }

}
