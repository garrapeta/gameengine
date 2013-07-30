package com.garrapeta.gameengine;

import android.util.Log;

public abstract class AsyncGameMessage extends GameMessage {

    public AsyncGameMessage() {
        this(MESSAGE_PRIORITY_DEFAULT);
    }

    public AsyncGameMessage(int priority) {
        mPriority = priority;
    }

    @Override
    public final void onPosted(final GameWorld world) {
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
            	synchronized (world) { 
	            	if (world.isRunning()) {
		            	try {
		            		doInBackground();
		            		world.add(AsyncGameMessage.this);
		            	} catch (Throwable t) {
		            		Log.e(GameWorld.LOG_SRC, "Error happening in async message", t);
		            		world.onError(t);
		            	}
	            	}
            	}
            }};
            world.executeAsynchronously(runnable);
    }

    public abstract void doInBackground();
}
