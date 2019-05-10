package com.pure.camera.module;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import com.pure.camera.common.LogPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraSettings {

    public final static String TAG = "CameraSettings";
    private static List<CaptureResult.Key<?>> supBackKeys, supFrontKeys;
    private static StreamConfigurationMap backMapSize, frontMapSize;

    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * 初始化保存后摄参数.
     *
     * @param parameter CameraManager获取到的Back Camera参数.
     */
    public static void initializeBackCameraSettings(CameraCharacteristics parameter) {
        LogPrinter.i("format", "initializeBackCameraSettings");
        supBackKeys = null;
        supBackKeys = parameter.getAvailableCaptureResultKeys();
        backMapSize = null;
        backMapSize = parameter.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    }

    /**
     * 初始化保存前摄参数.
     *
     * @param parameter CameraManager获取到的Front Camera参数.
     */
    public static void initializeFrontCameraSettings(CameraCharacteristics parameter) {
        LogPrinter.i(TAG, "initializeFrontCameraSettings");
        supFrontKeys = null;
        supFrontKeys = parameter.getAvailableCaptureResultKeys();
        frontMapSize = null;
        frontMapSize = parameter.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    }

    /**
     * 获取合适的摄像尺寸.
     *
     * @param width
     * @param height
     * @param setSize
     * @param back
     * @return
     */
    public static Size chooseVideoSize(int width, int height, Size setSize, boolean back) {
        Size[] sizes = back ? backMapSize.getOutputSizes(MediaRecorder.class) :
                frontMapSize.getOutputSizes(MediaRecorder.class);
        List<Size> bigEnough = new ArrayList<Size>();
        int w = setSize.getWidth();
        int h = setSize.getHeight();
        for (Size option : sizes) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return sizes[0];
        }
    }

    /**
     * 获取合适的照片尺寸.
     *
     * @param width
     * @param height
     * @param setSize
     * @param back
     * @return
     */
    public static Size choosePictureSize(int width, int height, Size setSize, boolean back, int format) {
        Size[] sizes = back ? backMapSize.getOutputSizes(format) :
                frontMapSize.getOutputSizes(format);
        if(null == sizes) {
            throw new IllegalArgumentException("Can not support this data format : " + Integer.toHexString(format));
        }
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size size : sizes) {
            Log.i(TAG, "------" + size);
        }
        int w = setSize.getWidth();
        int h = setSize.getHeight();
        float screenRatio = (float) h / (float) w;
        for (Size option : sizes) {
            float ratio = (float) option.getWidth() / (float) option.getHeight();
            if (screenRatio == ratio) {
                LogPrinter.i(TAG, "Got the best size : " + option);
                return option;
            }

            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            Size s = Collections.min(bigEnough, new CompareSizesByArea());
            LogPrinter.i(TAG, "Select a generic size : " + s);
            return s;
        } else {
            Log.e(TAG, "Couldn't find any suitable picture size : " + sizes[0]);
            return sizes[0];
        }
    }

    public static Size getBestPreviewSize(Size screenSize, boolean isBack) {
        int diffs = Integer.MAX_VALUE;
        Size[] availablePreviewSizes = isBack ? backMapSize.getOutputSizes(SurfaceTexture.class)
                : frontMapSize.getOutputSizes(SurfaceTexture.class);

        int mCameraPreviewWidth, mCameraPreviewHeight;
        int bestPreviewWidth = -1, bestPreviewHeight = -1;
        for (Size previewSize : availablePreviewSizes) {
            LogPrinter.v(TAG, " PreviewSizes = " + previewSize);
            mCameraPreviewWidth = previewSize.getWidth();
            mCameraPreviewHeight = previewSize.getHeight();
            int newDiffs = Math.abs(mCameraPreviewWidth - screenSize.getWidth()) + Math.abs(mCameraPreviewHeight - screenSize.getHeight());
            LogPrinter.v(TAG, "newDiffs = " + newDiffs);

            if (newDiffs == 0) {
                bestPreviewWidth = mCameraPreviewWidth;
                bestPreviewHeight = mCameraPreviewHeight;
                break;
            }
            if (diffs > newDiffs) {
                bestPreviewWidth = mCameraPreviewWidth;
                bestPreviewHeight = mCameraPreviewHeight;
                diffs = newDiffs;
            }
        }

        if(bestPreviewHeight == -1 || bestPreviewWidth == -1) {
            LogPrinter.e(TAG, "Couldn't find any suitable picture size : " + availablePreviewSizes[0]);
            return availablePreviewSizes[0];
        }
        return new Size(bestPreviewWidth, bestPreviewHeight);
    }

    /**
     * 根据预览的View的尺寸,获取合适的预览尺寸.
     *
     * @param setSize
     * @param back
     * @return
     */
    public static Size choosePreviewSize(Size setSize, boolean back) {
        Log.i(TAG, "choosePreviewSize : " + back);
        Size[] sizes = back ? backMapSize.getOutputSizes(SurfaceTexture.class) :
                frontMapSize.getOutputSizes(SurfaceTexture.class);
        List<Size> bigEnough = new ArrayList();
        int w = setSize.getWidth();
        int h = setSize.getHeight();
        float screenRatio = (float) h / (float) w;

        for (Size option : sizes) {
            float ratio = (float) option.getWidth() / (float) option.getHeight();
            if (screenRatio == ratio)
                return option;
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= w && option.getHeight() >= h) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return sizes[0];
        }
    }

    /**
     * 判断是否支持相应的设置.
     *
     * @param key  设置对应的key.
     * @param back 是否是后摄.
     * @return
     */
    public static boolean isSupport(CaptureResult.Key<?> key, boolean back) {
        return back ? supBackKeys.contains(key) : supFrontKeys.contains(key);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * 打印所有的支持的视频尺寸，照片尺寸，预览尺寸.
     */
    public static void printSupportSize() {
        LogPrinter.i(TAG, "---------------printSupportSize start------------------");
        Size[] videoSizes = backMapSize.getOutputSizes(MediaRecorder.class);
        Size[] pictureSizes = backMapSize.getOutputSizes(ImageFormat.YUV_420_888);
        Size[] previewSizes = backMapSize.getOutputSizes(SurfaceTexture.class);
        LogPrinter.i(TAG, "-------support video size---------");
        for (Size size : videoSizes) {
            LogPrinter.i(TAG, "videoSizes---" + size);
        }
        LogPrinter.i(TAG, "----------------------------------");
        LogPrinter.i(TAG, "-------support picture size---------");
        for (Size size : pictureSizes) {
            LogPrinter.i(TAG + "_YUV", "pictureSizes---" + size);
        }
        LogPrinter.i(TAG, "----------------------------------");
        LogPrinter.i(TAG, "-------support preview size---------");
        for (Size size : previewSizes) {
            LogPrinter.i(TAG, "previewSizes---" + size);
        }

        LogPrinter.i(TAG, "---------------printSupportSize end------------------");
    }

    /**
     * 获取与预览一直的照片/视频方向，避免拍摄结果与预览不一致的问题
     *
     * @param c                 当前打开的摄像头的参数
     * @param deviceOrientation 当前设备的方向
     * @return
     */
    public static int getModifyOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

}
