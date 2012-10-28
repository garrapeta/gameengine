package net.garrapeta.gameengine;

public abstract class GameMessage {

    protected int mPriority;
    protected static final int MESSAGE_PRIORITY_DEFAULT = 0;
    protected static final int MESSAGE_PRIORITY_MAX = Integer.MIN_VALUE;

    private float mDelay;
    public GameMessage() {
        super();
        mDelay = 0;
    }

    public abstract void onPosted(GameWorld world);

    public final boolean isReadyToBeDispatched(float lastFrameLength) {
        if (mDelay > 0) {
            mDelay -= lastFrameLength;
        }
        return mDelay <= 0;
    }
 
    public abstract void doInGameLoop(GameWorld world);

    protected final int getPriority() {
        return mPriority;
    }

    void setDelay(float delay) {
        mDelay = delay;
    }

}