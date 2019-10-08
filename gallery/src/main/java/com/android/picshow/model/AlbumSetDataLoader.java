package com.android.picshow.model;

import android.app.Application;
import android.net.Uri;

import com.android.picshow.utils.MediaSetUtils;
import com.pure.commonbase.BucketHelper;
import com.pure.commonbase.LogPrinter;

import java.util.concurrent.Semaphore;

/**
 * Created by yuntao.wei on 2017/12/6.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class AlbumSetDataLoader implements DataLoader {

    private static final String TAG = "AlbumSetDataLoader";

    private Application mContext;
    private LoadListener mListener;
    private Semaphore mSemaphore;
    private LoadThread loadTask;
    private ChangeNotify notifier;


    public AlbumSetDataLoader(Application context, LoadListener l) {
        mContext = context;
        mListener = l;
        notifier = new ChangeNotify(this, new Uri[]{
                MediaSetUtils.VIDEO_URI,
                MediaSetUtils.IMAGE_URI
        }, mContext);
    }


    public void resume() {
        LogPrinter.i(TAG, "resume");
        if (loadTask == null) {
            loadTask = new LoadThread();
        }
        loadTask.start();

        if (mSemaphore == null) {
            mSemaphore = new Semaphore(1);
        }
        mSemaphore.release();
    }

    public void pause() {
        LogPrinter.i(TAG, "pause");
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
                Album[] allAlbum = MediaSetUtils.getAllAlbum(mContext);
                LogPrinter.i(TAG, "LoadThread load complete:" + allAlbum.length);
                //PicShowUtils.sortItem(allAlbum,true);
                reSort(allAlbum);
                mListener.finishLoad(allAlbum);
                if (mSemaphore != null)
                    mSemaphore.release();
            }

        }

        private Album[] reSort(Album[] data) {
            Album tmp;
            int sortIndex = 0;

            int index = findIndexFromBucket(data, BucketHelper.LOCAL_CAMERA_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has camera album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.PICTURE_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has picture album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.SCREEN_SHOT_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has screen shot album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.TENCENT_WEIXIN_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has wechat album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.TENCENT_QQ_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has qq album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.TENCENT_NEWS_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has tencent news album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.SINA_WEIBO_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has sina weblog album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }

            index = findIndexFromBucket(data, BucketHelper.NEW_ARTICALS_BUCKET);
            if (index != -1) {
                LogPrinter.i("wytt", "has new articals album");
                tmp = data[index];
                data[index] = data[sortIndex];
                data[sortIndex] = tmp;
                sortIndex++;
            }
            return data;
        }

    }

    private int findIndexFromBucket(Album[] data, int bucket) {
        for (int i = 0; i < data.length; i++) {
            if (data[i].bucketID == bucket)
                return i;
        }
        return -1;
    }
}
