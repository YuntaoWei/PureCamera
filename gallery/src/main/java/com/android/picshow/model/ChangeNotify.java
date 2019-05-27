package com.android.picshow.model;

import android.net.Uri;

import com.android.picshow.app.PictureShowApplication;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yuntao.wei on 2017/12/12.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class ChangeNotify {
    private DataLoader mLoader;
    private AtomicBoolean mContentDirty = new AtomicBoolean(true);

    public ChangeNotify(DataLoader set, Uri uri, PictureShowApplication app) {
        mLoader = set;
        app.getDataManager().registerObServer(uri, this);
    }

    public ChangeNotify(DataLoader set, Uri[] uris, PictureShowApplication app) {
        mLoader = set;
        for (Uri u:uris
             ) {
            app.getDataManager().registerObServer(u, this);
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
