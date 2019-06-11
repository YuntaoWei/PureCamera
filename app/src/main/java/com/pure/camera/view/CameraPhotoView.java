package com.pure.camera.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.common.SizeUtil;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.module.OnFilterChangeListener;
import com.pure.camera.opengl.data.PreviewSize;

public class CameraPhotoView extends CameraView {

    private static final String TAG = "CameraPhotoView";
    private static final int FILTER_PREVIEW_BUTTON = 0x123;
    private ImageView filterPreviewButton;
    private PreviewSize screenSize;

    @Override
    public void addCameraGLView() {
        super.addCameraGLView();
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

        filterPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFilterPreview();
            }
        });
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
    protected void showSettingView() {
        LogPrinter.i("ttt", "showSettingView");
        /*if(null == settingWindow) {
            ListView menuList = new ListView(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    R.layout.layout_setting, R.id.tv_name);
            String[] photo = getStringArray(R.array.photo_setting);
            adapter.addAll(photo);
            menuList.setAdapter(adapter);
            settingWindow = new PopupWindow(menuList, 200, 300);
            //settingWindow.showAsDropDown(getView(R.id.img_setting), Gravity.NO_GRAVITY, 0, 0);
            settingWindow.showAtLocation(getView(R.id.img_setting), Gravity.START | Gravity.TOP, 0, 0);
        } else {
            settingWindow.showAsDropDown(getView(R.id.img_setting));
        }*/

        if(null == menuList) {
            menuList = getView(R.id.menu_list);
            final String[] photo = getStringArray(R.array.photo_setting);
            menuList.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return photo.length;
                }

                @Override
                public String getItem(int position) {
                    return photo[position];
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView;
                    if(null == convertView) {
                        convertView = getLayoutInflater().inflate(R.layout.layout_setting, null);
                        textView = convertView.findViewById(R.id.tv_name);
                        convertView.setTag(textView);
                    } else {
                        textView = (TextView) convertView.getTag();
                    }

                    textView.setText(getItem(position));
                    return convertView;
                }
            });
        }
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) menuList.getLayoutParams();
        if(lp.endToStart == 0)
            lp.endToStart = -1;
        else
            lp.endToStart = 0;
        lp.topMargin = SizeUtil.dpToPixel(getContext(), 26);
        menuList.setLayoutParams(lp);
    }

}
