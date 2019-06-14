package com.pure.camera.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.text.TextUtils;

import com.pure.camera.manager.camera.Camera2OneCamera;
import com.pure.camera.manager.camera.CameraParameter;

import java.util.HashMap;

public class Camera2OpenHelper {

    private static Camera2OpenHelper INSTANCE;

    private CameraManager cameraManager;
    private HashMap<String, CameraParameter> cameraParameters = new HashMap<>(2);
    private String backCameraID, frontCameraID;
    private CameraDevice.StateCallback openStateCallBack;

    //camera info
    private String[] allCameras;

    public static Camera2OpenHelper getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (Camera2OpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new Camera2OpenHelper(ctx);
            }
        }

        return INSTANCE;
    }

    private Camera2OpenHelper(Context ctx) {
        cameraManager = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
        initialCameraInfo();
    }

    private void initialCameraInfo() {
        try {
            allCameras = cameraManager.getCameraIdList();
            for (String cameraID : allCameras
                    ) {
                CameraCharacteristics parm = cameraManager.getCameraCharacteristics(cameraID);
                CameraParameter cp = new CameraParameter(cameraID, parm);
                if (cp.isBackCamera()) {
                    backCameraID = cameraID;
                }

                if (cp.isFrontCamera()) {
                    frontCameraID = cameraID;
                }
                cameraParameters.put(cameraID, cp);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public String getBackCamera() {
        return backCameraID;
    }

    public String getFrontCamera() {
        return frontCameraID;
    }

    public int getCameraCount() {
        return allCameras.length;
    }

    public boolean hasBackCamera() {
        return !TextUtils.isEmpty(backCameraID);
    }

    public boolean hasFrontCamera() {
        return !TextUtils.isEmpty(frontCameraID);
    }

    @SuppressLint("MissingPermission")
    public void openCamera(final String cameraID, final CameraOpenCallBack openCallBack, Handler handler) {
        if (TextUtils.isEmpty(cameraID) || openCallBack == null)
            return;

        if (openStateCallBack == null) {
            openStateCallBack = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice c) {
                    Camera2OneCamera camera = new Camera2OneCamera(c);
                    camera.setCameraParameter(cameraParameters.get(cameraID));
                    openCallBack.onCameraOpened(camera);
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                    openCallBack.onCameraDisconnect();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    openCallBack.onCameraOpenFalied(error);
                }
            };
        }

        try {
            cameraManager.openCamera(cameraID, openStateCallBack, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CameraParameter getParameterById(String id) {
        return cameraParameters.get(id);
    }

}