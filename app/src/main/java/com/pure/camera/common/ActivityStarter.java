package com.pure.camera.common;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.picshow.app.PhotoActivity;
import com.pure.camera.CameraApp;
import com.pure.camera.bean.MediaFile;
import com.pure.commonbase.BucketHelper;
import com.pure.commonbase.MediaSetData;

public class ActivityStarter {

    public static void startToGallery(Uri data) {
        Context ctx = CameraApp.getGlobalContext();
        Intent gallery = new Intent(ctx, PhotoActivity.class);

        long id = ContentUris.parseId(data);
        gallery.putExtra(MediaSetData.PHOTO_ID, id);
        gallery.putExtra(MediaSetData.BUCKET, BucketHelper.getBucketID(MediaFile.DEFAUT_STORAGE_LOCATION));
        ctx.startActivity(gallery);

        ARouter.getInstance().build("/gallery/photo")
                .withLong(MediaSetData.PHOTO_ID, id)
                .withInt(MediaSetData.BUCKET, BucketHelper.getBucketID(MediaFile.DEFAUT_STORAGE_LOCATION))
                .navigation();
    }
}
