package com.garrapeta.gameengine.module;



public abstract class LeveledActionsModule<V, P> extends LoadedLeveledActionsModule<V, V, P> {

	/**
	 * Constructor
	 * @param minimumLevel
	 */
	public LeveledActionsModule(short minimumLevel) {
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
