package com.pure.camera.base;

import android.os.Bundle;

import com.pure.camera.module.BaseCameraModule;
import com.pure.camera.view.CameraView;

public abstract class BaseCameraActivity extends BasePermissionActivity {

    protected CameraView mView;
    protected BaseCameraModule cameraModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenterAndView();
        setContentView(mView.getRootView());
    }

    public void initPresenterAndView() {
        try {
            mView = getViewClass().newInstance();
            mView.onCreateView(getLayoutInflater(), getLayoutID(), null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mView.destroy();
    }

    protected abstract Class<? extends CameraView> getViewClass();

    public abstract int getLayoutID();

}
