package com.android.picshow.app;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.picshow.R;
import com.android.picshow.model.Album;
import com.android.picshow.model.AlbumSetDataLoader;
import com.android.picshow.model.GlideApp;
import com.android.picshow.model.LoadListener;
import com.android.picshow.presenter.BaseFragment;
import com.android.picshow.utils.LogPrinter;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.utils.PathHelper;
import com.android.picshow.utils.PicShowUtils;
import com.android.picshow.view.fragment.AlbumSetPageDelegate;
import com.bumptech.glide.load.DecodeFormat;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class AlbumSetPage extends BaseFragment<AlbumSetPageDelegate> {

    public static final String TAG = "AlbumSetPage";
    private static final int UPDATE = 0x111;

    private LoadListener myLoadListener;
    private AlbumSetDataLoader dataLoader;
    private Handler mainHandler;
    private GridAdapter gridAdapter;
    private int decodeBitmapWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(dataLoader != null)
            dataLoader.resume();*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(dataLoader != null)
            dataLoader.pause();*/
        GlideApp.get(getContext()).clearMemory();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startAlbumPage(gridAdapter.getItem(position).bucketID,
                        gridAdapter.getItem(position).bucketDisplayName);
            }

        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if(dataLoader != null)
                dataLoader.resume();
        } else {
            if(dataLoader != null)
                dataLoader.pause();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected Class<AlbumSetPageDelegate> getDelegateClass() {
        return AlbumSetPageDelegate.class;
    }

    private void init() {
        decodeBitmapWidth = PicShowUtils.getAlbumImageWidth(getContext());
        LogPrinter.i(TAG,"decodeBitmapWidth:" + decodeBitmapWidth);
        myLoadListener = new LoadListener() {
            @Override
            public void startLoad() {

            }

            @Override
            public void finishLoad(Object[] items) {
                Message msg = mainHandler.obtainMessage();
                msg.what = UPDATE;
                msg.obj = items;
                mainHandler.sendMessage(msg);
            }
        };

        dataLoader = new AlbumSetDataLoader(getActivity().getApplication(), myLoadListener);
        mainHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE:
                        if(gridAdapter != null) {
                            gridAdapter.setData((Album[]) msg.obj);
                        }
                        break;

                    default:

                        break;
                }
            }

        };

    }

    private void initView() {
        gridAdapter = new GridAdapter();
        gridAdapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }

        });

        viewDelegate.setGridViewAdapter(gridAdapter);
    }

    private void startAlbumPage(int bucket, String name) {
        ARouter.getInstance().build(PathHelper.PATH_ALBUM)
                .withInt(MediaSetUtils.BUCKET, bucket)
                .withString(MediaSetUtils.SET_NAME, name)
                .navigation();
        /*Intent intent = new Intent(getContext(),AlbumPage.class);
        intent.putExtra(MediaSetUtils.BUCKET,bucket);
        intent.putExtra(MediaSetUtils.SET_NAME, name);
        getContext().startActivity(intent);*/
    }

    private class GridAdapter extends BaseAdapter {

        private Album[] allItems = new Album[0];

        public GridAdapter() {}

        public void setData(Album[] items) {
            allItems = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return allItems.length;
        }

        @Override
        public Album getItem(int position) {
            return allItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v = null;
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.picshow_album_item,null);
                v = new ViewHolder();
                v.albumImage = convertView.findViewById(R.id.album_img);
                v.albumTitle = convertView.findViewById(R.id.album_name);
                v.albumCount = convertView.findViewById(R.id.album_count);
            } else {
                v = (ViewHolder)convertView.getTag();
            }
            if(v != null) {
                GlideApp.with(AlbumSetPage.this)
                        .load(allItems[position].absPath)
                        .override(decodeBitmapWidth,decodeBitmapWidth)
                        .placeholder(R.drawable.other)
                        .centerCrop()
                        .dontAnimate()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into(v.albumImage);
                v.albumTitle.setText(allItems[position].getLocalizedName());
                v.albumCount.setText(allItems[position].count + "");
                convertView.setTag(v);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView albumImage;
        public TextView albumTitle;
        public TextView albumCount;
    }

}
