package com.pure.camera.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.CameraFilterManager;

public class CameraVideoView extends CameraView {

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();
    }

    @Override
    protected void onOpenGLPrepared() {
        //TODO 录像模式默认不设置任何滤镜效果
        setFilter(CameraFilterManager.FILTER_NAME_ORIGINAL);
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
    protected void showSettingView() {
        LogPrinter.i("ttt", "showSettingView");
        if(null == settingWindow) {
            ListView menuList = new ListView(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    R.layout.layout_setting, R.id.tv_name);
            adapter.add("test1");
            adapter.add("test2");
            adapter.add("test3");
            adapter.add("test4");
            menuList.setAdapter(adapter);
            settingWindow = new PopupWindow(menuList, 200, 300);
            settingWindow.showAsDropDown(getView(R.id.img_setting));
        } else {
            settingWindow.showAsDropDown(getView(R.id.img_setting));
        }
    }
}
