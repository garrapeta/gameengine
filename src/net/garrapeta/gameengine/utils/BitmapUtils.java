package net.garrapeta.gameengine.utils;

import net.garrapeta.gameengine.GameWorld;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BitmapUtils {

    /**
     * Gets a snapshot of the world.
     * Must be called from the game loop.
     * 
     * @param world
     * @param config
     * @return bitmap with the snapshot
     */
    public Bitmap getSnapShot(GameWorld world, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(world.mView.getWidth(), world.mView.getHeight(), config);
        Canvas canvas = new Canvas(bitmap);
        world.drawWorld(canvas);
        return bitmap;
    }
    
}
