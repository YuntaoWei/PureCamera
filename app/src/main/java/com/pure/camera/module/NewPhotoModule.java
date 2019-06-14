package com.pure.camera.module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.manager.Camera2OpenHelper;
import com.pure.camera.manager.CameraOpenCallBack;
import com.pure.camera.manager.camera.Camera2OneCamera;
import com.pure.camera.manager.camera.CameraParameter;
import com.pure.camera.opengl.UIStateListener;
import com.pure.camera.opengl.data.PreviewSize;
import com.pure.camera.view.CameraPhotoView;
import com.pure.camera.view.CameraView;

public class NewPhotoModule extends BaseCameraModule2 implements OnFilterChangeListener {

    private PreviewSize previewSize;
    private BaseFilter currentFilter;
    private String currentCamera;

    public NewPhotoModule(CameraActivity a, CameraView view) {
        super(a, view);
        initCameraUI();
    }

    @Override
    protected void openCamera() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        openHelper.openCamera(currentCamera, new CameraOpenCallBack() {

            @Override
            public void onCameraOpened(Camera2OneCamera oneCamera) {
                mCamera = oneCamera;
                cameraPrepared = true;
                startPreview();
            }

            @Override
            public void onCameraDisconnect() {

            }

            @Override
            public void onCameraOpenFalied(int error) {

            }
        }, cameraHandler);
    }

    private void initCameraUI() {
        cameraView.addCameraGLView();
        cameraView.setCameraOperation(this);
        ((CameraPhotoView) cameraView).setFilterChangeListener(this);
    }

    @Override
    public void onFilterChange(BaseFilter filter) {
        currentFilter = filter;
    }

    private void updateCameraSettings() {

    }

    @Override
    protected void switchCamera() {

    }

    @Override
    public void capture() {

    }

    @Override
    protected void startPreview() {
        updateCameraSettings();
    }

    @Override
    protected void stopPreivew() {

    }

    @Override
    protected void closeCamera() {

    }
}
