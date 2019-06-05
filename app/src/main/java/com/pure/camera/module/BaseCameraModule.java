package com.pure.camera.module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.SparseIntArray;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.bean.MediaFile;
import com.pure.camera.common.ActivityStarter;
import com.pure.camera.view.CameraView;

public class BaseCameraModule extends AbstractCameraModule implements CameraOperation {

    protected static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected CameraManager cameraManager;
    protected CameraDevice cameraDevice;
    protected SurfaceTexture previewSurfaceTexture;
    protected Surface previewSurface;
    protected CameraView cameraView;
    protected CameraDevice.StateCallback cameraOpenStateCallBack;

    protected String currentCamera = "0";
    protected String FRONT_CAMERA;
    protected String BACK_CAMERA;
    protected MediaFile currentFile;

    protected CameraActivity mActivity;
    protected boolean uiPrepared, cameraPrepared;
    protected Handler cameraHandler;

    public BaseCameraModule(CameraActivity a, CameraView view) {
        mActivity = a;
        cameraView = view;
    }

    @Override
    protected void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (null == cameraOpenStateCallBack) {
                cameraOpenStateCallBack = new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(final CameraDevice camera) {
                        cameraPrepared = true;
                        cameraDevice = camera;
                        startPreview();
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {
                        camera.close();
                        cameraDevice = null;
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {
                        camera.close();
                        cameraDevice = null;
                    }
                };
            }
            cameraManager.openCamera(currentCamera, cameraOpenStateCallBack, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isProgressing() {
        return false;
    }

    @Override
    public void resume() {
        cameraView.resume();
        initModule();
        openCamera();
    }

    @Override
    public void pause() {
        cameraView.pause();
        stopPreivew();
        closeCamera();
        cameraHandler.getLooper().quitSafely();
    }
    protected void initModule() {}

    @Override
    protected void closeCamera() {}

    @Override
    protected void switchCamera() {}

    @Override
    protected void startPreview() {}

    @Override
    protected void stopPreivew() {

    }

    @Override
    public void capture() {}

    @Override
    public void onShutterClicked() {
        //快门按下，进行相关拍照/摄像操作
        capture();
    }

    @Override
    public void onSwitchCamera() {
        //前后摄切换
        switchCamera();
    }

    @Override
    public void startToGallery() {
        //打开最近拍摄的图片
        Uri u = cameraView.getCurrentUri();
        if(null == u)
            return;

        ActivityStarter.startToGallery(u);
    }
}
