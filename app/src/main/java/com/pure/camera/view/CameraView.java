package com.pure.camera.view;

import android.graphics.SurfaceTexture;
import android.view.View;
import android.view.ViewGroup;

import com.pure.camera.R;
import com.pure.camera.ui.CameraGLView;
import com.pure.camera.ui.TextureListener;
import com.pure.camera.ui.UIStateListener;

public class CameraView extends BaseView implements TextureListener,
        SurfaceTexture.OnFrameAvailableListener {

    private CameraGLView cameraGLView;
    private UIStateListener uiStateListener;

    /**
     * 添加CameraGLView，用于预览显示Camera画面
     */
    public void addCameraGLView() {
        cameraGLView = new CameraGLView(getContext());
        cameraGLView.setTextureListener(this);
        ((ViewGroup)getView(R.id.camera_content)).addView(cameraGLView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 在CameraView所在的FrameLayout父布局中添加子View.
     * @param view 子View.
     * @param layoutParams 布局属性。
     */
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {

    }

    /**
     * 添加布局初始化完成的回调，主要传递创建好的SurfaceTexture.
     * @param l
     */
    public void setStateListener(UIStateListener l) {
        uiStateListener = l;
    }

    @Override
    public void onTexturePrepared(SurfaceTexture texture) {
        texture.setOnFrameAvailableListener(this);
        if(null != uiStateListener)
            uiStateListener.onUIPrepare(texture);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
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
