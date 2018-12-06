package cn.linhome.lib.utils.extend;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Bitmap的lru算法缓存管理
 */
public class FBitmapLruCache extends LruCache<String, Bitmap>
{
    private static FBitmapLruCache sInstance;

    private FBitmapLruCache()
    {
        super((int) ((double) Runtime.getRuntime().maxMemory() / 16));
    }

    public static FBitmapLruCache getInstance()
    {
        if (sInstance == null)
        {
            synchronized (FBitmapLruCache.class)
            {
                if (sInstance == null)
                {
                    sInstance = new FBitmapLruCache();
                }
            }
        }
        return sInstance;
    }

    @Override
    protected final int sizeOf(String key, Bitmap value)
    {
        return value.getByteCount();
    }
}
