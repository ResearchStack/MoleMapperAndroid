package org.researchstack.molemapper.ui.fragment;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import org.researchstack.molemapper.PhotoCaptureActivity;

public class CameraFragment extends Fragment
{

    public static CameraFragment newInstance(int id, int type, String desc, String path)
    {
        CameraFragment fragment = getCameraFragment();
        Bundle args = new Bundle();
        args.putInt(PhotoCaptureActivity.KEY_ID, id);
        args.putInt(PhotoCaptureActivity.KEY_TYPE, type);
        args.putString(PhotoCaptureActivity.KEY_INSTRUCTION, desc);
        args.putString(PhotoCaptureActivity.KEY_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    public CameraFragment()
    {
        super();
    }

    public static CameraFragment getCameraFragment()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            return new LollipopCameraFragment();
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            return new JellyBeanCameraFragment();
        }
        else
        {
            throw new RuntimeException("API v." + Build.VERSION.SDK_INT + " not supported");
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
