package com.garrapeta.gameengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.SparseArray;


public abstract class LevelBasedResourcesManager<K, V, P> {

	private final SparseArray<ResourceData> mResourceData;
	private final short mMinimumLevel;
	private Random mRandom;
	
	/**
	 * Constructor
	 * @param minimumLevel
	 */
	public LevelBasedResourcesManager(short minimumLevel) {
		mResourceData = new SparseArray<ResourceData>();
		mMinimumLevel = minimumLevel;
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

	public final boolean executeOverOneResourceForKey(short key) {
		return executeOverOneResourceForKey(key, (P[])null);
	}

	public final boolean executeOverOneResourceForKey(short key, P... params) {
		ResourceData resourceData = mResourceData.get(key);
    	if (resourceData == null) {
    		throw new IllegalArgumentException("No resource loaded for: " + key);
    	}
    	return resourceData.executeOverOne(params);
	}
	
	public final boolean executeOverAllResourcesForKey(short key) {
		return executeOverAllResourcesForKey(key, (P[])null);
	}

	public final boolean executeOverAllResourcesForKey(short key, P... params) {
		ResourceData resourceData = mResourceData.get(key);
    	if (resourceData == null) {
    		throw new IllegalArgumentException("No resource loaded for: " + key);
    	}
    	return executeOverAllResources(resourceData, params);
	}
	
	public final void executeOverAllResources() {
		executeOverAllResources((P[])null);
	}

	public final void executeOverAllResources(P... params) {
        if (mResourceData != null) {
            for (int i = 0; i < mResourceData.size(); i++) {
            	ResourceData resourceData = mResourceData.get(i);
            	executeOverAllResources(resourceData, params);
            }
        }
	}
	
	private final boolean executeOverAllResources(ResourceData data, P... params) {
  		return data.executeOverAll(params);
	}
	
	private final boolean shouldBeExecuted(short level) {
		return level >= mMinimumLevel;
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
			if (shouldBeExecuted(mLevel)) {
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
		
		private boolean executeOverOne(P[] params) {
			if (shouldBeExecuted(mLevel)) {
				V resource = getOneResource();
    			onExecute(resource, params);
    			return true;
			}
			return false;
		}
	
		private boolean executeOverAll(P[] params) {
			if (shouldBeExecuted(mLevel)) {
				if (mResource != null) {
	            	onExecute(mResource, params);
	            } else if (mResources != null) {
	                for (V resource : mResources) {
	                	onExecute(resource, params);
	                }
	            }
	            return true;
			}
			return false;
		}
		
		private void release() {
            if (mResource != null) {
            	LevelBasedResourcesManager.this.onRelease(mResource);
            	mResource = null;
            }
            if (mResources != null) {
                for (V resource : mResources) {
                	LevelBasedResourcesManager.this.onRelease(resource);
                }
                mResources.clear();
                mResources = null;
            }
		}
	}

}
