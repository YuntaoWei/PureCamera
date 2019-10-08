package com.pure.commonbase;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaSetData {
    public static final int CAMERA_BUCKET_ID
            = BucketHelper.getBucketID(Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/DCIM/Camera");

    public static final String BUCKET = "bucket";
    public static final String SET_NAME = "setname";
    public static final String PHOTO_ID = "_id";
    public static final String PHOTO_PATH = "_path";

    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
}
