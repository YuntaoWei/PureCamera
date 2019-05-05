package com.pure.camera.view;

import android.view.View;
import android.view.ViewGroup;

import com.pure.camera.R;

public class CameraVideoView extends CameraView implements View.OnClickListener {

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();

        setOnClickListener(this, R.id.shutter, R.id.recent_thumbnail, R.id.switcher);
    }

    @Override
    protected void onOpenGLPrepared() {
        //TODO 录像模式默认不设置任何滤镜效果
        setFilter(null);
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
     * 当进行模式切换的时候，如果tips view已被添加到camera root上，则需要移除.
     * @param tips
     */
    public void removeRecordTips(View tips) {
        if(null != tips.getParent()) {
            ((ViewGroup)tips.getParent()).removeView(tips);
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
        }
    }
}
