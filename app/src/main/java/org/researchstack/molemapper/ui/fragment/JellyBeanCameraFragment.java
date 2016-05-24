/*
 * Copyright (c) 2014 Rex St. John on behalf of AirPair.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.researchstack.molemapper.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.PhotoCaptureActivity;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.ui.StatusBarUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import rx.Observable;

/**
 * Take a picture directly from inside the app using this fragment.
 * <p>
 * Reference: http://developer.android.com/training/camera/cameradirect.html Reference:
 * http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
 * Reference: http://stackoverflow.com/questions/10913181/camera-preview-is-not-restarting
 * <p>
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
@TargetApi(16)
public class JellyBeanCameraFragment extends CameraFragment
{

    // First Id for back facing camera
    private int mCameraId;

    private Camera.CameraInfo mCameraInfo;

    // Native camera.
    private Camera mCamera;

    // View to display the camera output.
    private CameraPreview mPreview;

    // Reference to the containing view.
    private View mCameraView;
    private View progress;
    private View undo;

    private ImageView captureButton;

    private PhotoCaptureCallbacks mCallbacks;

    private String imagePath;
    private int    type;

    /**
     * Default empty constructor.
     */
    public JellyBeanCameraFragment()
    {
        super();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        setCallbacks(activity);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        setCallbacks(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // args
        imagePath = getArguments().getString(PhotoCaptureActivity.KEY_PATH);
        type = getArguments().getInt(PhotoCaptureActivity.KEY_TYPE);
    }

    private void setCallbacks(Context context)
    {
        if(! (context instanceof PhotoCaptureCallbacks))
        {
            throw new RuntimeException("Parent context must implement " +
                    "PhotoCaptureActivity.PhotoCaptureCallbacks");
        }

        mCallbacks = (PhotoCaptureCallbacks) context;
    }

    /**
     * OnCreateView fragment override
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Create our Preview view and set it as the content of our activity.
        boolean opened = safeCameraOpenInView(view);

        if(opened == false)
        {
            LogExt.d(getClass(), "Error, Camera failed to open");
            Toast.makeText(getActivity(), "Error, Camera failed to open", Toast.LENGTH_SHORT).
                    show();
            return view;
        }

        // Hide grid if we are capturing zone photo
        View grid = view.findViewById(R.id.grid);
        grid.setVisibility(type == PhotoCaptureActivity.ZONE ? View.GONE : View.VISIBLE);

        undo = view.findViewById(R.id.photo_undo);
        undo.setOnClickListener(v -> {
            undo.setVisibility(View.GONE);
            captureButton.setImageResource(R.drawable.button_capture);
            mPreview.startCameraPreview();
        });

        // Progress
        progress = view.findViewById(R.id.progress);
        progress.setAlpha(0);
        progress.setVisibility(View.GONE);

        // Trap the capture button.
        captureButton = (ImageView) view.findViewById(R.id.photo_capture);
        captureButton.setOnClickListener(v -> {
            if(undo.getVisibility() == View.GONE)
            {
                mCamera.takePicture(null, null, mPicture);
                showProgress(true);
            }
            else
            {
                mCallbacks.captureCompleted();
            }
        });

        String instruction = getArguments().getString(PhotoCaptureActivity.KEY_INSTRUCTION);
        if(! TextUtils.isEmpty(instruction))
        {
            TextView instructionTV = (TextView) view.findViewById(R.id.photo_instruction);
            instructionTV.setText(instruction);
            instructionTV.setVisibility(View.VISIBLE);
        }

        ImageView flashControl = (ImageView) view.findViewById(R.id.photo_flash);
        flashControl.setOnClickListener(v -> {
            if(mPreview != null)
            {
                String currentFlashMode = mCamera.getParameters().getFlashMode();
                String nextFlashMode = currentFlashMode;
                List<String> flashModes = mPreview.getSupportedFlashModes();
                int indexOfCurrent = flashModes.indexOf(currentFlashMode);
                if(indexOfCurrent != - 1)
                {
                    int indexOfNext =
                            indexOfCurrent + 1 >= flashModes.size() ? 0 : indexOfCurrent + 1;
                    nextFlashMode = flashModes.get(indexOfNext);
                }
                else
                {
                    if(! flashModes.isEmpty())
                    {
                        nextFlashMode = flashModes.get(0);
                    }
                }

                mPreview.setFlashMode(nextFlashMode);
                flashControl.setImageResource(getFlashModeDrawable(nextFlashMode));
            }
        });

        View cancel = view.findViewById(R.id.photo_cancel);
        cancel.setOnClickListener(v -> {
            if(isAdded())
            {
                mCallbacks.captureCanceled();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            StatusBarUtils.adjustForTransparentStatusBar(grid, flashControl, grid, cancel);
        }

        return view;
    }

    private int getFlashModeDrawable(String flashMode)
    {
        switch(flashMode)
        {
            // Flash is Off
            case Camera.Parameters.FLASH_MODE_OFF:
                return R.drawable.ic_flash_off;

            // Flash is Auto
            default:
            case Camera.Parameters.FLASH_MODE_AUTO:
                return R.drawable.ic_flash_auto;

            // Flash is On
            case Camera.Parameters.FLASH_MODE_ON:
                return R.drawable.ic_flash_on;
        }
    }

    private void showProgress(boolean show)
    {
        progress.animate().setDuration(100).alpha(show ? 1 : 0).withStartAction(() -> {
            if(show)
            {
                progress.setVisibility(View.VISIBLE);
            }
        }).withEndAction(() -> {
            if(! show)
            {
                progress.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Recommended "safe" way to open the camera.
     *
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view)
    {
        boolean qOpened = false;
        releaseCameraAndPreview();

        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for(int i = 0; i < numberOfCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                mCameraId = i;
                mCameraInfo = cameraInfo;
            }
        }

        mCamera = getCameraInstance(mCameraId);
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true)
        {
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, view);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    /**
     * Safe method for getting a camera instance.
     *
     * @return
     */
    public static Camera getCameraInstance(int id)
    {
        Camera c = null;
        try
        {
            c = Camera.open(id); // attempt to get a Camera instance
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        releaseCameraAndPreview();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview()
    {

        if(mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null)
        {
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    /**
     * Surface on which the camera projects it's capture results. This is derived both from Google's
     * docs and the excellent StackOverflow answer provided below.
     * <p>
     * Reference / Credit: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
     */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
    {

        // SurfaceHolder
        private SurfaceHolder mHolder;

        // Our Camera.
        private Camera mCamera;

        // Parent Context.
        private Context mContext;

        // Camera Sizing (For rotation, orientation changes)
        private Camera.Size mPreviewSize;

        // List of supported preview sizes
        private List<Camera.Size> mSupportedPreviewSizes;

        // Flash modes supported by this camera
        private List<String> mSupportedFlashModes;

        // View holding this camera.
        private View mCameraView;

        public CameraPreview(Context context, Camera camera, View cameraView)
        {
            super(context);

            // Capture the context
            mCameraView = cameraView;
            mContext = context;
            setCamera(camera);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        /**
         * Begin the preview of the camera input.
         */
        public void startCameraPreview()
        {
            try
            {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                initCameraDisplayOrientation();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         *
         * @param camera
         */
        private void setCamera(Camera camera)
        {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();

            // Set the camera to Auto Flash mode.
            setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

            requestLayout();
        }

        private void setFlashMode(String flashMode)
        {
            if(mSupportedFlashModes != null && mSupportedFlashModes.contains(flashMode))
            {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(flashMode);
                mCamera.setParameters(parameters);
            }
        }

        public List<String> getSupportedFlashModes()
        {
            return mSupportedFlashModes;
        }

        public void initCameraDisplayOrientation()
        {
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch(rotation)
            {
                case Surface.ROTATION_0:
                    degrees = 90;
                    break;
                case Surface.ROTATION_90:
                    degrees = 0;
                    break;
                case Surface.ROTATION_180:
                    degrees = 270;
                    break;
                case Surface.ROTATION_270:
                    degrees = 180;
                    break;
            }

            //            int result = (mCameraInfo.orientation - degrees + 360) % 360;
            mCamera.setDisplayOrientation(degrees);
        }


        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         *
         * @param holder
         */
        public void surfaceCreated(SurfaceHolder holder)
        {
            try
            {
                mCamera.setPreviewDisplay(holder);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Dispose of the camera preview.
         *
         * @param holder
         */
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            if(mCamera != null)
            {
                mCamera.stopPreview();
            }
        }

        /**
         * React to surface changed events
         *
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
        {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if(mHolder.getSurface() == null)
            {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try
            {
                Camera.Parameters parameters = mCamera.getParameters();

                // Set the auto-focus mode to "continuous"
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                // Preview size must exist.
                if(mPreviewSize != null)
                {
                    Camera.Size previewSize = mPreviewSize;
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Calculate the measurements of the layout
         *
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if(mSupportedPreviewSizes != null)
            {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        /**
         * Update the layout based on rotation and orientation changes.
         *
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            if(changed)
            {
                final int width = right - left;
                final int height = bottom - top;

                int previewWidth = width;
                int previewHeight = height;

                final int scaledChildHeight = previewHeight * width / previewWidth;
                mCameraView.layout(0, height - scaledChildHeight, width, height);
            }
        }

        /**
         * @param sizes
         * @param width
         * @param height
         * @return
         */
        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
        {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            Camera.Size optimalSize = null;

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) height / width;

            // Try to find a size match which suits the whole screen minus the menu on the left.
            for(Camera.Size size : sizes)
            {

                if(size.height != width)
                {
                    continue;
                }
                double ratio = (double) size.width / size.height;
                if(ratio <= targetRatio + ASPECT_TOLERANCE &&
                        ratio >= targetRatio - ASPECT_TOLERANCE)
                {
                    optimalSize = size;
                }
            }

            // If we cannot find the one that matches the aspect ratio, ignore the requirement.
            if(optimalSize == null)
            {
                // TODO in case we don't get a size, do something here
            }

            return optimalSize;
        }
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera)
        {
            Observable.create(subscriber -> {

                // Decode byte array width / height for sampleSize
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                // Set inSample size using image bounds
                options.inSampleSize = calculateInSampleSize(options,
                        mCameraView.getWidth(),
                        mCameraView.getHeight());
                options.inJustDecodeBounds = false;

                LogExt.e(getClass(), "Image Sample Size" + options.inSampleSize);

                // Decode image using above sample size
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                // Rotate bitmap properly
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                int rotation = getRotation(display);
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(rotation);
                Bitmap rotated = Bitmap.createBitmap(bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        rotateMatrix,
                        true);

                // Compress the image to JPG
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] compressedByteArray = stream.toByteArray();
                StorageAccess.getInstance()
                        .getFileAccess()
                        .writeData(getActivity(), imagePath, compressedByteArray);

                subscriber.onNext(null);
                subscriber.onCompleted();
            }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                showProgress(false);
                undo.setVisibility(View.VISIBLE);
                captureButton.setImageResource(R.drawable.button_confirm_photo);
                LogExt.e(getClass(), "Saved: " + imagePath);
            }, error -> {
                LogExt.e(getClass(), error);
                showProgress(false);
            });

        }
    };

    private int getRotation(Display display)
    {
        switch(display.getRotation())
        {
            case Surface.ROTATION_0: // This is display orientation
                return 90;
            default:
            case Surface.ROTATION_90:
                return 0;
            case Surface.ROTATION_180:
                return 270;
            case Surface.ROTATION_270:
                return 180;
        }
    }

}