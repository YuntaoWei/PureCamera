package com.pure.camera.base;

import android.os.Bundle;

import com.pure.camera.module.CameraModule;
import com.pure.camera.view.BaseView;

public abstract class BaseCameraActivity<V extends BaseView, M extends CameraModule> extends BasePermissionActivity {

    protected V mView;
    protected M cameraModule;

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

    protected abstract Class<V> getViewClass();

    public abstract int getLayoutID();

}
