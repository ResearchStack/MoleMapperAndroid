/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.researchstack.molemapper.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.molemapper.PhotoCaptureActivity;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.ui.StatusBarUtils;
import org.researchstack.molemapper.ui.view.AutoFitTextureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@TargetApi(21)
public class LollipopCameraFragment extends CameraFragment implements FragmentCompat.OnRequestPermissionsResultCallback
{
    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS              = new SparseIntArray();
    private static final int            REQUEST_CAMERA_PERMISSION = 1;
    private static final String         FRAGMENT_DIALOG           = "dialog";

    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Integer mFlashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;

    /**
     * This variable is a temp solution to the following: https://code.google.com/p/android/issues/detail?id=190966&q=label%3AReportedBy-Developer&
     * colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
     */
    @Deprecated
    private boolean showsPermissionDeniedDialog;

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
     * TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener()
    {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height)
        {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height)
        {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture)
        {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture)
        {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback()
    {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice)
        {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error)
        {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if(null != activity)
            {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(ImageReader reader)
        {
            Activity activity = getActivity();

            if(null == mTextureView || null == mPreviewSize || null == activity)
            {
                return;
            }

            try
            {
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);

                mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(),
                        getJpegOrientation(characteristics, rotation)));
            }
            catch(CameraAccessException e)
            {
                LogExt.e(getClass(), e);
            }
        }

