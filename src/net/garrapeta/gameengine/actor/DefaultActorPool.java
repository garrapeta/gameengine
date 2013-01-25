package net.garrapeta.gameengine.actor;

import java.lang.reflect.Constructor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.gameengine.GameWorld;
import android.util.Log;

import com.badlogic.gdx.utils.Pool.Poolable;

public class DefaultActorPool<T extends Actor & Poolable , S extends GameWorld > extends ActorPool<T , S> {

    private final Class<T> mPoolableActorClass;
    private final Class<S> mGameWorldClass;
    
    public DefaultActorPool(Class<T> actorPoolableClass, Class<S> gameWorldClass, S gameworld, int initialCapacity, int max) {
        super(gameworld, initialCapacity, max);
        mPoolableActorClass = actorPoolableClass;
        mGameWorldClass = gameWorldClass;
    }

    public DefaultActorPool(Class<T> actorPoolableClass, Class<S> gameWorldClass, S gameworld, int initialCapacity) {
        super(gameworld, initialCapacity);
        mPoolableActorClass = actorPoolableClass;
        mGameWorldClass = gameWorldClass;
    }

    public DefaultActorPool(Class<T> actorPoolableClass, Class<S> gameWorldClass, S gameworld) {
        super(gameworld);
        mPoolableActorClass = actorPoolableClass;
        mGameWorldClass = gameWorldClass;
    }

    @Override
    public T newObject(GameWorld gameWorld) {
        T actor = null;
        try {
            Constructor<T> constructor = mPoolableActorClass.getConstructor(mGameWorldClass);
            actor = constructor.newInstance(mGameWorld);
        } catch (Exception e) {
            Log.e(GameWorld.LOG_SRC, "Error when " + this.getClass().getSimpleName() + " tried to create a new " + mPoolableActorClass.getSimpleName() + " object ", e);
        }
        return actor;
    }

}
