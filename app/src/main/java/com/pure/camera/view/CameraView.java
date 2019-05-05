package com.pure.camera.view;

import android.graphics.SurfaceTexture;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pure.camera.R;
import com.pure.camera.common.Assert;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.module.CameraOperation;
import com.pure.camera.opengl.CameraGLView;
import com.pure.camera.opengl.TextureListener;
import com.pure.camera.opengl.UIStateListener;

public class CameraView extends BaseView implements TextureListener,
        SurfaceTexture.OnFrameAvailableListener {

    private CameraGLView cameraGLView;
    private UIStateListener uiStateListener;
    private boolean cameraGLViewAttached;
    private FrameLayout cameraGroupView;

    protected CameraOperation cameraOperation;

    /**
     * 添加CameraGLView，用于预览显示Camera画面
     */
    public void addCameraGLView() {
        if(cameraGLViewAttached)
            return;
        checkRoot();
        cameraGLView = new CameraGLView(getContext());
        cameraGLView.setTextureListener(this);
        cameraGroupView.addView(cameraGLView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        cameraGLViewAttached = true;
    }

    /**
     * 在CameraView所在的FrameLayout父布局中添加子View.
     * @param view 子View.
     * @param layoutParams 布局属性。
     */
    private void addView(View view, ViewGroup.LayoutParams layoutParams) {
        checkRoot();
        cameraGroupView.addView(view, layoutParams);
    }

    /**
     * 根据View的状态来判别是否需要被添加.
     * @param view 目标view.
     * @param layoutParams 布局属性.
     */
    protected void addViewIfNeed(View view, ViewGroup.LayoutParams layoutParams) {
        if(null != view.getParent()) {
            if (view.getParent() == cameraGroupView) {
                //view已被添加到Camera Root上，此时view应该被显示到屏幕上
            } else {
                //view已被添加，但是添加到别的地方，不应该执行到此处
                ((ViewGroup)view.getParent()).removeView(view);
                addView(view, layoutParams);
            }
        } else {
            //第一次添加view
            addView(view, layoutParams);
        }

        view.setVisibility(View.VISIBLE);
    }

    /**
     * 检测camera group是否为空，并进行初始化，若还是为空，则停止程序运行.
     */
    private void checkRoot() {
        if(null == cameraGroupView) {
            cameraGroupView = getView(R.id.camera_content);
        }

        Assert.assertNotNull(cameraGroupView);
    }

    /**
     * 实际的滤镜切换接口.
     * @param filterName 更新的滤镜的名字.
     */
    protected void setFilter(String filterName) {
        cameraGLView.getCameraRenderer().updateFilter(
                CameraFilterManager.getInstance().getFilter(filterName));
    }

    /**
     * 显示所有filter的预览
     */
    protected void showFilterPreview(boolean flag) {
        cameraGLView.getCameraRenderer().showFilterPreview(flag);
    }

    /**
     * 添加布局初始化完成的回调，主要传递创建好的SurfaceTexture.
     * @param l
     */
    public void setStateListener(UIStateListener l) {
        uiStateListener = l;
    }

    public void setCameraOperation(CameraOperation co) {
        cameraOperation = co;
    }

    public void updatePreviewSize(int w, int h) {
        cameraGLView.getCameraRenderer().updateTexture(w, h);
    }

    @Override
    public void onTexturePrepared(SurfaceTexture texture) {
        texture.setOnFrameAvailableListener(this);
        if(null != uiStateListener)
            uiStateListener.onUIPrepare(texture);

        onOpenGLPrepared();
    }

    /**
     * 有些UI设置操作依赖于整个环境就绪才能进行
     * 在这里回调之后，可以进行filter，texture size等元素的设定
     */
    protected void onOpenGLPrepared() {}

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
        if(null != mainHandler)
            mainHandler.removeCallbacksAndMessages(null);
    }

    public void destroy() {
        super.destroy();
        cameraGLViewAttached = false;
    }
}
