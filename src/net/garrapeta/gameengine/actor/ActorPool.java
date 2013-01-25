package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.GameWorld;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import net.garrapeta.gameengine.Actor;


public abstract class ActorPool<T extends Actor & Poolable, S extends GameWorld> extends Pool<T> {

    protected final S mGameWorld;

    /**
     * {@link Pool#Pool()}
     */
    public ActorPool(S gameworld) {
        super();
        mGameWorld = gameworld;
    }

    /**
     * {@link Pool#Pool()
     * 
     * @param initialCapacity
     * @param max
     */
    public ActorPool(S gameworld, int initialCapacity, int max) {
        super(initialCapacity, max);
        mGameWorld = gameworld;
    }

    /**
     * {@link Pool#Pool()
     * 
     * @param initialCapacity
     */
    public ActorPool(S gameworld, int initialCapacity) {
        super(initialCapacity);
        mGameWorld = gameworld;
    }

    @Override
    public final T newObject() {
        return newObject(mGameWorld);
    }
 
 
    /**
     * {@link Pool#newObject()}
     * 
     * @param gameWorld
     * @return
     */
    protected abstract T newObject(GameWorld gameWorld);


}
