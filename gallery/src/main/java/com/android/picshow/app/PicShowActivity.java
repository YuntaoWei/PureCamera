package com.android.picshow.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.picshow.R;
import com.android.picshow.presenter.BaseActivity;
import com.android.picshow.utils.PageFactory;
import com.android.picshow.view.activity.PicShowActivityDelegate;


public class PicShowActivity extends BaseActivity<PicShowActivityDelegate> implements View.OnClickListener, ViewPager.OnPageChangeListener{

    int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.btn_album, R.id.btn_photo, R.id.search_photo);
        viewDelegate.setOnFragmentPageChangeListener(this, R.id.vpager);
    }

    @Override
    protected void initView() {
        super.initView();
        viewDelegate.changeButtonSelectedStatus(PageFactory.INDEX_TIMELINE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_album) {
            viewDelegate.switchPage(PageFactory.INDEX_ALBUMSET);

        } else if (i == R.id.btn_photo) {
            viewDelegate.switchPage(PageFactory.INDEX_TIMELINE);

        } else if (i == R.id.search_photo) {
        } else {
        }
    }

    @Override
    protected Class getDelegateClass() {
        return PicShowActivityDelegate.class;
    }

    @Override
    public void onBackPressed() {
        if(!viewDelegate.onBackPressed(currentPageIndex))
            super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPageIndex = position;
        viewDelegate.changeButtonSelectedStatus(position);
        if(position == PageFactory.INDEX_TIMELINE)
            viewDelegate.setTitle(R.string.btn_photo);
        else
            viewDelegate.setTitle(R.string.btn_album);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setBottombarVisibility(boolean visible) {
        viewDelegate.setBottomBarVisibility(visible);
    }

}
