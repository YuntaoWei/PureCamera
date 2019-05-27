package com.pure.camera.common;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.picshow.app.PhotoActivity;
import com.android.picshow.utils.BucketHelper;
import com.android.picshow.utils.MediaSetUtils;
import com.pure.camera.CameraApp;
import com.pure.camera.bean.MediaFile;

public class ActivityStarter {

    public static void startToGallery(Uri data) {
        Context ctx = CameraApp.getGlobalContext();
        Intent gallery = new Intent(ctx, PhotoActivity.class);

        long id = ContentUris.parseId(data);
        gallery.putExtra(MediaSetUtils.PHOTO_ID, id);
        gallery.putExtra(MediaSetUtils.BUCKET, BucketHelper.getBucketID(MediaFile.DEFAUT_STORAGE_LOCATION));
        ctx.startActivity(gallery);
    }

}
