package com.pure.camera.data;

import android.content.ContentValues;
import android.provider.MediaStore;

public class PhotoFile extends MediaFile {

    public static final String PHOTO_REX = ".jpg";

    public PhotoFile(byte[] fileData, int fileWidth, int fileHeight, int fileOrientation) {
        super(fileData, fileWidth, fileHeight, fileOrientation,
                MIME_TYPE_PHOTO,
                FILE_TYPE_IMG, gerateFileName(FILE_TYPE_IMG),
                PHOTO_REX);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = super.toContentValues();
        cv.put(MediaStore.Images.ImageColumns.WIDTH, fileWidth);
        cv.put(MediaStore.Images.ImageColumns.HEIGHT, fileHeight);
        return cv;
    }
}
