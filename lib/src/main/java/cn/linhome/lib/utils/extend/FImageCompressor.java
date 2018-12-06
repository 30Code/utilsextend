package cn.linhome.lib.utils.extend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片压缩处理类
 */
public class FImageCompressor
{
    private static final String COMPRESSED_FILE_DIR_NAME = "compressed_image";

    private int mMaxFileSize = 1024 * 1024;
    private int mMaxWidth;
    private int mMaxHeight;

    private Context mContext;
    private File mCompressedFileDir;
    private List<File> mListCompressedFile;

    private Exception mException;

    public FImageCompressor(Context context)
    {
        if (context == null)
        {
            throw new NullPointerException("context must not be null");
        }
        mContext = context.getApplicationContext();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mMaxWidth = displayMetrics.widthPixels;
        mMaxHeight = displayMetrics.heightPixels;
    }

    private File getCompressedFileDir()
    {
        if (mCompressedFileDir == null)
        {
            File cacheDir = mContext.getExternalCacheDir();
            if (cacheDir == null)
            {
                cacheDir = mContext.getCacheDir();
            }
            mCompressedFileDir = new File(cacheDir, COMPRESSED_FILE_DIR_NAME);
        }
        if (!mCompressedFileDir.exists())
        {
            mCompressedFileDir.mkdirs();
        }
        return mCompressedFileDir;
    }

    /**
     * 设置图片的最大宽度
     *
     * @param maxWidth
     */
    public void setMaxWidth(int maxWidth)
    {
        mMaxWidth = maxWidth;
    }

    /**
     * 设置图片最大高度
     *
     * @param maxHeight
     */
    public void setMaxHeight(int maxHeight)
    {
        mMaxHeight = maxHeight;
    }

    /**
     * 设置压缩后图片文件大小的最大值
     *
     * @param maxFileSize
     */
    public void setMaxFileSize(int maxFileSize)
    {
        if (maxFileSize > 0)
        {
            mMaxFileSize = maxFileSize;
        }
    }

    /**
     * 按照设置参数压缩
     *
     * @param bitmap 要压缩的bitmap对象
     * @return 压缩好的bitmap对象
     */
    public Bitmap compressBitmapToBitmap(Bitmap bitmap)
    {
        try
        {
            boolean hasScaled = false;
            Bitmap bitmapScaled = scaleBitmapIfNeed(bitmap, mMaxWidth, mMaxHeight);
            if (bitmapScaled != bitmap)
            {
                hasScaled = true;
            }

            Bitmap bitmapCompressed = compressBitmapToFileSize(bitmapScaled, mMaxFileSize, 5);
            if (bitmapCompressed != bitmapScaled && hasScaled)
            {
                bitmapScaled.recycle();
            }
            return bitmapCompressed;
        } catch (Exception e)
        {
            mException = e;
            return null;
        }
    }

    /**
     * 按照设置参数压缩
     *
     * @param bitmap 要压缩的bitmap对象
     * @return 压缩好的图片文件
     */
    public File compressBitmapToFile(Bitmap bitmap)
    {
        Bitmap bitmapCompressed = compressBitmapToBitmap(bitmap);
        File file = bitmapToFile(bitmapCompressed);
        return file;
    }

    /**
     * 按照设置参数压缩
     *
     * @param filePath 要压缩的图片文件路径
     * @return 压缩好的bitmap对象
     */
    public Bitmap compressFileToBitmap(String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, mMaxWidth, mMaxHeight);
        options.inJustDecodeBounds = false;

