package com.pure.camera.module;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.R;
import com.pure.camera.data.VideoFile;
import com.pure.camera.ui.UIStateListener;
import com.pure.camera.util.CameraSettings;
import com.pure.camera.util.LogPrinter;
import com.pure.camera.view.CameraView;

import java.io.IOException;
import java.util.Arrays;

public class VideoModule extends BaseCameraModule {

    private static final String TAG = "VideoModule";

    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession previewSession;
    private CaptureRequest mCaptureRequest;
    private CaptureCallBack mCaptureCallBack;
    private MediaRecorder mMediaRecorder;
    private Size screenSize, previewSize, mVideoSize;
    private boolean isRecording = false;

    public VideoModule(CameraActivity activity, CameraView view) {
        super(activity, view);
        initUI();
    }

    private void initUI() {
        cameraView.setStateListener(new UIStateListener() {
            @Override
            public void onUIPrepare(SurfaceTexture texture) {
                LogPrinter.i(TAG, "onUIPrepare : " + texture);
                if (null != previewSize) {
                    texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                }
                previewSurfaceTexture = texture;
                previewSurface = new Surface(previewSurfaceTexture);
                uiPrepared = true;
                startPreview();
            }
        });

        cameraView.addCameraGLView();
        cameraView.setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    /**
     *  模块初始化。
     */
    @Override
    protected void initModule() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIDs = cameraManager.getCameraIdList();
                if (cameraIDs.length <= 0)
                    throw new RuntimeException("Can not find camera on this device!");
                for (String s : cameraIDs) {
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(s);
                    if (parameters.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_BACK) {
                        BACK_CAMERA = s;
                        currentCamera = s;
                        CameraSettings.initializeBackCameraSettings(parameters);
                    } else if (parameters.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        FRONT_CAMERA = s;
                        CameraSettings.initializeFrontCameraSettings(parameters);
                    }
                }

                if (TextUtils.isEmpty(currentCamera)) {
                    currentCamera = cameraIDs[0];
                    CameraCharacteristics parameters = cameraManager.getCameraCharacteristics(currentCamera);
                    CameraSettings.initializeBackCameraSettings(parameters);
                }


                DisplayMetrics dm = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                screenSize = new Size(dm.widthPixels, dm.heightPixels);
                LogPrinter.i(TAG, "Preview size : " +
                        screenSize.getWidth() + "x" + screenSize.getHeight());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        resetCameraPreviewParemeters(true);
        HandlerThread handlerThread = new HandlerThread("CameraThread");
        handlerThread.start();
        cameraHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    /**
     * 重置预览参数，比如预览尺寸，拍摄尺寸，WB，AE，ANTI-BINDING等。
     * @param backCamera
     */
    private void resetCameraPreviewParemeters(boolean backCamera) {
        previewSize = CameraSettings.choosePreviewSize(screenSize, backCamera);
        mVideoSize = CameraSettings.choosePictureSize(screenSize.getWidth(), screenSize.getHeight(), screenSize, backCamera);
        if (uiPrepared && null != previewSurfaceTexture)
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
    }

    @Override
    protected void closeCamera() {
        if (isRecording) {
            stopVideoRecord();
        }

        if (null != previewSession) {
            previewSession.close();
            previewSession = null;
        }

        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }

        cameraPrepared = false;
    }

    @Override
    protected void switchCamera() {
        if (TextUtils.isEmpty(BACK_CAMERA) || TextUtils.isEmpty(FRONT_CAMERA))
            return;

        boolean isBack = currentCamera.equals(BACK_CAMERA);
        currentCamera = isBack ? FRONT_CAMERA : BACK_CAMERA;
        isBack = !isBack;

        closeCamera();
        resetCameraPreviewParemeters(isBack);
        openCamera();
    }

    @Override
    public boolean isProgressing() {
        return false;
    }

    private class CaptureCallBack extends CameraCaptureSession.CaptureCallback {
        @Override
        public void onCaptureStarted(CameraCaptureSession session,
                                     CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session,
                                    CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.i(TAG, "onCaptureFailed");
        }
    }

    /**
     * 预览会话创建状态回调。
     */
    private class PreviewSessionCallBack extends CameraCaptureSession.StateCallback {


        @Override
        public void onConfigured(CameraCaptureSession session) {
            if (cameraDevice == null)
                return;
            previewSession = session;
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mCaptureRequest = mPreviewBuilder.build();
            try {
                if (mCaptureCallBack == null)
                    mCaptureCallBack = new CaptureCallBack();
                previewSession.setRepeatingRequest(mCaptureRequest, mCaptureCallBack, cameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    }

    /**
     * 初始化MediaRecorder，设置相关参数
     */
    private void configMediaRecorder() {
        mVideoSize = CameraSettings.chooseVideoSize(1280, 960, new Size(1280, 960), true);
        currentFile = new VideoFile(mVideoSize.getWidth(), mVideoSize.getHeight(),
                mActivity.getResources().getConfiguration().orientation);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(currentFile.getFilePath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = ORIENTATIONS.get(rotation);
        mMediaRecorder.setOrientationHint(orientation);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startPreview() {
        if (!uiPrepared || !cameraPrepared)
            return;

        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
                configMediaRecorder();
            }

            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mPreviewBuilder.addTarget(previewSurface);
            mPreviewBuilder.addTarget(mMediaRecorder.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface,
                    mMediaRecorder.getSurface()),
                    new PreviewSessionCallBack(), cameraHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void capture() {
        if (isRecording) {
            isRecording = false;
            stopVideoRecord();
        } else {
            cameraView.toast("Start Record");
            isRecording = true;
            startVideoRecord();
        }
    }

    private void startVideoRecord() {
        mMediaRecorder.start();
    }

    private void stopVideoRecord() {
        try {
            previewSession.stopRepeating();
            previewSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder = null;
        isRecording = false;
        if (null != mActivity) {
            cameraView.toast(currentFile.getFilePath());
        }
        startPreview();
    }
}
