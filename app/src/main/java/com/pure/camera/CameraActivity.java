package com.pure.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.pure.camera.base.BaseCameraActivity;
import com.pure.camera.module.PhotoModule;
import com.pure.camera.module.VideoModule;
import com.pure.camera.view.CameraPhotoView;
import com.pure.camera.view.CameraVideoView;
import com.pure.camera.view.CameraView;

public class CameraActivity extends BaseCameraActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        //成功获取到了所需的所有权限
        gotAllPermission = true;
        init();
    }

    @Override
    public void onGetPermissionFailure() {
        //没有取得所需的所有的权限
        gotAllPermission = false;
    }

    @Override
    protected Class<? extends CameraView> getViewClass() {
        return CameraPhotoView.class;
    }

    @Override
    public int getLayoutID() {
        return R.layout.main;
    }

    @Override
    public void onClick(View v) {
        cameraModule.pause();
        View root = mView.getRootView();
        if(cameraModule instanceof PhotoModule) {
            mView = new CameraVideoView();
            mView.setRootView(root);
            cameraModule = new VideoModule(this, mView);
        } else {
            mView = new CameraPhotoView();
            mView.setRootView(root);
            cameraModule = new PhotoModule(this, mView);
        }

        cameraModule.resume();
    }
}