        private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation)
        {
            if(deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            {
                return 0;
            }
            int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

            // Round device orientation to a multiple of 90
            deviceOrientation = (deviceOrientation + 45) / 90 * 90;

            // Reverse device orientation for front-facing cameras
            boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) ==
                    CameraCharacteristics.LENS_FACING_FRONT;
            if(facingFront)
            {
                deviceOrientation = - deviceOrientation;
            }

            // Calculate desired JPEG orientation relative to camera orientation to make
            // the image upright relative to the device orientation
            int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;
            return jpegOrientation;
        }
    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback()
    {

        private void process(CaptureResult result)
        {
            switch(mState)
            {
                case STATE_PREVIEW:
                {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK:
                {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if(afState == null || afState == CaptureResult.CONTROL_AF_STATE_INACTIVE)
                    {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    else if(CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState)
                    {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if(aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED)
                        {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        }
                        else
                        {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE:
                {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if(aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED)
                    {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE:
                {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if(aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE)
                    {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult)
        {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result)
        {
            process(result);
        }

    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text)
    {
        final Activity activity = getActivity();
        if(activity != null)
        {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that is
     * at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio)
    {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for(Size option : choices)
        {
            if(option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w)
            {
                if(option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight)
                {
                    bigEnough.add(option);
                }
                else
                {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if(bigEnough.size() > 0)
        {
            return Collections.min(bigEnough, new CompareSizesByArea());
        }
        else if(notBigEnough.size() > 0)
        {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        }
        else
        {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private String imagePath;
    private int    type;

    private PhotoCaptureCallbacks mCallbacks;
    private View                  undo;
    private ImageView             preview;
    private ImageView             picture;
    private View                  progress;

    /**
     * Default empty constructor.
     */
    public LollipopCameraFragment()
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

    private void setCallbacks(Context context)
    {
        if(! (context instanceof PhotoCaptureCallbacks))
        {
            throw new RuntimeException("Parent context must implement " +
                    "PhotoCaptureActivity.PhotoCaptureCallbacks");
        }

        mCallbacks = (PhotoCaptureCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // args
        imagePath = getArguments().getString(PhotoCaptureActivity.KEY_PATH);
        type = getArguments().getInt(PhotoCaptureActivity.KEY_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        // Hide grid if we are capturing zone photo
        View grid = view.findViewById(R.id.grid);
        grid.setVisibility(type == PhotoCaptureActivity.ZONE ? View.GONE : View.VISIBLE);

        preview = (ImageView) view.findViewById(R.id.photo_preview);

        progress = view.findViewById(R.id.progress);
        progress.setAlpha(0);
        progress.setVisibility(View.GONE);

        String instruction = getArguments().getString(PhotoCaptureActivity.KEY_INSTRUCTION);
        if(! TextUtils.isEmpty(instruction))
        {
            TextView instructionTV = (TextView) view.findViewById(R.id.photo_instruction);
            instructionTV.setText(instruction);
            instructionTV.setVisibility(View.VISIBLE);
        }

        picture = (ImageView) view.findViewById(R.id.photo_capture);
        picture.setOnClickListener(v -> {
            LogExt.i(getClass(), "photo_capture click");

            if(mState != STATE_PICTURE_TAKEN)
            {
                takePicture();
            }
            else
            {
                mCallbacks.captureCompleted();
            }
        });
        picture.setElevation(getResources().getDisplayMetrics().density * 8);
        picture.setOutlineProvider(new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        View cancel = view.findViewById(R.id.photo_cancel);
        cancel.setOnClickListener(v -> {
            if(isAdded())
            {
                mCallbacks.captureCanceled();
            }
        });
        undo = view.findViewById(R.id.photo_undo);
        undo.setOnClickListener(v -> {
            unlockFocus();
            undo.setVisibility(View.GONE);

            preview.setImageBitmap(null);
            preview.setVisibility(View.GONE);

            picture.setImageResource(R.drawable.button_capture);
        });
        ImageView flashControl = (ImageView) view.findViewById(R.id.photo_flash);
        flashControl.setOnClickListener(v -> {
            switch(mFlashMode)
            {
                // Flash is Off
                case CaptureRequest.CONTROL_AE_MODE_ON:
                    mFlashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
                    flashControl.setImageResource(R.drawable.ic_flash_auto);
                    break;

                // Flash is Auto
                case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                    mFlashMode = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
                    flashControl.setImageResource(R.drawable.ic_flash_on);
                    break;

                // Flash is On
                case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                    mFlashMode = CaptureRequest.CONTROL_AE_MODE_ON;
                    flashControl.setImageResource(R.drawable.ic_flash_off);
                    break;
            }
        });
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            StatusBarUtils.adjustForTransparentStatusBar(grid, flashControl, grid, cancel);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if(mTextureView.isAvailable())
        {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        }
        else
        {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        if(showsPermissionDeniedDialog)
        {
            showPermissionDeniedDialog();
        }
    }

    @Override
    public void onPause()
    {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    private void requestCameraPermission()
    {
        if(FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
        else
        {
            FragmentCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                if(isResumed())
                {
                    showPermissionDeniedDialog();
                }
                else
                {
                    showsPermissionDeniedDialog = true;
                }
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionDeniedDialog()
    {
        ErrorDialog.newInstance(getString(R.string.request_permission))
                .show(getChildFragmentManager(), FRAGMENT_DIALOG);

        showsPermissionDeniedDialog = false;
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height)
    {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try
        {
            for(String cameraId : manager.getCameraIdList())
            {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT)
                {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if(map == null)
                {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(),
                        largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/
                        2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                        mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch(displayRotation)
                {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if(sensorOrientation == 90 || sensorOrientation == 270)
                        {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if(sensorOrientation == 0 || sensorOrientation == 180)
                        {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if(swappedDimensions)
                {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if(maxPreviewWidth > MAX_PREVIEW_WIDTH)
                {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if(maxPreviewHeight > MAX_PREVIEW_HEIGHT)
                {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth,
                        rotatedPreviewHeight,
                        maxPreviewWidth,
                        maxPreviewHeight,
                        largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if(orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                }
                else
                {
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
        }
        catch(NullPointerException e)
        {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    /**
     * Opens the camera specified by {@link LollipopCameraFragment#mCameraId}.
     */
    private void openCamera(int width, int height)
    {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
        {
            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try
        {
            if(! mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS))
            {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera()
    {
        try
        {
            mCameraOpenCloseLock.acquire();
            if(null != mCaptureSession)
            {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if(null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if(null != mImageReader)
            {
                mImageReader.close();
                mImageReader = null;
            }
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        }
        finally
        {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread()
    {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        mBackgroundThread.quitSafely();
        try
        {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
        catch(InterruptedException e)
        {
            LogExt.e(getClass(), e);
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession()
    {
        try
        {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback()
                    {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession)
                        {
                            // The camera is already closed
                            if(null == mCameraDevice)
                            {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try
                            {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback,
                                        mBackgroundHandler);
                            }
                            catch(CameraAccessException e)
                            {
                                LogExt.e(getClass(), e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession)
                        {
                            showToast(getString(R.string.error_camera_configure_failed));
                        }
                    },
                    null);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight)
    {
        Activity activity = getActivity();
        if(null == mTextureView || null == mPreviewSize || null == activity)
        {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if(Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        else if(Surface.ROTATION_180 == rotation)
        {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture()
    {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus()
    {
        try
        {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(),
                    mCaptureCallback,
                    mBackgroundHandler);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when we
     * get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence()
    {
        try
        {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(),
                    mCaptureCallback,
                    mBackgroundHandler);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in {@link
     * #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture()
    {
        try
        {
            final Activity activity = getActivity();
            if(null == activity || null == mCameraDevice)
            {
                return;
            }

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // Orientation is handled within the ImageSaver runnable. Phones, (like the S7) have
            // shown to ignore this attribute so we must handle the rotation ourselves
            //
            // int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            // CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            // captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
            //         getJpegOrientation(characteristics, rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback()
            {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result)
                {
                    // We Update UI when image saving is done
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);

            LogExt.e(getClass(), "show Progress");
            showProgress(true);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
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
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus()
    {
        try
        {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(),
                    mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                    mCaptureCallback,
                    mBackgroundHandler);
        }
        catch(CameraAccessException e)
        {
            LogExt.e(getClass(), e);
            LogExt.e(getClass(), e);
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder)
    {
        if(mFlashSupported)
        {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode);
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private class ImageSaver implements Runnable
    {

        /**
         * The JPEG image
         */
        private Image image;
        private int   rotation;

        public ImageSaver(Image image, int rotation)
        {
            this.image = image;
            this.rotation = rotation;
        }

        @Override
        public void run()
        {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            image.close();
            buffer.clear();

            // Decode byte array width / height for sampleSize
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            // Set inSample size using image bounds
            options.inSampleSize = calculateInSampleSize(options,
                    mPreviewSize.getWidth(),
                    mPreviewSize.getHeight());
            options.inJustDecodeBounds = false;

            // Decode image using above sample size
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            // Rotate the drawable to compensate for camera sensor rotation
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);

            // Compress the image to JPEG
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] compressedByteArray = stream.toByteArray();
            StorageAccess.getInstance()
                    .getFileAccess()
                    .writeData(getActivity(), imagePath, compressedByteArray);

            // Decode bitmap and then display it after its saved.
            Bitmap compressed = BitmapFactory.decodeByteArray(compressedByteArray,
                    0,
                    compressedByteArray.length);

            // This code chunk is meh
            new Handler(Looper.getMainLooper()).post(() -> {
                undo.setVisibility(View.VISIBLE);
                picture.setImageResource(R.drawable.button_confirm_photo);

                preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(compressed);

                showProgress(false);
            });
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size>
    {

        @Override
        public int compare(Size lhs, Size rhs)
        {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment
    {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message)
        {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity).setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        activity.finish();
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity()).setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        FragmentCompat.requestPermissions(parent,
                                new String[] {Manifest.permission.CAMERA},
                                REQUEST_CAMERA_PERMISSION);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        Activity activity = parent.getActivity();
                        if(activity != null)
                        {
                            activity.finish();
                        }
                    })
                    .create();
        }
    }

}