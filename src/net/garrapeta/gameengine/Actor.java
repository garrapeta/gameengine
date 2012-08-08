package net.garrapeta.gameengine;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class Actor {

    // --------------------------------------------------- Variables

    /** Mundo del actor */
    public GameWorld gameWorld;

    /**
     * Z-index del actor. Cuanto mayor, m�s en primer plano.
     */
    private int zIndex = 0;

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
        this.gameWorld = gameWorld;
        this.zIndex = zIndex;
    }

    // ------------------------------------------- Getters y Setters

    // ----------------------------------------------------- M�todos
    /**
     * Pinta el actor
     * 
     * @param canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * Realiza la l�gica del frame
     * 
     * @param gameTimeStep
     *            tiempo del frame anterior, en ms
     */
    public abstract void doLogic(float gameTimeStep);

    /** M�todo ejecutado cuando el actor entra en el mundo */
    public void onAddedToWorld() {
    }

    /**
     * M�todo dispose, ejecutado cuando el actor se quita del mundo
     */
    public void onRemovedFromWorld() {
    }

    /**
     * Devuelve zIndex
     */
    public final int getZIndex() {
        return zIndex;
    }

    /**
     * Devuelve zIndex
     */
    public final void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        gameWorld.onZindexChanged(this);
    }

}
