package com.pure.camera.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.module.OnFilterChangeListener;
import com.pure.camera.opengl.data.PreviewSize;

public class CameraPhotoView extends CameraView implements View.OnClickListener {

    private static final String TAG = "CameraPhotoView";
    private static final int FILTER_PREVIEW_BUTTON = 0x123;
    private ImageView filterPreviewButton;
    private PreviewSize screenSize;

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();
        setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    @Override
    public void resume() {
        super.resume();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dp = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dp);
        screenSize = new PreviewSize(dp.widthPixels, dp.heightPixels);
        LogPrinter.i(TAG, "screen size : " + screenSize);
        cameraGLView.setScreenSize(screenSize);
        initPhotoView();
    }

    @Override
    protected void onOpenGLPrepared() {
        setFilter(CameraFilterManager.FILTER_NAME_ORIGINAL);
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
            filterPreviewButton.setImageResource(R.mipmap.ic_filter_128);
            filterPreviewButton.setOnClickListener(this);
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        lp.rightMargin = 50;
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

    public void setFilterChangeListener(OnFilterChangeListener l) {
        cameraGLView.setOnFilterChangeListener(l);
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
                switchFilterPreview();
                break;
        }
    }
}
