package com.pure.camera.module;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.R;
import com.pure.camera.data.PhotoFile;
import com.pure.camera.ui.UIStateListener;
import com.pure.camera.util.CameraSettings;
import com.pure.camera.util.FileOperatorHelper;
import com.pure.camera.util.LogPrinter;
import com.pure.camera.view.CameraView;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PhotoModule extends BaseCameraModule {

    private static final String TAG = "PhotoModule";

    private Size previewSize = new Size(1280, 960);
    private Size pictureSize = new Size(1280, 960);
    private CameraCaptureSession previewSession;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest.Builder captureBuilder;
    private CaptureRequest previewRequest;
    private ImageReader imageReader;
    private CameraCaptureSession.CaptureCallback captureCallback;
    protected CameraCaptureSession.StateCallback captureStateCallback;

    private Size screenSize;

    public PhotoModule(CameraActivity ctx, CameraView view) {
        super(ctx, view);
        initCameraUI();
    }

    @Override
    protected void initModule() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIDs = cameraManager.getCameraIdList();
                if (cameraIDs.length <= 0)
                    throw new RuntimeException("Can not find camera on this device!");
                for (String s : cameraIDs) {
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(s);
                    if (parameters.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_BACK) {
                        BACK_CAMERA = s;
                        currentCamera = s;
                        CameraSettings.initializeBackCameraSettings(parameters);
                    } else if (parameters.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        FRONT_CAMERA = s;
                        CameraSettings.initializeFrontCameraSettings(parameters);
                    }
                }

                if (TextUtils.isEmpty(currentCamera)) {
                    currentCamera = cameraIDs[0];
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(currentCamera);
                    CameraSettings.initializeBackCameraSettings(parameters);
                }


                DisplayMetrics dm = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                screenSize = new Size(dm.widthPixels, dm.heightPixels);
                LogPrinter.i(TAG, "Preview size : " +
                        previewSize.getWidth() + "x" + previewSize.getHeight());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        resetCameraPreviewParemeters(true);
        HandlerThread handlerThread = new HandlerThread("CameraThread");
        handlerThread.start();
        cameraHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    private void initCameraUI() {
        cameraView.setStateListener(new UIStateListener() {
            @Override
            public void onUIPrepare(SurfaceTexture texture) {
                uiPrepared = true;
                previewSurfaceTexture = texture;
                previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                previewSurface = new Surface(previewSurfaceTexture);
                startPreview();
            }
        });

        cameraView.addCameraGLView();
        cameraView.setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    @Override
    protected void closeCamera() {
        if (null != captureSession) {
            captureSession.close();
            captureSession = null;
        }

        if (null != previewSession) {
            previewSession.close();
            previewSession = null;
        }

        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }

        cameraPrepared = false;
    }

    @Override
    protected void startPreview() {
        if (!cameraPrepared || !uiPrepared || null == cameraDevice)
            return;

        try {
            if (null == previewBuilder) {
                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewBuilder.addTarget(previewSurface);
            }
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        previewSession = session;
                        previewRequest = previewBuilder.build();
                        previewSession.setRepeatingRequest(previewRequest, null, cameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void capture() {
        try {
            if (null == captureBuilder) {
                captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            }

            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = cameraView.getContext().getResources().getConfiguration().orientation;
            Log.i("ttt", rotation + " " + CameraSettings.ORIENTATIONS.get(rotation));
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, CameraSettings.ORIENTATIONS.get(rotation));
            if (null == captureStateCallback) {
                captureStateCallback = new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        try {
                            captureSession = session;
                            if (null == captureCallback) {
                                captureCallback = new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                                        LogPrinter.i(TAG, "onCaptureStarted");
                                    }

                                    @Override
                                    public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                        LogPrinter.i(TAG, "onCaptureProgressed");
                                    }

                                    @Override
                                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                        LogPrinter.i(TAG, "onCaptureCompleted");
                                        startPreview();
                                    }

                                    @Override
                                    public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                                        LogPrinter.i(TAG, "onCaptureFailed");
                                        startPreview();
                                    }
                                };
                            }
                            captureSession.capture(captureBuilder.build(), captureCallback, cameraHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {

                    }

                };
            }
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), captureStateCallback, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchCamera() {
        if (TextUtils.isEmpty(BACK_CAMERA) || TextUtils.isEmpty(FRONT_CAMERA))
            return;

        boolean isBack = currentCamera.equals(BACK_CAMERA);
        currentCamera = isBack ? FRONT_CAMERA : BACK_CAMERA;
        isBack = !isBack;
        closeCamera();
        resetCameraPreviewParemeters(isBack);
        openCamera();
    }

    private void resetCameraPreviewParemeters(boolean back) {
        previewSize = CameraSettings.choosePreviewSize(screenSize, back);
        pictureSize = CameraSettings.choosePictureSize(screenSize.getWidth(), screenSize.getHeight(), pictureSize, back);
        createImageReader(pictureSize);
        if (null != previewSurfaceTexture)
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
    }

    /**
     * 创建拍照时接收数据的ImageReader.
     * @param size 拍照尺寸
     */
    private void createImageReader(Size size) {
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }

        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                LogPrinter.i(TAG, "onImageAvailable : " + System.currentTimeMillis() +
                        "  " + Thread.currentThread().getName());
                try (Image image = reader.acquireLatestImage()) {
                    Image.Plane[] planes = image.getPlanes();
                    if (planes.length > 0) {
                        ByteBuffer buffer = planes[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        final PhotoFile p = new PhotoFile(data, imageReader.getWidth(), imageReader.getHeight(), 0);
                        if (FileOperatorHelper.getInstance().saveFile(p)) {
                            LogPrinter.i(TAG, "Save photo success!");
                            cameraView.getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cameraView.toast(p.getFilePath());
                                }
                            });
                        } else {
                            LogPrinter.e(TAG, "Save photo failed!");
                        }
                    }
                }
                LogPrinter.i(TAG, "onImageAvailable" + System.currentTimeMillis());
            }
        }, cameraHandler);
    }

    @Override
    public boolean isProgressing() {
        return false;
    }
}
