package com.pure.camera.module;

import android.content.Context;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;

import com.pure.camera.CameraActivity;
import com.pure.camera.common.FileOperatorHelper;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.bean.VideoFile;
import com.pure.camera.opengl.UIStateListener;
import com.pure.camera.ui.VideoTipsView;
import com.pure.camera.view.CameraVideoView;
import com.pure.camera.view.CameraView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class VideoModule extends BaseCameraModule {

    private static final String TAG = "VideoModule";

    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession previewSession;
    private CaptureRequest mCaptureRequest;
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
        cameraView.setCameraOperation(this);
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
        mVideoSize = CameraSettings.chooseVideoSize(screenSize.getWidth(), screenSize.getHeight(), screenSize, backCamera);
        if (uiPrepared && null != previewSurfaceTexture) {
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            cameraView.updatePreviewSize(previewSize.getWidth(), previewSize.getHeight());
        }
    }

    private void releaseRecorder() {
        if(null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        //MediaRecorder在prepare阶段是会创建一个空文件的，在start后才会写入
        //如果只是开启了预览，并没有录制，则要删除prepare时创建的空文件
        File f = new File(currentFile.getFilePath());
        if(f.exists() && f.length() == 0)
            f.delete();
    }

    @Override
    protected void closeCamera() {
        if (isRecording) {
            stopVideoRecord();
        } else {
            releaseRecorder();
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
        if(null != videoTipsView) {
            ((CameraVideoView)cameraView).removeRecordTips(videoTipsView.getTipsView());
        }
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
            LogPrinter.i(TAG, "----VideoModule onCaptureStarted----");
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult partialResult) {
            LogPrinter.i(TAG, "----VideoModule onCaptureProgressed----");
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request, TotalCaptureResult result) {
            LogPrinter.i(TAG, "----VideoModule onCaptureCompleted----");
            //startPreview();
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session,
                                    CaptureRequest request, CaptureFailure failure) {
            LogPrinter.i(TAG, "----VideoModule onCaptureFailed----");
            //startPreview();
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
                //在视频模块中不需要传递capture call back，因为预览帧即是视频帧.
                // 所以每一帧都会回调无任何意义.
                //if (mCaptureCallBack == null)
                //    mCaptureCallBack = new CaptureCallBack();
                //previewSession.setRepeatingRequest(mCaptureRequest, mCaptureCallBack, cameraHandler);
                session.setRepeatingRequest(mCaptureRequest, null, cameraHandler);
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
        mVideoSize = CameraSettings.chooseVideoSize(screenSize.getWidth(), screenSize.getHeight(), new Size(1280, 960), true);
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
        int orientation = cameraView.getContext().getResources().getConfiguration().orientation;
        try {
            CameraCharacteristics p = cameraManager.getCameraCharacteristics(currentCamera);
            orientation = CameraSettings.getModifyOrientation(p, orientation);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            orientation = ORIENTATIONS.get(orientation);
        }
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
        showRecordView();
    }

    private void stopVideoRecord() {
        try {
            previewSession.stopRepeating();
            previewSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mMediaRecorder.stop();
        releaseRecorder();
        isRecording = false;
        FileOperatorHelper.getInstance().updateDataBase(currentFile);
        if (null != mActivity) {
            cameraView.toast(currentFile.getFilePath());
        }

        hideRecordView(false);
        startPreview();
    }

    private VideoTipsView videoTipsView;
    private int time = 0;
    private Runnable updateDuration = new Runnable() {
        @Override
        public void run() {
            if(null != videoTipsView) {
                time ++;
                videoTipsView.setDuration(time);
                cameraView.runOnUiThreadDelay(updateDuration, 1000);
            }
        }
    };
    private void showRecordView() {
        if(null == videoTipsView) {
            videoTipsView = VideoTipsView.getTipsView(cameraView.getContext(), cameraView.getLayoutInflater());
        }
        ((CameraVideoView)cameraView).showRecordTipView(videoTipsView.getTipsView(), videoTipsView.getTipsLayoutParams());
        cameraView.runOnUiThreadDelay(updateDuration, 1000);
    }

    private void hideRecordView(boolean remove) {
        if(null != videoTipsView) {
            if(remove) {
                ((CameraVideoView)cameraView).removeRecordTips(videoTipsView.getTipsView());
            } else {
                ((CameraVideoView)cameraView).hideRecordTipView(videoTipsView.getTipsView());
            }
        }

        time = 0;
        cameraHandler.removeCallbacks(updateDuration);
    }

}
