package com.garrapeta.gameengine;

import android.graphics.Canvas;

/**
 * Actor del juego
 * 
 * @author GaRRaPeTa
 */
public abstract class Actor<T extends GameWorld> {

    // --------------------------------------------------- Variables

    /** Mundo del actor */
    private T mWorld;

    /**
     * Z-index del actor. Cuanto mayor, m�s en primer plano.
     */
    private int mZIndex = 0;

    /**
     * If the actor is initialised. EveryActor needs to be initialised before
     * being added to the world.
     */
    private boolean mIsInitted = false;

    // ----------------------------------------------- Constructores

    /**
     * Constructor
     */
    public Actor(T world) {
        this(world, 0);
    }

    /**
     * Constructor
     */
    public Actor(T world, int zIndex) {
        mWorld = world;
        mZIndex = zIndex;
    }

    // ------------------------------------------- Getters y Setters

    // ----------------------------------------------------- Methods

    /**
     * @return the world of the actor
     */
    public T getWorld() {
        return mWorld;
    }

    /**
     * Sets the actor as initialised, so it is ready to be added to the world.
     * This method has to be called before the actor is added to the world. This
     * should be called from any method used to initialise the actor in a custom
     * way.
     * 
     * @return this actor
     * @throws IllegalStateException
     *             if the actor was already initialised
     */
    public final Actor<T> setInitted() {
        assertNotInnited();
        mIsInitted = true;
        return this;
    }

    /**
     * Sets the actor as not initialised, so it is ready to be reused.
     * 
     * @return this actor
     * @throws IllegalStateException
     *             if the actor was not initialised
     */
    public final Actor<T> setNotInitted() {
        assertInnited();
        mIsInitted = false;
        return this;
    }

    /**
     * Asserts the actor is initted.
     * 
     * @throws IllegalStateException
     *             if the actor was not initialised
     */
    public final void assertInnited() {
        if (!mIsInitted) {
            throw new IllegalStateException(this + " is  not initialised yet");
        }
    }

    /**
     * Asserts the actor is not initted.
     * 
     * @throws IllegalStateException
     *             if the actor was initialised
     */
    public final void assertNotInnited() {
        if (mIsInitted) {
            throw new IllegalStateException(this + " was already initialised");
        }
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

    /** método ejecutado cuando el actor entra en el mundo */
    protected void onAddedToWorld() {
    }

    void doOnRemovedFromWorld() {
        onRemovedFromWorld();
        // Do not call dispose here, as the pool may recycle it.
        // dispose();
    }

    /**
     * método dispose, ejecutado cuando el actor se quita del mundo
     */
    protected void onRemovedFromWorld() {
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
        mWorld = null;
    }

}
