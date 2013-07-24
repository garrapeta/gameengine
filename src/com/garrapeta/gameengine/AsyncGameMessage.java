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
        // TODO: do this in an executor to avoid creating threads
        new Thread(new Runnable(){
            @Override
            public void run() {
            	try {
            		doInBackground();
            		world.add(AsyncGameMessage.this);
            	} catch (Throwable t) {
            		Log.e(GameWorld.LOG_SRC, "Error happening in async message", t);
            		world.onError(t);
            	}
            }}).start();
    }

    public abstract void doInBackground();
}
