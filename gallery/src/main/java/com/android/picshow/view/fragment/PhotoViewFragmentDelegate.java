package com.android.picshow.view.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.android.picshow.R;
import com.android.picshow.model.GlideApp;
import com.android.picshow.view.AppDelegate;
import com.bumptech.glide.load.DecodeFormat;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewFragmentDelegate extends AppDelegate {

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_photo_page_item;
    }

    @Override
    public void initWidget() {
    }

    public void setVideoIconVisibility(boolean visiable) {
        getView(R.id.videoIcon).setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
    }

    public void showBitmap(String picPath) {
        GlideApp.with(getActivity())
                .load(picPath)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into((PhotoView)getView(R.id.photo));
    }

    public void freeSource(Fragment f) {
        GlideApp.with(f).onDestroy();
        GlideApp.with(f).clear(getView(R.id.photo));
    }

}
