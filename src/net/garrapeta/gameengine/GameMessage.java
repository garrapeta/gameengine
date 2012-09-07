package net.garrapeta.gameengine;

public abstract class GameMessage {

    protected int mPriority;
    protected static final int MESSAGE_PRIORITY_DEFAULT = 0;
    protected static final int MESSAGE_PRIORITY_MAX = Integer.MIN_VALUE;

    public GameMessage() {
        super();
    }

    public abstract void onPosted(GameWorld world);

    public abstract void doInGameLoop(GameWorld world);

    protected final int getPriority() {
        return mPriority;
    }

}