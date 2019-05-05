package com.pure.camera.view;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.CameraFilterManager;

public class CameraPhotoView extends CameraView implements View.OnClickListener {

    private static final String TAG = "CameraPhotoView";
    private static final int FILTER_PREVIEW_BUTTON = 0x123;
    ImageView filterPreviewButton;

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();
        setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    @Override
    public void resume() {
        super.resume();
        initPhotoView();
    }

    @Override
    protected void onOpenGLPrepared() {
        //TODO 测试先行把拍照模式的滤镜设置为gray
        setFilter(CameraFilterManager.FILTER_NAME_GRAY);
    }

    @Override
    public void pause() {
        super.pause();
        clearPhotoView();
    }

    /**
     * 在这里添加一些Photo界面独有的ui元素以及操作
     */
    private void initPhotoView() {
        if(null == filterPreviewButton) {
            filterPreviewButton = new ImageView(getContext());
            filterPreviewButton.setId(FILTER_PREVIEW_BUTTON);
            filterPreviewButton.setImageResource(R.mipmap.ic_launcher);
            filterPreviewButton.setOnClickListener(this);
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addViewIfNeed(filterPreviewButton, lp);
    }

    /**
     * 这里清楚掉所有独有的元素以及操作
     */
    private void clearPhotoView() {
        if(null != filterPreviewButton) {
            ((ViewGroup)filterPreviewButton.getParent()).removeView(filterPreviewButton);
        }
    }

    @Override
    public void onClick(View v) {
        if(null == cameraOperation)
            return;

        switch (v.getId()) {
            case R.id.shutter:
                cameraOperation.onShutterClicked();
                break;

            case R.id.switcher:
                cameraOperation.onSwitchCamera();
                break;

            case R.id.recent_thumbnail:
                cameraOperation.startToGallery();
                break;

            case FILTER_PREVIEW_BUTTON:
                showFilterPreview(true);
                break;
        }
    }

}
