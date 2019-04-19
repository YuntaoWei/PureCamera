package com.pure.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pure.camera.base.BaseCameraActivity;
import com.pure.camera.module.BaseCameraModule;
import com.pure.camera.module.PhotoModule;
import com.pure.camera.module.VideoModule;
import com.pure.camera.view.CameraView;

public class CameraActivity extends BaseCameraActivity<BaseCameraModule, CameraView> implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView.setOnClickListener(this, R.id.mode_switch);
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
    protected Class<CameraView> getViewClass() {
        return CameraView.class;
    }

    @Override
    public int getLayoutID() {
        return R.layout.main;
    }

    @Override
    public void onClick(View v) {
        cameraModule.pause();
        if(cameraModule instanceof PhotoModule) {
            cameraModule = new VideoModule(this, mView);
        } else {
            cameraModule = new PhotoModule(this, mView);
        }

        cameraModule.resume();
    }
}
