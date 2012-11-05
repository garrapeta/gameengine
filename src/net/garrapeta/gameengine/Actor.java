package net.garrapeta.gameengine;

import android.graphics.Canvas;

/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class Actor {

    // --------------------------------------------------- Variables

    /** Mundo del actor */
    protected GameWorld mGameWorld;

    /**
     * Z-index del actor. Cuanto mayor, m�s en primer plano.
     */
    private int mZIndex = 0;

    // ----------------------------------------------- Constructores

    /**
     * Constructor protegido
     */
    protected Actor(GameWorld gameWorld) {
        this(gameWorld, 0);
    }

    /**
     * Constructor protegido
     */
    protected Actor(GameWorld gameWorld, int zIndex) {
        this.mGameWorld = gameWorld;
        this.mZIndex = zIndex;
    }

    // ------------------------------------------- Getters y Setters

    // ----------------------------------------------------- M�todos
    /**
     * Pinta el actor
     * 
     * @param canvas
     */
    protected abstract void draw(Canvas canvas);

    /**
     * Realiza la l�gica del frame
     * 
     * @param gameTimeStep
     *            tiempo del frame anterior, en ms
     */
    protected abstract void processFrame(float gameTimeStep);

    /** M�todo ejecutado cuando el actor entra en el mundo */
    protected void onAddedToWorld() {
    }

    void doOnRemovedFromWorld() {
        onRemovedFromWorld();
        dispose();
    }

    
    /**
     * M�todo dispose, ejecutado cuando el actor se quita del mundo
     */
    public void onRemovedFromWorld() {
    }

    /**
     * Devuelve zIndex
     */
    protected final int getZIndex() {
        return mZIndex;
    }

    void doOnAddedToWorld() {
        onAddedToWorld();
    }

    /**
     * Frees resources and nullifies references
     */
    protected void dispose() {
        mGameWorld = null;
    }

}
