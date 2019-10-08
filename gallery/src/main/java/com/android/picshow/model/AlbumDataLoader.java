package com.android.picshow.model;

import android.app.Application;
import android.net.Uri;

import com.android.picshow.utils.MediaSetUtils;
import com.pure.commonbase.LogPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

/**
 * Created by yuntao.wei on 2017/12/8.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class AlbumDataLoader implements DataLoader {

    private static final String TAG = "AlbumDataLoader";
    private Application mContext;
    private LoadListener mListener;
    private Semaphore mSemaphore;
    private LoadThread loadTask;
    private int bucketID;
    private ChangeNotify notifier;


    public AlbumDataLoader(Application context, LoadListener l, int bucket) {
        mContext = context;
        mListener = l;
        bucketID = bucket;
        notifier = new ChangeNotify(this, new Uri[]{
                MediaSetUtils.VIDEO_URI,
                MediaSetUtils.IMAGE_URI
        }, mContext);
    }


    public void resume() {
        if (loadTask == null) {
            loadTask = new LoadThread();
        }
        loadTask.start();

        if (mSemaphore == null) {
            mSemaphore = new Semaphore(0);
        }
        mSemaphore.release();
    }

    public void pause() {
        if (loadTask != null) {
            loadTask.stopTask();
            loadTask = null;
        }
        if (mSemaphore != null) {
            mSemaphore.release();
            mSemaphore = null;
        }
    }

    private void reloadData() {
        if (mSemaphore != null)
            mSemaphore.release();
    }

    @Override
    public void notifyContentChanged() {
        reloadData();
    }

    private class LoadThread extends Thread {

        private boolean stopTask = false;

        public LoadThread() {
        }

        public void stopTask() {
            stopTask = true;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (mSemaphore == null)
                        continue;
                    mSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (stopTask) {
                    return;
                }
                if (!notifier.isDirty())
                    continue;
                mListener.startLoad();
                ArrayList<PhotoItem> items = new ArrayList<>();
                MediaSetUtils.queryImages(mContext, items, bucketID);
                MediaSetUtils.queryVideo(mContext, items, bucketID);
                LogPrinter.i(TAG, "LoadThread load complete:" + items.size());
                PhotoItem[] allItem = new PhotoItem[items.size()];
                items.toArray(allItem);
                Arrays.sort(allItem, new Comparator<PhotoItem>() {

                    @Override
                    public int compare(PhotoItem o1, PhotoItem o2) {
                        return (int) (o2.getDateToken() - o1.getDateToken());
                    }
                });
                mListener.finishLoad(allItem);
            }
        }
    }

}
