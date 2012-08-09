package net.garrapeta.gameengine;

public abstract class GameMessage {

    private int mPriority;
    
    static final int MESSAGE_PRIORITY_DEFAULT = 0;
    static final int MESSAGE_PRIORITY_MAX = Integer.MIN_VALUE;
    
    public GameMessage() {
        this(MESSAGE_PRIORITY_DEFAULT);
    }

    public GameMessage(int priority) {
        mPriority = priority;
    }
    
    
    public abstract void process();
    
    final int getPriority() {
        return mPriority;
    }
}
