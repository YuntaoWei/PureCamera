package com.pure.camera.module;

import android.hardware.camera2.CameraAccessException;

public abstract class AbstractCameraModule implements CameraModule {

    protected abstract void openCamera() throws CameraAccessException;

    protected abstract void closeCamera();

    protected abstract void switchCamera();

    //开始预览
    protected abstract void startPreview();
    //关闭预览
    protected abstract void stopPreivew();

}
