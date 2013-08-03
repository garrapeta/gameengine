package com.garrapeta.gameengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.SparseArray;


public abstract class LevelBasedResourcesModule<K, V, P> {

	private final LevelBasedModule<V, P> mLevelBaseModule;
	private final SparseArray<ResourceData> mResourceData;
	private Random mRandom;
	
	public LevelBasedResourcesModule(short minimumLevel) {
		mLevelBaseModule = new CustomLevelBasedModule(minimumLevel);
		mResourceData = new SparseArray<ResourceData>();
	}
	
	public final ResourceData create(short level, short key) {
		if (mResourceData.get(key) != null) {
	        throw new IllegalArgumentException("Already exising: " + key);
	    }

		ResourceData resourceData = new ResourceData(level);
		mResourceData.put(key, resourceData);
		return resourceData;
	}
	
	protected final int getCount(short key) {
		ResourceData resourceData = mResourceData.get(key);
		if (resourceData != null) {
	        return resourceData.getCount();
	    }
		return 0;
	}
	
	protected abstract V load(K key);

	public final boolean executeOverOne(short key) {
		return executeOverOne(key, (P[])null);
	}

	public final boolean executeOverOne(short key, P... params) {
		ResourceData resourceData = mResourceData.get(key);
    	if (resourceData == null) {
    		throw new IllegalArgumentException("No resource loaded for: " + key);
    	}
    	if (mLevelBaseModule.shouldBeExecuted(resourceData.mLevel)) {
    		resourceData.executeOverOne(params);
    		return true;
    	}
        return false;
	}
	
	public final boolean executeOverAllOf(short key) {
		return executeOverAllOf(key, (P[])null);
	}

	public final boolean executeOverAllOf(short key, P... params) {
		ResourceData resourceData = mResourceData.get(key);
    	if (resourceData == null) {
    		throw new IllegalArgumentException("No resource loaded for: " + key);
    	}
    	return executeOverAll(resourceData, params);
	}
	
	private final boolean executeOverAll(ResourceData data, P... params) {
    	if (mLevelBaseModule.shouldBeExecuted(data.mLevel)) {
    		data.executeOverAll(params);
    		return true;
    	}
        return false;
	}
	
	public final void executeOverAll() {
		executeOverAll((P[])null);
	}

	public final void executeOverAll(P... params) {
        if (mResourceData != null) {
            for (int i = 0; i < mResourceData.size(); i++) {
            	ResourceData resourceData = mResourceData.get(i);
            	executeOverAll(resourceData, params);
            }
        }
	}
	
	/**
     * Frees resources
     */
	final void releaseAll() {
        if (mResourceData != null) {
            for (int i = 0; i < mResourceData.size(); i++) {
            	ResourceData data = mResourceData.get(i);
            	data.release();
            }
            mResourceData.clear();
        }
    }
	
	protected abstract void onExecute(V resource, P... params);
	
	protected abstract void onRelease(V mResource);

	
	
	private class CustomLevelBasedModule extends LevelBasedModule<V, P> {

		public CustomLevelBasedModule(short minimumLevel) {
			super(minimumLevel);
		}

		@Override
		protected void onExecute(V resource, P... params) {
			LevelBasedResourcesModule.this.onExecute(resource, params);
		}
	}

	public final class ResourceData {
    	private final short mLevel;
    	private V mResource;
    	private List<V> mResources;
    	
		private ResourceData(short level) {
			super();
			mLevel = level;
		}
		
		public final ResourceData add(K key) {
			V resource = null;
			if (mLevelBaseModule.shouldBeExecuted(mLevel)) {
	    		resource = load(key);
	    		addResource(resource);
	    	} 
			return this;
	    	
		}
		
		void addResource(V resource) {
            if (mResource == null && mResources == null) {
            	mResource = resource;
            } else {
                if (mResources == null) {
                	mResources = new ArrayList<V>();
                	mResources.add(mResource);
                	mResource = null;
                	mRandom = new Random();
                }
                mResources.add(resource);
            }
		}
		
		public final int getCount() {
			if (mResource != null) {
				return 1;
			} else if (mResources != null) {
				return mResources.size();
			} else {
				return 0;
			}
		}

		private V getOneResource() {
            if (mResource != null) {
                return mResource;
            } else {
            	final int idx = mRandom.nextInt(mResources.size());
                return mResources.get(idx);
            }
		}
		
		private void executeOverOne(P[] params) {
    		V resource = getOneResource();
    		onExecute(resource, params);
		}
	
		private void executeOverAll(P[] params) {
            if (mResource != null) {
            	onExecute(mResource, params);
            }
            if (mResources != null) {
                for (V resource : mResources) {
                	onExecute(resource, params);
                }
            }
		}
		
		private void release() {
            if (mResource != null) {
            	LevelBasedResourcesModule.this.onRelease(mResource);
            	mResource = null;
            }
            if (mResources != null) {
                for (V resource : mResources) {
                	LevelBasedResourcesModule.this.onRelease(resource);
                }
                mResources.clear();
                mResources = null;
            }
		}
	}

}
