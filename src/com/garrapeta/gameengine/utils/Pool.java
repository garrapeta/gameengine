package com.garrapeta.gameengine.utils;

import com.badlogic.gdx.utils.Array;


/**
 * 
 * Wrapper of {@link com.badlogic.gdx.utils.Pool} to add debug methods and
 * to force all the clients object to be {@link com.badlogic.gdx.utils.Pool.Poolable}
 *
 * @param <T>
 */
public abstract class Pool<T extends  com.badlogic.gdx.utils.Pool.Poolable> {

    private final com.badlogic.gdx.utils.Pool<T> mPool;

    private int mAllocations = 0;
    
    private int mObtainings = 0;

    /**
     * {@link com.badlogic.gdx.utils.Pool#Pool()}
     */
    public Pool() {
        mPool = new PoolWrapper();
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#Pool()
     * 
     * @param initialCapacity
     */
    public Pool(int initialCapacity) {
        mPool = new PoolWrapper(initialCapacity);
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#Pool()
     * 
     * @param initialCapacity
     * @param max
     */
    public Pool(int initialCapacity, int max) {
        mPool = new PoolWrapper(initialCapacity, max);
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#newObject()
     * @return
     */
    protected abstract T newObject();

    /**
     * {@link com.badlogic.gdx.utils.Pool#obtain()
     * @return
     */
    public T obtain() {
        return mPool.obtain();
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#free(Object)
     * @param object
     */
    public void free(T object) {
        mPool.free(object);
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#free(Array)
     * @param objects
     */
    public void free(Array<T> objects) {
        mPool.free(objects);
    }

    /**
     * {@link com.badlogic.gdx.utils.Pool#clear()
     */
    public void clear() {
        mPool.clear();
    }

    /**
     * @return number of objects obtained with this pool
     */
    public int getObtainings() {
        return mObtainings;
    }

    /**
     * @return number of objects allocated by this pool
     */
    public int getAllocations() {
        return mAllocations;
    }

    /**
     * @return info String
     */
    public String getDebugString() {
        return("Obtainings/Allocations: " + getObtainings() + "/" + getAllocations());
    }
 
    /**
     *  Extension of {@link com.badlogic.gdx.utils.Pool}
     */
    private class PoolWrapper extends com.badlogic.gdx.utils.Pool<T>{

        public PoolWrapper() {
            super();
        }

        public PoolWrapper(int initialCapacity, int max) {
            super(initialCapacity, max);
        }

        public PoolWrapper(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        protected T newObject() {
            mAllocations++;
            return Pool.this.newObject();
        }

        @Override
        public T obtain() {
            mObtainings++;
            return super.obtain();
        }
    }
}
