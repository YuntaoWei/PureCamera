package com.pure.camera.module;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.common.ImageUtil;
import com.pure.camera.common.FileOperatorHelper;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.data.PhotoFile;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.filter.engine.NoFilter;
import com.pure.camera.opengl.UIStateListener;
import com.pure.camera.view.CameraPhotoView;
import com.pure.camera.view.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PhotoModule extends BaseCameraModule implements OnFilterChangeListener {

    private static final String TAG = "PhotoModule";

    private Size previewSize = new Size(1280, 960);
    private Size pictureSize = new Size(1280, 960);
    private CameraCaptureSession previewSession;
    private CaptureRequest.Builder previewBuilder;
    private ImageReader captureImageReader;
    private CameraCaptureSession.CaptureCallback captureCallback;
    private int defaultFormat = ImageFormat.YUV_420_888;
    //private int defaultFormat = ImageFormat.JPEG;

    private int cameraOrientation;
    private Size screenSize;
    private BaseFilter currentFilter = CameraFilterManager.getInstance()
            .getFilter(CameraFilterManager.FILTER_NAME_ORIGINAL);

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
                    LogPrinter.i("mm", "camera id : " + s);
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
                        previewSize.getWidth() + "x" + previewSize.getHeight() + "  screen size : " + screenSize);
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
        cameraView.setCameraOperation(this);
        ((CameraPhotoView) cameraView).setFilterChangeListener(this);
    }

    @Override
    protected void closeCamera() {
        if (null != previewSession) {
            previewSession.close();
            previewSession = null;
        }

        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (null != captureImageReader) {
            captureImageReader.close();
            captureImageReader = null;
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
            }

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, captureImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        previewBuilder.addTarget(previewSurface);
                        previewSession = session;
                        previewSession.setRepeatingRequest(previewBuilder.build(), null, cameraHandler);
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
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(previewSurface);
            captureBuilder.addTarget(captureImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            cameraOrientation = cameraView.getContext().getResources().getConfiguration().orientation;
            cameraOrientation = CameraSettings.getModifyOrientation(cameraManager.getCameraCharacteristics(currentCamera), cameraOrientation);
            if(defaultFormat == ImageFormat.JPEG) {
                byte a = 100;
                captureBuilder.set(CaptureRequest.JPEG_QUALITY, new Byte(a));
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, cameraOrientation);
                LogPrinter.i(TAG, "orientation set : device orientation = " + cameraOrientation +
                        " jpeg orientation = " + CameraSettings.ORIENTATIONS.get(cameraOrientation));
            }

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
                        session.close();
                        startPreview();
                    }

                    @Override
                    public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                        LogPrinter.i(TAG, "onCaptureFailed");
                        session.close();
                        startPreview();
                    }
                };
            }
            previewSession.capture(captureBuilder.build(), captureCallback, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchCamera() {
        if (TextUtils.isEmpty(BACK_CAMERA) || TextUtils.isEmpty(FRONT_CAMERA))
            return;

        boolean isBack = isBack();
        currentCamera = isBack ? FRONT_CAMERA : BACK_CAMERA;
        isBack = !isBack;
        closeCamera();
        resetCameraPreviewParemeters(isBack);
        openCamera();
    }

    private boolean isBack() {
        return currentCamera.equals(BACK_CAMERA);
    }

    /**
     * 重置Camera Parameter，在这里可以重新设置Camera的各项参数.
     * 设置改变，前后摄切换等，都会调用此方法进行Camera的重新设置.
     *
     * @param back 是否是后摄.
     */
    private void resetCameraPreviewParemeters(boolean back) {
        CameraSettings.printSupportSize();
        previewSize = CameraSettings.choosePreviewSize(screenSize, back);
        pictureSize = CameraSettings.choosePictureSize(screenSize.getWidth(),
                screenSize.getHeight(), pictureSize, back, defaultFormat);
        LogPrinter.i(TAG, "resetCameraPreviewParemeters : preview size = " + previewSize + " , picture size : " + pictureSize);
        createImageReader(pictureSize);
        if (null != previewSurfaceTexture) {
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            cameraView.updatePreviewSize(previewSize.getWidth(), previewSize.getHeight());
        }
    }

    private void reCreateImageReader() {
        if (null != captureImageReader) {
            captureImageReader.close();
            captureImageReader = null;
        }

        createImageReader(pictureSize);
    }

    /**
     * 创建拍照时接收数据的ImageReader.
     *
     * @param size 拍照尺寸
     */
    private void createImageReader(Size size) {
        captureImageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), defaultFormat, 1);
        captureImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                long t = System.currentTimeMillis();
                LogPrinter.i(TAG, "onImageAvailable : " + t +
                        "  " + Thread.currentThread().getName() + "  " + reader.getWidth() + "x" + reader.getHeight());
                try (Image image = reader.acquireLatestImage()) {
                    if(defaultFormat == ImageFormat.JPEG) {
                        Image.Plane p[] = image.getPlanes();
                        byte[] data = new byte[p[0].getBuffer().remaining()];
                        p[0].getBuffer().get(data);
                        final PhotoFile photo = new PhotoFile(data, image.getWidth(), image.getHeight(), 0);
                        if (FileOperatorHelper.getInstance().saveFile(photo)) {
                            LogPrinter.i(TAG, "Save photo success!");
                            cameraView.toast(photo.getFilePath());
                        } else {
                            cameraView.toast("Save file failed!");
                        }
                    } else {
                        long t1 = System.currentTimeMillis();
                        //byte[] data = ImageUtil.getYUVDataFromImageAsType(image, ImageUtil.YUV420P);
                        //byte[] data = ImageUtil.getCamera2YUVData(image, ImageUtil.NV12);
                        byte[] data = ImageUtil.getYUV420Data(image, ImageUtil.YUV420P);
                        Log.i(TAG, "start get data : " + (System.currentTimeMillis() - t1));
                        final PhotoFile p = new PhotoFile(data, image.getWidth(), image.getHeight(), cameraOrientation);
                        if (FileOperatorHelper.getInstance().saveFile(p, currentFilter)) {
                            LogPrinter.i(TAG, "Save photo success!");
                            cameraView.toast(p.getFilePath());
                        } else {
                            cameraView.toast("Save file failed!");
                        }
                    }
                }
                LogPrinter.i(TAG, "onImageAvailable : save file take time : " + (System.currentTimeMillis() - t));
            }
        }, cameraHandler);
    }

    @Override
    public boolean isProgressing() {
        return false;
    }

    @Override
    public void onFilterChange(BaseFilter filter) {
        currentFilter = filter;
    }

    private boolean requestJpegData() {
        return (currentFilter == null || currentFilter instanceof NoFilter);
    }
}
