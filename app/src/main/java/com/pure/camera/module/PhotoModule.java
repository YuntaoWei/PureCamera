package com.pure.camera.module;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;

import com.pure.camera.CameraActivity;
import com.pure.camera.R;
import com.pure.camera.data.PhotoFile;
import com.pure.camera.ui.UIStateListener;
import com.pure.camera.util.CameraSettings;
import com.pure.camera.util.FileOperatorHelper;
import com.pure.camera.util.LogPrinter;
import com.pure.camera.view.CameraPhotoView;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PhotoModule extends AbstractCameraModule implements View.OnClickListener {

    private static final String TAG = "PhotoModule";

    private CameraActivity mContext;
    private CameraManager cameraManager;
    private CameraPhotoView cameraView;
    private String currentCamera;
    private Size previewSize = new Size(1280, 960);
    private Handler cameraHandler;
    private CameraDevice cameraDevice;
    private Surface previewSurface;
    private CameraCaptureSession previewSession;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest.Builder captureBuilder;
    private CaptureRequest previewRequest;
    private boolean uiPrepared, cameraPrepared;
    private ImageReader imageReader;
    private CameraCaptureSession.StateCallback captureStateCallback;
    private CameraCaptureSession.CaptureCallback captureCallback;

    public PhotoModule(CameraActivity ctx, CameraPhotoView view) {
        mContext = ctx;
        cameraView = view;
        initCameraUI(view);
    }

    private void initModule() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIDs = cameraManager.getCameraIdList();
                if (cameraIDs.length <= 0)
                    throw new RuntimeException("Can not find camera on this device!");
                for (String s : cameraIDs) {
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(s);
                    if (parameters.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_BACK) {
                        currentCamera = s;
                        CameraSettings.initialize(parameters);
                        break;
                    }
                }

                if (TextUtils.isEmpty(currentCamera)) {
                    currentCamera = cameraIDs[0];
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(currentCamera);
                    CameraSettings.initialize(parameters);
                }


                DisplayMetrics dm = new DisplayMetrics();
                mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
                previewSize = CameraSettings.choosePreviewSize(new Size(dm.widthPixels, dm.heightPixels));
                LogPrinter.i(TAG, "Preview size : " +
                        previewSize.getWidth() + "x" + previewSize.getHeight());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        HandlerThread handlerThread = new HandlerThread("CameraThread");
        handlerThread.start();
        cameraHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(),
                ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                LogPrinter.i(TAG, "onImageAvailable : " + System.currentTimeMillis() +
                        "  " + Thread.currentThread().getName());
                try (Image image = reader.acquireNextImage()) {
                    Image.Plane[] planes = image.getPlanes();
                    if (planes.length > 0) {
                        ByteBuffer buffer = planes[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        final PhotoFile p = new PhotoFile(data, imageReader.getWidth(), imageReader.getHeight(), 0);
                        if(FileOperatorHelper.getInstance().saveFile(p)) {
                            cameraView.getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cameraView.toast(p.getFilePath());
                                }
                            });
                        }
                    }
                }
                LogPrinter.i(TAG, "onImageAvailable" + System.currentTimeMillis());
            }
        }, cameraHandler);
    }

    private void initCameraUI(CameraPhotoView main) {
        cameraView.setStateListener(new UIStateListener() {
            @Override
            public void onUIPrepare(SurfaceTexture texture) {
                uiPrepared = true;
                texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                previewSurface = new Surface(texture);
                startPreview();
            }
        });

        cameraView.addCameraGLView();
        cameraView.setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    @Override
    public void resume() {
        initModule();
        try {
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        cameraPrepared = false;
        closeCamera();
        cameraHandler.getLooper().quitSafely();
    }

    @Override
    protected void openCamera() throws CameraAccessException {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        cameraManager.openCamera(currentCamera, new CameraDevice.StateCallback() {
            @Override
            public void onOpened(final CameraDevice camera) {
                cameraPrepared = true;
                cameraDevice = camera;
                startPreview();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                camera.close();
                cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                camera.close();
                cameraDevice = null;
            }
        }, cameraHandler);
    }

    @Override
    protected void closeCamera() {
        if (null != captureSession) {
            captureSession.close();
        }

        if (null != previewSession) {
            captureSession.close();
        }

        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    @Override
    protected void startPreview() {
        if (!cameraPrepared || !uiPrepared || null == cameraDevice)
            return;

        try {
            if(null == previewBuilder) {
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

    }

    @Override
    public boolean isProgressing() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.shutter:
                //拍照
                capture();
                break;

            case R.id.switcher:
                //前后摄切换
                switchCamera();
                break;

            case R.id.recent_thumbnail:
                //打开最近拍摄的图片

                break;
        }
    }
}
