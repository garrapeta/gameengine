package com.garrapeta.gameengine.module;



public abstract class LevelActionsModule<V, P> extends LoadedLevelActionsModule<V, V, P> {

	/**
	 * Constructor
	 * @param minimumLevel
	 */
	public LevelActionsModule(short minimumLevel) {
		super(minimumLevel);
	}
	
	@Override
	protected final V obtain(V value) {
		return value;
	}
	
	@Override
	protected void onRelease(V value) {
		// nothing
	}

}
