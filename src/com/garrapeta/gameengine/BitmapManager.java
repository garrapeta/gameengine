package com.garrapeta.gameengine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.util.SparseArray;

import com.garrapeta.gameengine.utils.L;

/**
 * Module / helper to deal with bitmaps
 */
public class BitmapManager {

    // -------------------------------- Static vars

    /** Source trazas de log */
    public static final String TAG = "bitmap";

    // ------------------------------ Instance vars

    private SparseArray<Bitmap> mBitmaps;

    /**
     * Gets the bitmap with the specified resource id
     * 
     * @param resourceId
     * @return the bitmap
     */
    public Bitmap getBitmap(int resourceId) {
        Bitmap bmp = mBitmaps.get(resourceId);
        if (bmp == null) {
            throw new IllegalStateException("Bitmap with resource id " + resourceId + " was not loaded");
        }
        return bmp;
    }

    /**
     * Loads the bitmap with the specified resource id
     * 
     * @param resourceId
     * @return the loaded bitmap
     */
    public Bitmap loadBitmap(Resources resources, int resourceId) {
        if (L.sEnabled)
            Log.d(TAG, "Loading bitmap: " + resourceId);

        if (mBitmaps == null) {
            mBitmaps = new SparseArray<Bitmap>();
        }
        Options o = new Options();
        Bitmap bmp = BitmapFactory.decodeResource(resources, resourceId, o);
        mBitmaps.append(resourceId, bmp);
        if (L.sEnabled)
            Log.v(TAG, "Loaded " + resourceId + ". " + mBitmaps.size() + " bitmaps in memory");
        return bmp;
    }

    /**
     * Releases the bitmap with the specified resource id
     * 
     * @param resourceId
     */
    public void releaseBitmap(int resourceId) {
        if (L.sEnabled)
            Log.d(TAG, "Releasing bitmap: " + resourceId);
        Bitmap bmp = getBitmap(resourceId);
        mBitmaps.delete(resourceId);
        if (bmp != null) {
            bmp.recycle();
            mBitmaps.delete(resourceId);
        }
        if (L.sEnabled)
            Log.v(TAG, "Released " + resourceId + ". " + mBitmaps.size() + " bitmaps in memory");
    }

    /**
     * Releases of the resources
     */
    public void releaseAll() {
        if (L.sEnabled)
            Log.i(TAG, "Releasing all the bitmaps");
        if (mBitmaps != null) {
            int key = 0;
            int size = mBitmaps.size();
            for (int i = size - 1; i >= 0; i--) {
                key = mBitmaps.keyAt(i);
                releaseBitmap(key);
            }
            mBitmaps.clear();
            mBitmaps = null;
        }
    }
}
