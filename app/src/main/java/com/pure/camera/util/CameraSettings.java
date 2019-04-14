package com.pure.camera.util;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraSettings {

    public final static String TAG = "CameraSettings";
    private static List<CaptureResult.Key<?>> supKeys, supFrontKeys;
    private static StreamConfigurationMap mapSize;

    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static void initialize(CameraCharacteristics parameter) {
        supKeys = null;
        supFrontKeys = null;
        supKeys = parameter.getAvailableCaptureResultKeys();
        mapSize = null;
        mapSize = parameter.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    }

    public static Size chooseVideoSize(int width, int height, Size setSize) {
        Size[] sizes = mapSize.getOutputSizes(MediaRecorder.class);
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

    public static Size choosePictureSize(int width, int height, Size setSize) {
        Size[] sizes = mapSize.getOutputSizes(ImageReader.class);
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size size : sizes) {
            Log.i(TAG, "------" + size);
        }
        int w = setSize.getWidth();
        int h = setSize.getHeight();
        for (Size option : sizes) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return sizes[0];
        }
    }

    public static Size choosePreviewSize(Size setSize) {
        Size[] sizes = mapSize.getOutputSizes(SurfaceTexture.class);
        List<Size> bigEnough = new ArrayList<Size>();
        int w = setSize.getWidth();
        int h = setSize.getHeight();
        for (Size option : sizes) {
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

    public static boolean isSupport(CaptureResult.Key<?> key) {
        return supKeys.contains(key);
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

    public static void printSupportSize() {
        Size[] videoSizes = mapSize.getOutputSizes(MediaRecorder.class);
        Size[] pictureSizes = mapSize.getOutputSizes(ImageFormat.JPEG);
        Size[] previewSizes = mapSize.getOutputSizes(SurfaceTexture.class);
        Log.i(TAG, "-------support video size---------");
        for (Size size : videoSizes) {
            Log.i(TAG + "a", "videoSizes---" + size);
        }
        Log.i(TAG, "----------------------------------");
        Log.i(TAG, "-------support picture size---------");
        for (Size size : pictureSizes) {
            Log.i(TAG + "a", "pictureSizes---" + size);
        }
        Log.i(TAG, "----------------------------------");
        Log.i(TAG, "-------support preview size---------");
        for (Size size : previewSizes) {
            Log.i(TAG + "a", "previewSizes---" + size);
        }
    }

}
