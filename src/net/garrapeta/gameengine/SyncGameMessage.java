package net.garrapeta.gameengine;

public abstract class SyncGameMessage extends GameMessage {

    public SyncGameMessage() {
        this(MESSAGE_PRIORITY_DEFAULT);
    }

    public SyncGameMessage(int priority) {
        mPriority = priority;
    }

    @Override
    public final void onPosted(GameWorld world) {
        world.add(this);
    }
}