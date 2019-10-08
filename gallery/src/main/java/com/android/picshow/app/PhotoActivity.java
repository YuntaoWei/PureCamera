package com.android.picshow.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.android.picshow.R;
import com.android.picshow.adapter.PhotoPageAdapter;
import com.android.picshow.model.GlideApp;
import com.android.picshow.model.PhotoDataLoader;
import com.android.picshow.model.SimpleMediaItem;
import com.android.picshow.presenter.BaseActivity;
import com.android.picshow.ui.MenuExecutor;
import com.android.picshow.ui.PicPopupWindow;
import com.android.picshow.utils.LogPrinter;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.utils.PathHelper;
import com.android.picshow.utils.PhotoPageUtils;
import com.android.picshow.utils.PicShowUtils;
import com.android.picshow.utils.QuickSortUtil;
import com.android.picshow.view.activity.PhotoActivityDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuntao.wei on 2017/12/9.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

@Route(path = PathHelper.PATH_PHOTO)
public class PhotoActivity extends BaseActivity<PhotoActivityDelegate> implements PhotoDataLoader.PhotoLoadListener, View.OnClickListener {

    private static final String TAG = "PhotoActivity";

    private long currentID;
    private int currentIndex;
    private int bucketID;
    private PhotoPageAdapter photoPageAdapter;
    private PhotoDataLoader mLoader;
    private Handler mainHandler;
    private final static int UPDATE = 0x111;
    private final static int ENTER_FULL_SCREEN = 0x112;
    private final static int EXIT_FULL_SCREEN = 0x113;
    private MenuExecutor menuExecutor;
    private LayoutInflater inflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PicShowUtils.extendLayoutToFulScreen(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        currentID = intent.getLongExtra(MediaSetUtils.PHOTO_ID, PhotoDataLoader.INVALID);
        bucketID = intent.getIntExtra(MediaSetUtils.BUCKET, MediaSetUtils.CAMERA_BUCKET_ID);
        mLoader = new PhotoDataLoader(getApplication(), bucketID, this);
        mainHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE:
                        viewDelegate.setPagerAdapter(photoPageAdapter);
                        viewDelegate.switchPage(currentIndex);
                        break;

                    case ENTER_FULL_SCREEN:
                        PicShowUtils.enterFullScreen(viewDelegate.getToolbar());
                        break;

                    case EXIT_FULL_SCREEN:
                        PicShowUtils.exitFullScreen(viewDelegate.getToolbar());
                        break;
                }
            }

        };
        menuExecutor = new MenuExecutor(this);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.share, R.id.edit, R.id.delete, R.id.more);
        viewDelegate.setToolbarNavigationClickListener(this, R.id.photo_toolbar);
        viewDelegate.setOnFragmentPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentID = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        }, R.id.photo_pager);
    }

    public void toggleFullScreen() {
        viewDelegate.toggleFullScreen();
    }

    private void showMoreMenu() {
        viewDelegate.showMoreMenu(new PicPopupWindow.PicPopupWindowListener() {
            @Override
            public void onClick(View v) {
                String itemName = (String) v.getTag();
                if (itemName == null)
                    return;

                if (photoPageAdapter == null) return;
                Cursor c = (Cursor) photoPageAdapter.getDataItem(currentIndex);
                ArrayList<SimpleMediaItem> items = new ArrayList<>();
                items.add(PhotoPageUtils.getUriFromCursor(c));

                Resources res = getResources();
                if (itemName.equals(res.getString(R.string.rename))) {
                    //rename
                    menuExecutor.execute(MenuExecutor.MENU_ACTION_RENAME, items, null);
                } else if (itemName.equals(res.getString(R.string.set_as))) {
                    //set as
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.setDataAndType(items.get(0).itemUrl, items.get(0).itemType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(
                            intent, getString(R.string.set_as)));
                } else if (itemName.equals(res.getString(R.string.detail))) {
                    //detail
                    String path = c.getString(1);
                    List<String> detail = PicShowUtils.getExifInfo(path);
                    String title = c.getString(3);
                    detail.add(0, "Title : /" + title);
                    detail.add("Path : /" + path);
                    checkInflater();
                    PicShowUtils.showDetailDialog(PhotoActivity.this, detail);
                }
            }
        });
    }

    private void checkInflater() {
        if (inflater == null)
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoader != null)
            mLoader.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoader != null)
            mLoader.pause();
        GlideApp.get(getApplicationContext()).clearMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewDelegate.exitFullScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cursor c = photoPageAdapter.getCursor();
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }

    @Override
    protected Class<PhotoActivityDelegate> getDelegateClass() {
        return PhotoActivityDelegate.class;
    }

    @Override
    public void startLoad() {
    }

    @Override
    public void loadFinish(Cursor cursor) {
        currentIndex = QuickSortUtil.quickGetPosition(cursor, currentID);
        LogPrinter.i(TAG, "loadFinish:" + cursor.getCount() + "   " + currentIndex + "   " + currentID);
        photoPageAdapter = new PhotoPageAdapter(getApplicationContext(), getSupportFragmentManager(), cursor);
        mainHandler.sendEmptyMessage(UPDATE);
    }

    @Override
    public void onClick(View v) {
        if (photoPageAdapter == null) return;

        Cursor c = (Cursor) photoPageAdapter.getDataItem(currentIndex);
        ArrayList<SimpleMediaItem> list = new ArrayList<>();
        list.add(PhotoPageUtils.getUriFromCursor(c));

        int i = v.getId();
        if (i == -1) {
            finish();

        } else if (i == R.id.share) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_SHARE, list, null);

        } else if (i == R.id.edit) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_EDIT, list, null);

        } else if (i == R.id.delete) {
            menuExecutor.execute(MenuExecutor.MENU_ACTION_DELETE, list, null);

        } else if (i == R.id.more) {
            showMoreMenu();

        } else if (i == R.id.photo_toolbar) {
            finish();

        }
    }
}
