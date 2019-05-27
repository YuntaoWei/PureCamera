package com.android.picshow.model;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;

import com.android.picshow.app.PictureShowApplication;
import com.android.picshow.utils.MediaSetUtils;

import java.util.concurrent.Semaphore;

/**
 * Created by yuntao.wei on 2017/12/9.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PhotoDataLoader implements DataLoader {

    public static final int INVALID = -1;
    private Semaphore semaphore;
    private LoadThread loadTask;
    private int bucketID;
    private PhotoLoadListener loadCallBack;
    private Application mApp;
    private ChangeNotify notifier;

    public interface PhotoLoadListener {

        public void startLoad();
        public void loadFinish(Cursor cursor);
    }

    public PhotoDataLoader(Application app, int bucket, PhotoLoadListener listener) {
        mApp = app;
        bucketID = bucket;
        semaphore = new Semaphore(1);
        loadCallBack = listener;
    }

    public void resume() {
        if(null == notifier) {
            notifier = new ChangeNotify(this, new Uri[]{
                    MediaSetUtils.VIDEO_URI,
                    MediaSetUtils.IMAGE_URI
            }, mApp);
        } else {
            DataManager.getDataManager(mApp).registerObServer(MediaSetUtils.VIDEO_URI, notifier);
            DataManager.getDataManager(mApp).registerObServer(MediaSetUtils.IMAGE_URI, notifier);
        }

        if(semaphore == null)
            semaphore = new Semaphore(1);
        if(loadTask == null)
            loadTask = new LoadThread();
        loadTask.start();
        semaphore.release();
    }

    public void pause() {
        if(loadTask != null)
            loadTask.stopTask();
        loadTask = null;
        if(semaphore != null)
            semaphore.release();
        semaphore = null;

        DataManager.getDataManager(mApp).releaseAllObserver();
    }

    private void reloadData() {
        if(semaphore != null)
            semaphore.release();
    }

    @Override
    public void notifyContentChanged() {
        reloadData();
    }

    private class LoadThread extends Thread {

        private boolean stop = false;

        public LoadThread() {}

        public void stopTask() {
            stop = true;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if(semaphore == null)
                        continue;
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(stop)
                    return;

                if(!notifier.isDirty())
                    continue;

                Cursor c = MediaSetUtils.queryAllItemByBucketID(mApp.getContentResolver(), bucketID);
                loadCallBack.loadFinish(c);
            }
        }
    }

}
