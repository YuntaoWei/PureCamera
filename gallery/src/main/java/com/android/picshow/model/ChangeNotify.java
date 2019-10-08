package com.android.picshow.model;

import android.app.Application;
import android.net.Uri;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yuntao.wei on 2017/12/12.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class ChangeNotify {
    private DataLoader mLoader;
    private AtomicBoolean mContentDirty = new AtomicBoolean(true);

    public ChangeNotify(DataLoader set, Uri uri, Application app) {
        mLoader = set;
        DataManager.getDataManager(app).registerObServer(uri, this);
    }

    public ChangeNotify(DataLoader set, Uri[] uris, Application app) {
        mLoader = set;
        for (Uri u : uris
        ) {
            DataManager.getDataManager(app).registerObServer(u, this);
        }
    }

    // Returns the dirty flag and clear it.
    public boolean isDirty() {
        return mContentDirty.compareAndSet(true, false);
    }

    public void fakeChange() {
        onChange(false);
    }

    protected void onChange(boolean selfChange) {
        if (mContentDirty.compareAndSet(false, true)) {
            mLoader.notifyContentChanged();
        }
    }
}
