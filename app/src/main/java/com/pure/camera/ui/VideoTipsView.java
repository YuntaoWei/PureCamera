package com.pure.camera.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pure.camera.R;
import com.pure.camera.common.SizeUtil;

public class VideoTipsView {

    private static final int VIEW_WIDTH = 100;//size in dip
    private static final int VIEW_HEIGHT = 40;//size in dip
    private static final int TOP_MARGIN = 30;//size in dip

    private static VideoTipsView instance;

    private View tipsView;
    private TextView tvRecordDuration;
    private ImageView btnRecordState;
    private int currentTime;
    private FrameLayout.LayoutParams layoutParams;

    public static VideoTipsView getTipsView(Context ctx, LayoutInflater inflater) {
        if(null == instance) {
            instance = new VideoTipsView(ctx, inflater);
        }

        return instance;
    }

    private VideoTipsView(Context ctx, LayoutInflater inflater) {
        tipsView = inflater.inflate(R.layout.video_tips, null);
        tvRecordDuration = tipsView.findViewById(R.id.video_record_duration);
        btnRecordState = tipsView.findViewById(R.id.video_record_state);

        layoutParams = new FrameLayout.LayoutParams(SizeUtil.dpToPixel(ctx, VIEW_WIDTH),
                //FrameLayout.LayoutParams.WRAP_CONTENT);
                SizeUtil.dpToPixel(ctx, VIEW_HEIGHT));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        layoutParams.topMargin = SizeUtil.dpToPixel(ctx, TOP_MARGIN);
    }

    public void setDuration(int timeInSeconds) {
        currentTime = timeInSeconds;
        int minute = timeInSeconds / 60;
        int second = timeInSeconds % 60;
        String s;
        if(minute == 0) {
            s = String.valueOf(second);
        } else {
            s = minute + ":" + second;
        }
        tvRecordDuration.setText(s);
    }

    public void setState(boolean recording) {
        if (recording) {

        }
    }

    public View getTipsView() {
        return tipsView;
    }

    public FrameLayout.LayoutParams getTipsLayoutParams() {
        return layoutParams;
    }

    public void reset() {
        tvRecordDuration.setText("");
        currentTime = 0;
    }

}
