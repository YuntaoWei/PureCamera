package com.pure.camera.view;

import android.view.View;
import android.view.ViewGroup;

public class CameraVideoView extends CameraView {

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();
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

}
