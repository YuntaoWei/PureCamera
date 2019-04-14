package com.pure.camera.module;

import android.graphics.SurfaceTexture;

public interface CameraModule {

    //拍照或录像
    void capture();

    void switchCamera();

    //是否处于处理过程中
    boolean isProgressing();

    void resume();

    void pause();
}
