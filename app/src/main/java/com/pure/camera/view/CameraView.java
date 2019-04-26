package com.pure.camera.view;

import android.graphics.SurfaceTexture;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pure.camera.R;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.CameraGLView;
import com.pure.camera.opengl.TextureListener;
import com.pure.camera.opengl.UIStateListener;
import com.pure.camera.common.Assert;

public class CameraView extends BaseView implements TextureListener,
        SurfaceTexture.OnFrameAvailableListener {

    private CameraGLView cameraGLView;
    private UIStateListener uiStateListener;
    private boolean cameraGLViewAttached;
    private FrameLayout cameraGroupView;

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
    protected void addView(View view, ViewGroup.LayoutParams layoutParams) {
        checkRoot();
        cameraGroupView.addView(view, layoutParams);
    }

    protected void removeView(View view) {
        checkRoot();
        cameraGroupView.removeView(view);
    }

    /**
     * 根据View的状态来判别是否需要被添加.
     * @param tipsView 目标view.
     * @param layoutParams 布局属性.
     */
    private void addViewIfNeed(View tipsView, ViewGroup.LayoutParams layoutParams) {
        if(null != tipsView.getParent()) {
            if (tipsView.getParent() == cameraGroupView) {
                //view已被添加到Camera Root上，此时view应该被显示到屏幕上
            } else {
                //view已被添加，但是添加到别的地方，不应该执行到此处
                ((ViewGroup)tipsView.getParent()).removeView(tipsView);
                addView(tipsView, layoutParams);
            }
        } else {
            //第一次添加view
            addView(tipsView, layoutParams);
        }

        tipsView.setVisibility(View.VISIBLE);
    }

    /**
     * 当进行模式切换的时候，如果tips view已被添加到camera root上，则需要移除.
     * @param tips
     */
    public void removeRecordTips(View tips) {
        if(null != tips.getParent()) {
            ((ViewGroup)tips.getParent()).removeView(tips);
        }
    }

    /**
     * 显示视频录制状态时候的提示，录制状态，以及录制时长等.
     * @param tipsView
     * @param layoutParams
     */
    public void showRecordTipView(View tipsView, ViewGroup.LayoutParams layoutParams) {
        addViewIfNeed(tipsView, layoutParams);
    }

    /**
     * 录制完成后，需要对tips进行隐藏
     * @param tipsView
     */
    public void hideRecordTipView(View tipsView) {
        tipsView.setVisibility(View.INVISIBLE);
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
     * 为测试而添加的接口，实际的滤镜切换，只在view内部进行，不对外开放
     */
    public void setGrayFilterForDebug() {
        setFilter(CameraFilterManager.FILTER_NAME_GRAY);
    }

    /**
     * 实际的滤镜切换接口.
     * @param filterName 更新的滤镜的名字.
     */
    private void setFilter(String filterName) {
        cameraGLView.getCameraRenderer().updateFilter(
                CameraFilterManager.getInstance().getFilter(filterName));
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
        if(null != mainHandler)
            mainHandler.removeCallbacksAndMessages(null);
    }

    public void destroy() {
        super.destroy();
        cameraGLViewAttached = false;
    }
}
