package com.pure.camera.manager;

import com.pure.camera.manager.camera.Camera2OneCamera;

public interface CameraOpenCallBack {

    void onCameraOpened(Camera2OneCamera oneCamera);
    void onCameraDisconnect();
    void onCameraOpenFalied(int error);

}
