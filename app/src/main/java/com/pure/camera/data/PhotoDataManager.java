package com.pure.camera.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.pure.camera.CameraApp;
import com.pure.camera.async.Future;
import com.pure.camera.async.FutureListener;
import com.pure.camera.async.ThreadPool;
import com.pure.camera.bean.MediaFile;
import com.pure.camera.common.LogPrinter;

/**
 * 用来组织camera拍摄的数据，按照最近拍摄顺序组织排列
 */
public class PhotoDataManager {

    private static PhotoDataManager INSTANCE;
    private static final String LOOKAT_PATH = MediaFile.DEFAUT_STORAGE_LOCATION;
    private static final Uri LOOKAT_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final Uri LOOKAT_VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    private Handler mHandler;
    private DataChangeNotifier dataChangeNotifier;

    private PhotoDataManager() {
        mHandler = new Handler();
        dataChangeNotifier = new DataChangeNotifier(mHandler);
    }

    public static PhotoDataManager getInstance() {
        if(null == INSTANCE) {
            synchronized (PhotoDataManager.class) {
                INSTANCE = new PhotoDataManager();
            }
        }
        return INSTANCE;
    }

    public void resume(DataChangeListener l) {
        ContentResolver cr = CameraApp.getGlobalContext().getContentResolver();
        cr.registerContentObserver(LOOKAT_IMAGE_URI, true, dataChangeNotifier);
        cr.registerContentObserver(LOOKAT_VIDEO_URI, true, dataChangeNotifier);
        dataChangeNotifier.setDataChangeListener(l);

        reload();
    }

    public void pause() {
        ContentResolver cr = CameraApp.getGlobalContext().getContentResolver();
        cr.unregisterContentObserver(dataChangeNotifier);
        dataChangeNotifier.removeDataChangeListener();
    }

    private void reload() {
        ThreadPool.getDefaultPool().submit(new ThreadPool.Job<Uri>() {
            @Override
            public Uri run(ThreadPool.JobContext jc) {
                ContentResolver cr = CameraApp.getGlobalContext().getContentResolver();
                String[] projection = new String[] {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN};
                String selection = MediaStore.Images.Media.BUCKET_ID + " = " + MediaFile.BUCKET_ID;
                Cursor image = cr.query(MediaFile.URI_IMAGE, projection, selection, null,
                        MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT 1");
                Cursor video = cr.query(MediaFile.URI_VIDEO, projection, selection, null,
                        MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT 1");
                long imageTime = image.moveToNext() ? image.getLong(1) : -1;
                long videoTime = video.moveToNext() ? video.getLong(1) : -1;
                if(imageTime == -1 && videoTime == -1)
                    return null;

                boolean latestImage = imageTime > videoTime;
                int id = latestImage ? image.getInt(0) : video.getInt(0);

                image.close();
                video.close();

                return ContentUris.withAppendedId(latestImage ? MediaFile.URI_IMAGE : MediaFile.URI_VIDEO, id);
            }
        }, new FutureListener<Uri>() {
            @Override
            public void onFutureDone(Future<Uri> future) {
                Uri u = future.get();
                if(u == null)
                    return;
                dataChangeNotifier.onChange(false, u);
            }
        });
    }

}
