package com.android.picshow.view.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.picshow.R;
import com.android.picshow.adapter.PageControlAdapter;
import com.android.picshow.app.TimeLinePage;
import com.android.picshow.utils.PageFactory;
import com.android.picshow.view.AppDelegate;

public class PicShowActivityDelegate extends AppDelegate {

    ViewPager viewPager;

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_main;
    }

    @Override
    public void initWidget() {
        viewPager = getView(R.id.vpager);
        viewPager.setAdapter(
                new PageControlAdapter(((FragmentActivity)getActivity()).getSupportFragmentManager(), PageFactory.getMainPage()));
    }

    @Override
    public Toolbar getToolbar() {
        return getView(R.id.bottombar);
    }

    public void switchPage(int index) {
        if(viewPager == null)
            viewPager = getView(R.id.vpager);


        viewPager.setCurrentItem(index);
    }

    public void setTitleVisibility(boolean album) {
        if(album) {
            getView(R.id.album_title).setVisibility(View.INVISIBLE);
        } else {
            getView(R.id.album_title).setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String s) {
        ((TextView)getView(R.id.album_title)).setText(s);
    }

    public void setTitle(int res) {
        ((TextView)getView(R.id.album_title)).setText(res);
    }

    /**
     * change the focus button style
     * @param index
     */
    public void changeButtonSelectedStatus(int index) {
        Resources res = getActivity().getResources();
        switch (index) {
            case PageFactory.INDEX_ALBUMSET:
                //album button selected,should display high light status.
                Drawable albumSelectDrawable = res.getDrawable(R.drawable.album_n);
                albumSelectDrawable.setBounds(0, 0,
                        albumSelectDrawable.getMinimumWidth(), albumSelectDrawable.getMinimumHeight());

                ((Button)getView(R.id.btn_album)).setTextColor(res.getColor(R.color.select_text_color));
                ((Button)getView(R.id.btn_album)).setCompoundDrawables(null, albumSelectDrawable, null, null);

                //photo button display default status.
                Drawable photoDefaultDrawable = res.getDrawable(R.drawable.photo_gray_n);
                photoDefaultDrawable.setBounds(0, 0,
                        photoDefaultDrawable.getMinimumWidth(), photoDefaultDrawable.getMinimumHeight());

                ((Button)getView(R.id.btn_photo)).setTextColor(res.getColor(R.color.default_text_color));
                ((Button)getView(R.id.btn_photo)).setCompoundDrawables(null, photoDefaultDrawable, null, null);
                return;

            case PageFactory.INDEX_TIMELINE:
                //album button should display default status.
                Drawable albumDefaultDrawable = res.getDrawable(R.drawable.album_gray_n);
                albumDefaultDrawable.setBounds(0, 0,
                        albumDefaultDrawable.getMinimumWidth(), albumDefaultDrawable.getMinimumHeight());

                ((Button)getView(R.id.btn_album)).setTextColor(res.getColor(R.color.default_text_color));
                ((Button)getView(R.id.btn_album)).setCompoundDrawables(null, albumDefaultDrawable, null, null);


                //photo button selected,should display high light status.
                Drawable photoSelectDrawable = res.getDrawable(R.drawable.photo_n);
                photoSelectDrawable.setBounds(0, 0,
                        photoSelectDrawable.getMinimumWidth(), photoSelectDrawable.getMinimumHeight());

                ((Button)getView(R.id.btn_photo)).setTextColor(res.getColor(R.color.select_text_color));
                ((Button)getView(R.id.btn_photo)).setCompoundDrawables(null, photoSelectDrawable, null, null);
                return;

            default:
                break;
        }
    }

    public void setBottomBarVisibility(boolean visibility) {
        getView(R.id.bottombar).setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public boolean onBackPressed(int current) {
        PageControlAdapter adapter = (PageControlAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(current);
        if(f instanceof TimeLinePage) {
            return ((TimeLinePage)f).onBackPressed();
        }
        return false;
    }

}
