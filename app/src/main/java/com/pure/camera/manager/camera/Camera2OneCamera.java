package com.pure.camera.manager.camera;

import android.hardware.camera2.CameraDevice;

public class Camera2OneCamera {

    private CameraDevice cameraDevice;
    private CameraParameter cameraParameter;

    public Camera2OneCamera(CameraDevice camera) {
        cameraDevice = camera;
    }

    public Camera2OneCamera(CameraDevice camera, CameraParameter cp) {
        cameraDevice = camera;
        cameraParameter = cp;
    }

    public void setCameraParameter(CameraParameter cp) {
        cameraParameter = cp;
    }

    public void startPreview() {

    }

    public void stopPreview() {

    }

    public void reStartPreview() {

    }

    public void close() {
        cameraDevice.close();
        cameraDevice = null;
    }

}
