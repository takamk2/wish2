package takamk2.local.wish2.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by takamk2 on 17/01/20.
 * <p>
 * The Edit Fragment of Base Class.
 */

public class BitmapCache {

    private static final String LOGTAG = BitmapCache.class.getSimpleName();

    private LruCache<String, Bitmap> mMemoryCache;

    public BitmapCache() {

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        Bitmap old = null;
        synchronized (mMemoryCache) {
            if (getBitmap(url) == null) {
                Log.d(LOGTAG, "putBitmap - DEBUG: put bitmap : url=" + url);
                old = mMemoryCache.put(url, bitmap);
            } else {
                Log.d(LOGTAG, "putBitmap - DEBUG: bitmap is already exists : url=" + url);
            }
        }

        if (old != null) {
            if (!old.isRecycled()) {
                old.recycle();
            }
            old = null;
            Log.d(LOGTAG, "putBitmap - DEBUG: bitmap has been recycled");
        }
    }
}
