package com.android.picshow.view.fragment;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.android.picshow.R;
import com.android.picshow.adapter.TimeLineAdapter;
import com.android.picshow.app.PicShowActivity;
import com.android.picshow.view.AppDelegate;

public class TimeLinePageDelegate extends AppDelegate {

    private GridView gridView;
    private View bottomView;
    private Button btnEdit, btnDetail;

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_timeline;
    }

    @Override
    public void initWidget() {
        gridView = getView(R.id.grid);
        bottomView = getView(R.id.bottom_layout);
        btnEdit = getView(R.id.edit);
        btnDetail = getView(R.id.more);
    }

    public void setGridViewAdapter(TimeLineAdapter adapter) {
        gridView.setAdapter(adapter);
    }

    public void setGridViewOnItemClickListener(AdapterView.OnItemClickListener l) {
        gridView.setOnItemClickListener(l);
    }

    public void setGridViewItemOnLongClickListener(AdapterView.OnItemLongClickListener l) {
        gridView.setOnItemLongClickListener(l);
    }

    public void enableDetailAndEdit() {
        if (btnEdit != null) {
            btnEdit.setClickable(true);
            btnEdit.setAlpha(1.0f);
        }
        if (btnDetail != null) {
            btnDetail.setClickable(true);
            btnDetail.setAlpha(1.0f);
        }
    }

    public void disableDetailAndEdit() {
        if(btnEdit != null) {
            btnEdit.setClickable(false);
            btnEdit.setAlpha(0.3f);
        }
        if(btnDetail != null) {
            btnDetail.setClickable(false);
            btnDetail.setAlpha(0.3f);
        }
    }

    public void setBottomViewVisiblity(boolean visiable) {
        ((PicShowActivity)getActivity()).setBottombarVisibility(!visiable);
        bottomView.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    private ProgressDialog dialog;
    public void showProgreeDialog() {
        if(dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    public void dimissDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

}
