package net.garrapeta.gameengine;

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
                doInBackground();
                world.add(AsyncGameMessage.this);
            }}).start();
    }

    public abstract void doInBackground();
}
