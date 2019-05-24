package com.pure.camera.task;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Size;

import com.pure.camera.CameraApp;
import com.pure.camera.async.ThreadPool;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UpdateThumbnail implements ThreadPool.Job {
    private Uri uri;
    private Size pictureSize;
    private ContentResolver contentResolver;

    public UpdateThumbnail(Uri u, Size size) {
        uri = u;
        pictureSize = size;
        contentResolver = CameraApp.getGlobalContext().getContentResolver();
    }

    public void update(Uri u) {
        uri = u;
    }

    public void update(Uri u, Size size) {
        uri = u;
        pictureSize = size;
    }

    private boolean isImage() {
        if (null == uri) {
            throw new RuntimeException("Uri is null.");
        }

        String path = uri.getPath();
        if (path.contains("images"))
            return true;

        return false;
    }

    private Bitmap decodePhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream ips = contentResolver.openInputStream(uri)) {
            ByteArrayOutputStream bops = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 512];//512K
            int len = 0;
            while ((len = ips.read(buffer)) > 0) {
                bops.write(buffer, 0, len);
            }

            buffer = null;
            byte[] picData = bops.toByteArray();

            BitmapFactory.decodeByteArray(picData, 0, picData.length, options);
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            int scaleX = originalWidth / pictureSize.getWidth();
            int scaleY = originalHeight / pictureSize.getHeight();
            options.inSampleSize = scaleX > scaleY ? scaleY : scaleX;
            options.inJustDecodeBounds = false;
            options.inScaled = true;
            Bitmap bm = BitmapFactory.decodeByteArray(picData, 0, picData.length, options);
            return bm;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap decodeVideo() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Cursor c = contentResolver.query(uri, new String[]{MediaStore.Video.Media.DATA},
                null, null, null);

        if (null != c && c.moveToNext()) {
            String path = c.getString(0);
            retriever.setDataSource(path);
            c.close();
            c = null;
        }

        return retriever.getFrameAtTime();

    }


    @Override
    public Bitmap run(ThreadPool.JobContext jc) {
        if (jc.isCancelled())
            return null;

        Bitmap bm = null;
        if (isImage()) {
            bm = decodePhoto();
        } else {
            bm = decodeVideo();
        }

        if (jc.isCancelled()) {
            return null;
        }
        return bm;
    }
}
