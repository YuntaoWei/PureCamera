package com.pure.camera.view;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.pure.camera.R;
import com.pure.camera.ui.CameraGLView;
import com.pure.camera.ui.TextureListener;
import com.pure.camera.ui.UIStateListener;

public class CameraPhotoView extends BaseView implements TextureListener,
        SurfaceTexture.OnFrameAvailableListener {

    private CameraGLView cameraGLView;
    private UIStateListener uiStateListener;

    public void addCameraGLView() {
        cameraGLView = new CameraGLView(getContext());
        cameraGLView.setTextureListener(this);
        ((ViewGroup)getView(R.id.camera_content)).addView(cameraGLView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setStateListener(UIStateListener l) {
        uiStateListener = l;
    }

    @Override
    public void onTexturePrepared(SurfaceTexture texture) {
        Log.i("ttt", "onTexturePrepared");
        texture.setOnFrameAvailableListener(this);
        if(null != uiStateListener)
            uiStateListener.onUIPrepare(texture);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i("ttt", "onFrameAvailable");
        cameraGLView.requestRender();
    }

    @Override
    public void resume() {
        cameraGLView.onResume();
    }

    @Override
    public void pause() {
        cameraGLView.onPause();
    }

}
