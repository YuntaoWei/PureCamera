package com.android.picshow.view.fragment;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.android.picshow.R;
import com.android.picshow.view.AppDelegate;

public class AlbumSetPageDelegate extends AppDelegate {

    GridView gridView;

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_albumset;
    }

    @Override
    public void initWidget() {
        gridView = getView(R.id.grid);
    }

    public void setGridViewAdapter(BaseAdapter adapter) {
        gridView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        gridView.setOnItemClickListener(l);
    }

}
