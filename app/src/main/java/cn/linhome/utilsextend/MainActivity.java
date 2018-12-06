package cn.linhome.utilsextend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import java.io.File;

import cn.linhome.lib.utils.extend.FImageCompressor;
import cn.linhome.lib.utils.extend.FImageGetter;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private ImageView iv_image;

    private FImageGetter mImageGetter;
    private FImageCompressor mImageCompressor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_image = findViewById(R.id.iv_image);

        testImageCompressor();
        testImageGetter();

        iv_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mImageGetter.getImageFromAlbum();
            }
        });
    }

    private void testImageGetter()
    {
        mImageGetter = new FImageGetter(this);
        mImageGetter.setCallback(new FImageGetter.Callback()
        {
            @Override
            public void onResultFromAlbum(File file)
            {
                Log.i(TAG, "onResultFromAlbum:" + file.getAbsolutePath());

                Bitmap bitmap = mImageCompressor.compressFileToBitmap(file.getAbsolutePath());
                iv_image.setImageBitmap(bitmap);
            }

            @Override
            public void onResultFromCamera(File file)
            {
                Log.i(TAG, "onResultFromCamera:" + file.getAbsolutePath());
            }

            @Override
            public void onError(String desc)
            {
                Log.e(TAG, desc);
            }
        });
    }

    private void testImageCompressor()
    {
        mImageCompressor = new FImageCompressor(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mImageGetter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mImageCompressor != null)
        {
            mImageCompressor.deleteAllCompressedFile();
        }
    }
}
