package com.android.picshow.app;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.android.picshow.R;
import com.android.picshow.adapter.TimeLineAdapter;
import com.android.picshow.model.AlbumDataLoader;
import com.android.picshow.model.GlideApp;
import com.android.picshow.model.LoadListener;
import com.android.picshow.model.Path;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.presenter.BaseActivity;
import com.android.picshow.ui.MenuExecutor;
import com.android.picshow.ui.SelectionManager;
import com.android.picshow.utils.LogPrinter;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.utils.PicShowUtils;
import com.android.picshow.view.activity.AlbumPageDelegate;

import java.util.List;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class AlbumPage extends BaseActivity<AlbumPageDelegate> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String TAG = "AlbumPage";

    private static final int UPDATE = 0x111;

    private TimeLineAdapter mAdapter;
    private int decodeBitmapWidth;
    private LoadListener myLoadListener;
    private Handler mainHandler;
    private AlbumDataLoader albumDataLoader;
    private int bucketID;
    private Toolbar mToolbar;
    private SelectionManager selectionManager;
    private SelectionManager.SelectionListener selectionListener;
    private MenuExecutor menuExecutor;
    private MenuExecutor.ExcuteListener excuteListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(albumDataLoader != null)
            albumDataLoader.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(albumDataLoader != null)
            albumDataLoader.pause();
        GlideApp.get(getApplicationContext()).clearMemory();
    }

    private void init() {
        decodeBitmapWidth = PicShowUtils.getImageWidth(getApplicationContext());
        myLoadListener = new LoadListener() {
            @Override
            public void startLoad() {
                LogPrinter.i(TAG,"startLoad");
            }

            @Override
            public void finishLoad(Object[] items) {
                LogPrinter.i(TAG,"finishLoad:" + mAdapter);
                Message msg = mainHandler.obtainMessage();
                msg.what = UPDATE;
                msg.obj = items;
                mainHandler.sendMessage(msg);
            }
        };

        bucketID = getIntent().getIntExtra(MediaSetUtils.BUCKET, MediaSetUtils.CAMERA_BUCKET_ID);

        albumDataLoader = new AlbumDataLoader(getApplication(), myLoadListener, bucketID);

        mainHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE:
                        if(mAdapter != null) {
                            mAdapter.setData((PhotoItem[]) msg.obj);
                        }
                        break;

                    default:

                        break;
                }
            }

        };

        selectionListener = new SelectionManager.SelectionListener() {
            @Override
            public void enterSelectionMode() {

            }

            @Override
            public void exitSelectionMode() {
                LogPrinter.i("ttt", "exitSelectionMode");
                mAdapter.setSelectState(false);
                viewDelegate.setBottomViewVisibility(false);
            }

            @Override
            public void onSelectionChange(Path p, boolean select) {
                if(selectionManager.getSelectCount() > 1) {
                    viewDelegate.disableEditAndMore();
                } else {
                    viewDelegate.enableEditAndMore();
                }
            }
        };
        selectionManager.setSelectionListener(selectionListener);

    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setToolBarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getSelectState()) {
                    selectionManager.clearSelection();
                } else
                    finish();
            }
        });
        viewDelegate.setGridViewClickListener(this, this);

        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.delete) {
                    menuExecutor.execute(MenuExecutor.MENU_ACTION_DELETE,
                            selectionManager.getSelectItems(), excuteListener);

                } else if (i == R.id.more) {
                    PhotoItem item = mAdapter.getItem(selectionManager.getSelectPostion());
                    String path = item.getPath();
                    List<String> detail = PicShowUtils.getExifInfo(path);
                    String title = item.getTitle();
                    detail.add(0, "Title : /" + title);
                    detail.add("Path : /" + path);
                    PicShowUtils.showDetailDialog(AlbumPage.this, detail);

                } else if (i == R.id.edit) {
                    menuExecutor.execute(MenuExecutor.MENU_ACTION_EDIT,
                            selectionManager.getSelectItems(), excuteListener);

                } else if (i == R.id.share) {
                    menuExecutor.execute(MenuExecutor.MENU_ACTION_SHARE,
                            selectionManager.getSelectItems(), excuteListener);

                }
            }
        };

        viewDelegate.setOnClickListener(onclick, R.id.delete, R.id.share, R.id.edit, R.id.more);
    }

    @Override
    protected void initView() {
        icBlack = true;
        mAdapter = new TimeLineAdapter(this);
        mAdapter.setDecodeSize(decodeBitmapWidth);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        selectionManager = new SelectionManager();
        menuExecutor = new MenuExecutor(AlbumPage.this);
        excuteListener = new MenuExecutor.ExcuteListener() {
            @Override
            public void startExcute() {
                //show dialog here.
                viewDelegate.showProgreeDialog();
            }

            @Override
            public void excuteSuccess() {
                //exit select mode and hide dialog.
                mAdapter.setSelectState(false);
                viewDelegate.dimissDialog();
                selectionManager.clearSelection();
            }

            @Override
            public void excuteFailed() {
                //some error occurs.
                viewDelegate.dimissDialog();
            }
        };

        super.initView();
        viewDelegate.setGridViewAdapter(mAdapter);
        viewDelegate.setTitle(getIntent().getStringExtra(MediaSetUtils.SET_NAME));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null)
            mAdapter.destroy();
        mAdapter = null;
    }

    @Override
    protected Class getDelegateClass() {
        return AlbumPageDelegate.class;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mAdapter.getSelectState()) {
            PhotoItem item = mAdapter.getItem(position);
            viewDelegate.updateItem(view, selectionManager.togglePath(position, item.toPath()));
        } else {
            PhotoItem item = mAdapter.getItem(position);
            goToPhotoPage(item, position);
        }
    }

    private void goToPhotoPage(PhotoItem item, int position) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(MediaSetUtils.PHOTO_ID, position);
        intent.putExtra(MediaSetUtils.PHOTO_PATH, item.getPath());
        intent.putExtra(MediaSetUtils.BUCKET, bucketID);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!mAdapter.getSelectState()) {
            mAdapter.setSelectState(true);
            viewDelegate.setBottomViewVisibility(true);
        } else {
            mAdapter.setSelectState(false);
            viewDelegate.setBottomViewVisibility(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        LogPrinter.i("ttt", "onBackPressed : " + mAdapter.getSelectState());
        if(mAdapter.getSelectState()) {
            selectionManager.clearSelection();
        } else
            finish();
    }
}
