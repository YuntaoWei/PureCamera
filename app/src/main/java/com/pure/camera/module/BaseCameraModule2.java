package com.pure.camera.module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.SparseIntArray;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.bean.MediaFile;
import com.pure.camera.common.ActivityStarter;
import com.pure.camera.manager.Camera2OpenHelper;
import com.pure.camera.manager.CameraOpenCallBack;
import com.pure.camera.manager.camera.Camera2OneCamera;
import com.pure.camera.opengl.UIStateListener;
import com.pure.camera.opengl.data.PreviewSize;
import com.pure.camera.view.CameraView;

public abstract class BaseCameraModule2 extends AbstractCameraModule implements CameraOperation {

    protected static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected SurfaceTexture previewSurfaceTexture;
    protected Surface previewSurface;
    protected CameraView cameraView;

    protected String currentCamera;
    protected MediaFile currentFile;

    protected CameraActivity mActivity;
    protected boolean uiPrepared, cameraPrepared;
    protected Handler cameraHandler;
    protected Camera2OpenHelper openHelper;
    protected PreviewSize previewSize;
    protected Camera2OneCamera mCamera;

    public BaseCameraModule2(CameraActivity a, CameraView view) {
        mActivity = a;
        cameraView = view;
        openHelper = Camera2OpenHelper.getInstance(cameraView.getContext());
        currentCamera = openHelper.hasBackCamera() ? openHelper.getBackCamera() : "0";

        cameraView.setStateListener(new UIStateListener() {
            @Override
            public void onUIPrepare(SurfaceTexture texture) {
                uiPrepared = true;
                previewSurfaceTexture = texture;
                previewSurfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height);
                previewSurface = new Surface(previewSurfaceTexture);
                startPreview();
            }
        });
    }

    @Override
    protected abstract void openCamera();

    @Override
    protected void initModule() {
        HandlerThread cameraThread = new HandlerThread("camera_thread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    @Override
    public boolean isProgressing() {
        return false;
    }

    @Override
    public void resume() {
        initModule();
        cameraView.resume();
        openCamera();
    }

    @Override
    public void pause() {
        cameraView.pause();
        stopPreivew();
        closeCamera();
        cameraHandler.getLooper().quitSafely();
    }

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
        if (null == u)
            return;

        ActivityStarter.startToGallery(u);
    }
}
