package com.android.picshow.app;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.android.picshow.R;
import com.android.picshow.adapter.TimeLineAdapter;
import com.android.picshow.model.GlideApp;
import com.android.picshow.model.LoadListener;
import com.android.picshow.model.Path;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.model.TimeLinePageDataLoader;
import com.android.picshow.presenter.BaseFragment;
import com.android.picshow.ui.MenuExecutor;
import com.android.picshow.ui.SelectionManager;
import com.android.picshow.utils.LogPrinter;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.utils.PicShowUtils;
import com.android.picshow.view.fragment.TimeLinePageDelegate;

import java.util.List;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class TimeLinePage extends BaseFragment<TimeLinePageDelegate> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, View.OnClickListener {

    private static final String TAG = "TimeLinePage";
    public static final int UPDATE = 0x111;


    private LoadListener myLoadListener;
    private TimeLinePageDataLoader dataLoader;
    private Handler mainHandler;

    private TimeLineAdapter gridAdapter;
    private int decodeBitmapWidth;
    private SelectionManager selectionManager;
    private SelectionManager.SelectionListener selectionListener;
    private MenuExecutor menuExecutor;
    MenuExecutor.ExcuteListener excuteListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dataLoader != null)
            dataLoader.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dataLoader != null)
            dataLoader.pause();
        GlideApp.get(getContext()).clearMemory();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogPrinter.i(TAG,"onViewCreated:" + view);
        initView();
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setGridViewItemOnLongClickListener(this);
        viewDelegate.setGridViewOnItemClickListener(this);
        viewDelegate.setOnClickListener(this, R.id.share, R.id.edit, R.id.delete, R.id.more);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gridAdapter != null)
            gridAdapter.destroy();
        gridAdapter = null;
    }

    @Override
    protected Class<TimeLinePageDelegate> getDelegateClass() {
        return TimeLinePageDelegate.class;
    }

    private void init() {
        decodeBitmapWidth = PicShowUtils.getImageWidth(getContext());
        LogPrinter.i(TAG,"decodeBitmapWidth:" + decodeBitmapWidth
                + "  density:" + getResources().getDisplayMetrics().density);
        myLoadListener = new LoadListener() {
            @Override
            public void startLoad() {
                LogPrinter.i(TAG,"startLoad");
            }

            @Override
            public void finishLoad(Object[] items) {
                LogPrinter.i(TAG,"finishLoad:" + gridAdapter);
                Message msg = mainHandler.obtainMessage();
                msg.what = UPDATE;
                msg.obj = items;
                mainHandler.sendMessage(msg);
            }
        };
        dataLoader = new TimeLinePageDataLoader(getActivity().getApplication(),myLoadListener);

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE:
                        if(gridAdapter != null) {
                            gridAdapter.setData((PhotoItem[]) msg.obj);
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
                viewDelegate.setBottomViewVisiblity(true);
            }

            @Override
            public void exitSelectionMode() {
                viewDelegate.setBottomViewVisiblity(false);
                gridAdapter.setSelectState(false);
            }

            @Override
            public void onSelectionChange(Path p, boolean select) {
                if(selectionManager.getSelectCount() > 1) {
                    viewDelegate.disableDetailAndEdit();
                } else {
                    viewDelegate.enableDetailAndEdit();
                }
            }
        };
    }

    private void initView() {
        selectionManager = new SelectionManager();
        gridAdapter = new TimeLineAdapter(getActivity(), null, selectionManager);
        gridAdapter.setDecodeSize(decodeBitmapWidth);
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

        selectionManager.setSelectionListener(selectionListener);
        menuExecutor = new MenuExecutor(getContext());
        excuteListener = new MenuExecutor.ExcuteListener() {

            @Override
            public void startExcute() {
                //show dialog here.
                viewDelegate.showProgreeDialog();
            }

            @Override
            public void excuteSuccess() {
                //exit select mode and hide dialog.
                gridAdapter.setSelectState(false);
                viewDelegate.dimissDialog();
                selectionManager.clearSelection();
            }

            @Override
            public void excuteFailed() {
                //some error occurs.
                viewDelegate.dimissDialog();
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(gridAdapter.getSelectState()) {
            PhotoItem item = gridAdapter.getItem(position);
            updateItem(view, selectionManager.togglePath(position, item.toPath()));
        } else {
            String path = gridAdapter.getItem(position).getPath();
            LogPrinter.i("test", "position : " + position + " path : " +
                    path + "   bucket id : " + MediaSetUtils.CAMERA_BUCKET_ID + "  " + "  " + id);
            Intent intent = new Intent(getActivity(), PhotoActivity.class);
            intent.putExtra(MediaSetUtils.PHOTO_ID, id);
            intent.putExtra(MediaSetUtils.PHOTO_PATH, path);
            intent.putExtra(MediaSetUtils.BUCKET, MediaSetUtils.CAMERA_BUCKET_ID);
            startActivity(intent);
        }
    }

    private void updateItem(View v, boolean select) {
        TimeLineAdapter.ViewHolder vh = (TimeLineAdapter.ViewHolder)v.getTag();
        if(vh != null) {
            vh.selectIcon.setChecked(select);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!gridAdapter.getSelectState()) {
            PhotoItem item = gridAdapter.getItem(position);
            updateItem(view, selectionManager.togglePath(position, item.toPath()));
            gridAdapter.setSelectState(true);
            viewDelegate.setBottomViewVisiblity(true);
        } else {
            gridAdapter.setSelectState(false);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.delete) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_DELETE,
                    selectionManager.getSelectItems(), excuteListener);

        } else if (i == R.id.more) {
            PhotoItem item = gridAdapter.getItem(selectionManager.getSelectPostion());
            String path = item.getPath();
            List<String> detail = PicShowUtils.getExifInfo(path);
            String title = item.getTitle();
            detail.add(0, "Title : /" + title);
            detail.add("Path : /" + path);
            PicShowUtils.showDetailDialog(getActivity(), detail);

        } else if (i == R.id.edit) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_EDIT,
                    selectionManager.getSelectItems(), excuteListener);

        } else if (i == R.id.share) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_SHARE,
                    selectionManager.getSelectItems(), excuteListener);

        }
    }

    public boolean onBackPressed() {
        if(gridAdapter.getSelectState()) {
            selectionManager.clearSelection();
            gridAdapter.setSelectState(false);
            return true;
        }
        return false;
    }

}
