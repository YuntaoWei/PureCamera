package com.pure.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pure.camera.base.BaseCameraActivity;
import com.pure.camera.module.PhotoModule;
import com.pure.camera.view.CameraPhotoView;

public class CameraActivity extends BaseCameraActivity<CameraPhotoView, PhotoModule> {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraModule = new PhotoModule(this, mView);
    }

    private void init() {
        if (gotAllPermission && null != cameraModule)
            cameraModule.resume();
    }

    private void release() {
        if (gotAllPermission && null != cameraModule)
            cameraModule.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        release();
    }

    @Override
    public void onGetPermissionSuccess() {
        gotAllPermission = true;
        init();
    }

    @Override
    public void onGetPermissionFailure() {
        gotAllPermission = false;
    }

    @Override
    protected Class<CameraPhotoView> getViewClass() {
        return CameraPhotoView.class;
    }

    @Override
    public int getLayoutID() {
        return R.layout.main;
    }
}
