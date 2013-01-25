package net.garrapeta.gameengine;


import com.badlogic.gdx.utils.Pool.Poolable;

import android.graphics.Canvas;


/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class Actor implements Poolable {

    // --------------------------------------------------- Variables

    /** Mundo del actor */
    protected GameWorld mGameWorld;

    /**
     * Z-index del actor. Cuanto mayor, m�s en primer plano.
     */
    private int mZIndex = 0;

    /**
     * If the actor is initialised. EveryActor needs to be initialised before being added to the world. 
     */
    boolean mIsInitted = false;

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

    // ----------------------------------------------------- Methods

    /**
     * Sets the actor as initialised, so it is ready to be added to the world.
     * This method has to be called before the actor is added to the world.
     * This should be called from any method used to initialise the actor in a custom way.
     * 
     * @return this actor
     * @throws IllegalStateException if the actor was already initialised
     */
    public final Actor setInitted() {
        if (mIsInitted) {
            throw new IllegalStateException(this + " was already initialised");
        }
        mIsInitted = true;
        return this;
    }
 

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
        // Do not call dispose here, as the pool may recycle it.
        //  dispose();
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

   // ----------------------------------------------- Methods from Poolable

    @Override
    public final void reset() {
        if (!mIsInitted) {
            throw new IllegalStateException(this + " was not initialised");
        }
        mIsInitted = false;
        // TODO: notify some kind of "onResettedByPool"
    }

}
