package com.android.picshow.view.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.picshow.R;
import com.android.picshow.adapter.TimeLineAdapter;
import com.android.picshow.view.AppDelegate;

public class AlbumPageDelegate extends AppDelegate {

    private GridView gridView;
    private View bottomView;

    @Override
    public int getRootLayoutId() {
        return R.layout.picshow_album;
    }

    @Override
    public void initWidget() {
        gridView = getView(R.id.grid);
        bottomView = getView(R.id.bottom_layout);
    }

    @Override
    public Toolbar getToolbar() {
        return getView(R.id.topbar);
    }

    public void setToolBarNavigationClickListener(View.OnClickListener l) {
        getToolbar().setNavigationOnClickListener(l);
    }

    public void setGridViewAdapter(TimeLineAdapter adapter) {
        gridView.setAdapter(adapter);
    }

    public void setGridViewClickListener(AdapterView.OnItemClickListener l, AdapterView.OnItemLongClickListener ll) {
        if (l != null)
            gridView.setOnItemClickListener(l);

        if (ll != null)
            gridView.setOnItemLongClickListener(ll);
    }

    public void setBottomViewVisibility(boolean visiable) {
        bottomView.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    public void enableEditAndMore() {
        View edit = getView(R.id.edit);
        edit.setClickable(true);
        edit.setAlpha(1.0f);

        View detail = getView(R.id.more);
        detail.setClickable(true);
        detail.setAlpha(1.0f);
    }

    public void disableEditAndMore() {
        View edit = getView(R.id.edit);
        edit.setClickable(false);
        edit.setAlpha(0.3f);

        View detail = getView(R.id.more);
        detail.setClickable(false);
        detail.setAlpha(0.3f);
    }

    private ProgressDialog dialog;

    public void showProgreeDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
        }
        dialog.show();

    }

    public void dimissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void updateItem(View v, boolean select) {
        TimeLineAdapter.ViewHolder vh = (TimeLineAdapter.ViewHolder) v.getTag();
        if (vh != null) {
            vh.selectIcon.setChecked(select);
        }
    }

    public void setTitle(String s) {
        ((TextView) getView(R.id.title)).setText(s);
    }

}
