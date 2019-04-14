package com.pure.camera.module;

import android.hardware.camera2.CameraAccessException;

public abstract class AbstractCameraModule implements CameraModule {

    protected abstract void openCamera() throws CameraAccessException;

    protected abstract void closeCamera();

    //开始预览
    protected abstract void startPreview();

}
