package com.pure.camera.manager.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Size;

public class CameraParameter {

    String id;
    CameraCharacteristics parm;
    StreamConfigurationMap supportSize;

    public CameraParameter(String id, CameraCharacteristics parm) {
        this.id = id;
        this.parm = parm;
        supportSize = parm.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    }

    public boolean isBackCamera() {
        return parm.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK;
    }

    public boolean isFrontCamera() {
        return parm.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
    }

    /**
     * Get all supported preview size.
     * @return
     */
    public Size[] getSupportedPreviewSize() {
        Size[] size = supportSize.getOutputSizes(SurfaceTexture.class);
        return size;
    }

    /**
     * Get all supported photo size by output format.
     * @param format photo format, such as JPEG, NV21, 420SP_888 etc, see {@link ImageFormat}.
     * @return
     */
    public Size[] getSupportPhotoSize(int format) {
        if(!isSupportedFormat(format))
            return null;

        Size[] sizes = supportSize.getOutputSizes(format);
        return sizes;
    }

    /**
     * Get all supported VideoSize.
     */
    public Size[] getSupportVideoSize() {
        Size[] sizes = supportSize.getOutputSizes(MediaRecorder.class);
        return sizes;
    }

    /**
     * Get all supported output format, see {@link ImageFormat}.
     * @return
     */
    public int[] getSupportedOutputFormat() {
        return supportSize.getOutputFormats();
    }

    public boolean isSupportedFormat(int format) {
        int[] formats = supportSize.getOutputFormats();
        for (int f : formats
             ) {
            if(f == format)
                return true;
        }

        return false;
    }

}
