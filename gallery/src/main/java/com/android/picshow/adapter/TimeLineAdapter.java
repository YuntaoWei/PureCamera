package com.android.picshow.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.picshow.R;
import com.android.picshow.model.GlideApp;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.ui.SelectionManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.trustyapp.gridheaders.TrustyGridSimpleAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuntao.wei on 2017/12/14.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class TimeLineAdapter extends BaseAdapter implements TrustyGridSimpleAdapter {

    private PhotoItem[] datas = new PhotoItem[0];
    private Activity attachActivity;
    private int thubNailSize = 0;
    private Resources mRes;
    private int currentHeadCount = 0;
    private String pattern;
    private boolean selected = false;
    private SelectionManager selectionManager;

    public TimeLineAdapter(Activity a) {
        this(a, null, null);
    }

    public TimeLineAdapter(Activity a, PhotoItem[] items, SelectionManager s) {
        attachActivity = a;
        pattern = a.getString(R.string.timeline_title_format);
        datas = items == null ? datas : items;
        mRes = a.getResources();
        selectionManager = s;
    }

    public void setData(PhotoItem[] items) {
        datas = items;
        notifyDataSetChanged();
    }

    public void destroy() {
        attachActivity = null;
        mRes = null;
        datas = null;
        System.gc();
    }

    public void setDecodeSize(int size) {
        thubNailSize = size;
    }

    public void setSelectState(boolean select) {
        selected = select;
        notifyDataSetChanged();
    }

    public boolean getSelectState() {
        return selected;
    }

    public void updateItem(int position, boolean select) {

    }

    public long getTimeId(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(mRes.getString(R.string.timeline_title_format));
        Date mDate = null;

        try {
            mDate = sdf.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mDate.getTime();
    }

    @Override
    public long getHeaderId(int position) {
        return getTimeId(getItem(position).getDateAdd(pattern));
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
        HeaderViewHolder mHeadViewHolder = null;
        if (convertView==null){
            mHeadViewHolder = new HeaderViewHolder();
            convertView = attachActivity.getLayoutInflater().inflate(R.layout.picshow_item_time_header,null);

            mHeadViewHolder.tvTimeHeader = convertView.findViewById(R.id.tv_time_header);
            mHeadViewHolder.tvCount = convertView.findViewById(R.id.count);

            convertView.setTag(mHeadViewHolder);
        }else {
            mHeadViewHolder = (HeaderViewHolder)convertView.getTag();
        }

        mHeadViewHolder.tvTimeHeader.setText(getItem(position).getDateAdd(pattern));

        return convertView;
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public PhotoItem getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return datas[position].getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("wyt","getView : " + position);
        ViewHolder v = null;
        int type = getItem(position).getItemType();
        if(convertView != null) {
            v = (ViewHolder)convertView.getTag();
            if(v == null) {
                v = new ViewHolder();
                v.imgView = convertView.findViewById(R.id.img);
                v.videoIcon = convertView.findViewById(R.id.videoIcon);
                v.selectIcon = convertView.findViewById(R.id.select);
            }
        } else {
            convertView = attachActivity.getLayoutInflater().inflate(R.layout.picshow_img_item,null);
            v = new ViewHolder();
            v.imgView = convertView.findViewById(R.id.img);
            v.videoIcon = convertView.findViewById(R.id.videoIcon);
            v.selectIcon = convertView.findViewById(R.id.select);
        }
        convertView.setTag(v);
        if(v != null && v.imgView != null) {
            if(thubNailSize != 0) {
                GlideApp.with(attachActivity)
                        .load(getItem(position).getPath())
                        .override(thubNailSize)
                        .placeholder(R.drawable.other)
                        .centerCrop()
                        .dontAnimate()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into(v.imgView);
            } else {
                GlideApp.with(attachActivity)
                        .load(getItem(position).getPath())
                        .placeholder(R.drawable.other)
                        .centerCrop()
                        .dontAnimate()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .signature(new MediaStoreSignature(type == PhotoItem.TYPE_VIDEO ? "video/*" : "image/*"
                                ,getItem(position).getDateToken(), 0))
                        .into(v.imgView);
            }
            if(type == PhotoItem.TYPE_VIDEO)
                v.videoIcon.setVisibility(View.VISIBLE);
            else
                v.videoIcon.setVisibility(View.GONE);
            if(selected)
                v.selectIcon.setVisibility(View.VISIBLE);
            else
                v.selectIcon.setVisibility(View.GONE);
            v.selectIcon.setChecked(selectionManager == null ? false : selectionManager.isSelected(position));
        }
        return convertView;
    }

    public class HeaderViewHolder {
        public TextView tvTimeHeader;
        public TextView tvCount;
    }

    public class ViewHolder {
        public ImageView imgView;
        public ImageView videoIcon;
        public CheckBox selectIcon;
    }

}