        try
        {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            Bitmap bitmapCompressed = compressBitmapToBitmap(bitmap);
            if (bitmapCompressed != bitmap)
            {
                bitmap.recycle();
            }
            return bitmapCompressed;
        } catch (Exception e)
        {
            mException = e;
            return null;
        }
    }

    /**
     * 按照设置参数压缩
     *
     * @param filePath 要压缩的图片文件路径
     * @return 压缩好的图片文件
     */
    public File compressFileToFile(String filePath)
    {
        Bitmap bitmapCompressed = compressFileToBitmap(filePath);
        File file = bitmapToFile(bitmapCompressed);
        if (bitmapCompressed != null)
        {
            bitmapCompressed.recycle();
        }
        return file;
    }

    private void addCompressedFile(File file)
    {
        if (mListCompressedFile == null)
        {
            mListCompressedFile = new ArrayList<>();
        }
        mListCompressedFile.add(file);
    }

    public Exception getException()
    {
        return mException;
    }

    /**
     * 删除当前对象保存的压缩文件
     */
    public void deleteCompressedFiles()
    {
        if (mListCompressedFile == null || mListCompressedFile.isEmpty())
        {
            return;
        }
        for (File item : mListCompressedFile)
        {
            item.delete();
        }
    }

    /**
     * 删除目录下所有保存的压缩文件
     */
    public void deleteAllCompressedFile()
    {
        deleteFileOrDir(mCompressedFileDir);
    }

    /**
     * 压缩bitmap到不超过指定的大小(文件大小，非bitmap大小)
     *
     * @param bitmap
     * @param maxSize      指定的最大值
     * @param deltaQuality 当前quality不满足的时候，quality递减的大小
     * @return
     */
    private Bitmap compressBitmapToFileSize(Bitmap bitmap, long maxSize, int deltaQuality)
    {
        if (maxSize <= 0)
        {
            return bitmap;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            int quality = 100;
            while (true)
            {
                if (quality <= 1)
                {
                    break;
                }
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

                if (baos.size() > maxSize)
                {
                    quality -= deltaQuality;
                    continue;
                } else
                {
                    // 压缩到指定大小成功
                    byte[] arrByte = baos.toByteArray();
                    return BitmapFactory.decodeByteArray(arrByte, 0, arrByte.length);
                }
            }
        } finally
        {
            closeQuietly(baos);
        }
        return null;
    }

    protected int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight)
    {
        if (maxWidth <= 0 || maxHeight <= 0)
        {
            return 1;
        }

        int inSampleSize = 1;

        final int height = options.outHeight;
        final int width = options.outWidth;

        if (width > maxWidth || height > maxHeight)
        {
            if (width > height)
            {
                inSampleSize = Math.round((float) height / (float) maxHeight);
            } else
            {
                inSampleSize = Math.round((float) width / (float) maxWidth);
            }

            final float totalPixels = width * height;
            final float maxTotalPixels = maxWidth * maxHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > maxTotalPixels)
            {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * 如果bitmap的宽或者高超过设置的最大宽或者高，则进行压缩
     *
     * @param bitmap
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return
     */
    protected Bitmap scaleBitmapIfNeed(Bitmap bitmap, final int maxWidth, final int maxHeight)
    {
        if (maxWidth <= 0 || maxHeight <= 0)
        {
            return bitmap;
        }

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > maxHeight || height > maxHeight)
        {
            int[] arrTarget = calculateFitSize(new int[]{width, height}, new int[]{maxWidth, maxHeight});
            return Bitmap.createScaledBitmap(bitmap, arrTarget[0], arrTarget[1], true);
        } else
        {
            return bitmap;
        }
    }

    /**
     * 把bitmap保存到File
     *
     * @param bitmap
     * @return
     */
    protected File bitmapToFile(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return null;
        }

        File file = newFileUnderDir(getCompressedFileDir(), ".jpg");
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            addCompressedFile(file);
            return file;
        } catch (FileNotFoundException e)
        {
            mException = e;
            return null;
        } finally
        {
            closeQuietly(fos);
        }
    }

    /**
     * 根据源宽高和限制宽高，计算出适应限制的宽高
     *
     * @param source 源宽高
     * @param limit  限制宽高
     * @return
     */
    private static int[] calculateFitSize(int[] source, int[] limit)
    {
        float sourceRatio = (float) source[0] / (float) source[1];
        float limitRatio = (float) limit[0] / (float) limit[1];
        float differRatio = sourceRatio - limitRatio;

        int[] result = new int[2];
        if (differRatio >= 0)
        {
            result[0] = limit[0];
            result[1] = (int) (result[0] / sourceRatio);
        } else
        {
            result[1] = limit[1];
            result[0] = (int) (result[1] * sourceRatio);
        }
        return result;
    }

    private static File newFileUnderDir(File dir, String ext)
    {
        if (dir == null)
        {
            return null;
        }
        if (ext == null)
        {
            ext = "";
        }

        long current = System.currentTimeMillis();
        File file = new File(dir, String.valueOf(current + ext));
        while (file.exists())
        {
            current++;
            file = new File(dir, String.valueOf(current + ext));
        }
        return file;
    }

    private static void closeQuietly(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (Throwable ignored)
            {
            }
        }
    }

    private static boolean deleteFileOrDir(File file)
    {
        if (file == null || !file.exists())
        {
            return true;
        }
        if (file.isFile())
        {
            return file.delete();
        }
        File[] files = file.listFiles();
        if (files != null)
        {
            for (File item : files)
            {
                deleteFileOrDir(item);
            }
        }
        return file.delete();
    }
}
