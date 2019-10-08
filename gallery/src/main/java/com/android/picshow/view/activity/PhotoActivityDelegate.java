package com.android.picshow.view.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.picshow.R;
import com.android.picshow.adapter.PhotoPageAdapter;
import com.android.picshow.ui.PicPopupWindow;
import com.android.picshow.utils.PicShowUtils;
import com.android.picshow.view.AppDelegate;
import com.pure.commonbase.ApiHelper;

public class PhotoActivityDelegate extends AppDelegate {

    boolean fullScreen;
    private ViewPager mPager;
    String[] moreMenu;

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_photo_page;
    }

    @Override
    public void initWidget() {
        mPager = getView(R.id.photo_pager);
        mPager.setOffscreenPageLimit(PicShowUtils.MAX_LOAD);
    }

    @Override
    public Toolbar getToolbar() {
        return getView(R.id.photo_toolbar);
    }

    public View getBottomView() {
        return getView(R.id.bottom_view);
    }

    public void setToolbarNavigationClickListener(View.OnClickListener cl, int toolbarID) {
        ((Toolbar) getView(toolbarID)).setNavigationOnClickListener(cl);
    }

    public void setPagerAdapter(PhotoPageAdapter adapter) {
        if (mPager == null) {
            mPager = getView(R.id.photo_pager);
        }
        mPager.setAdapter(adapter);
    }

    public void switchPage(int id) {
        if (mPager == null) {
            mPager = getView(R.id.photo_pager);
        }
        mPager.setCurrentItem(id);
    }

    public void enterFullScreen() {
        if (fullScreen || getToolbar() == null || rootView == null || getBottomView() == null)
            return;

        //bottomView.setVisibility(View.GONE);

        int flag = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        if (ApiHelper.HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE)
            flag = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        getToolbar().setVisibility(View.INVISIBLE);
        rootView.setSystemUiVisibility(flag);
        fullScreen = true;
    }

    public void exitFullScreen() {
        if (!fullScreen || getToolbar() == null || rootView == null || getBottomView() == null)
            return;

        //bottomView.setVisibility(View.VISIBLE);

        getToolbar().setVisibility(View.VISIBLE);
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        fullScreen = false;
    }

    public void toggleFullScreen() {
        if (fullScreen) {
            exitFullScreen();
        } else {
            enterFullScreen();

        }
        toggleBottomView();
    }

    private void toggleBottomView() {
        if (getBottomView() == null)
            return;

        if (getBottomView().getVisibility() == View.VISIBLE)
            getBottomView().setVisibility(View.GONE);
        else
            getBottomView().setVisibility(View.VISIBLE);
    }

    public void showMoreMenu(PicPopupWindow.PicPopupWindowListener itemListener) {
        if (moreMenu == null) {
            moreMenu = getActivity().getResources().getStringArray(R.array.more_menu);
        }

        PicPopupWindow moreMenuWindow = PicPopupWindow.getPicPopupWindow(getActivity());
        for (String item : moreMenu
        ) {
            moreMenuWindow.addPopupWindowItem(item, item, itemListener);
        }

        moreMenuWindow.setPopupWindowTitle(R.string.more);
        moreMenuWindow.show(getBottomView());
    }
}
