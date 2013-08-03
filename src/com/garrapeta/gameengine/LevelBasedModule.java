package com.garrapeta.gameengine;

public abstract class LevelBasedModule<V, P> {

	private final short mMinimumLevel;

	public LevelBasedModule(short minimumLevel) {
		mMinimumLevel = minimumLevel;
	}
	
	public final boolean execute(short level, V value, P... params) {
		if (shouldBeExecuted(level)) {
			onExecute(value, params);
			return true;
		}
		return false;
	}
	
	protected abstract void onExecute(V value, P... params);

	protected final boolean shouldBeExecuted(short level) {
		return level >= mMinimumLevel;
	}

}
