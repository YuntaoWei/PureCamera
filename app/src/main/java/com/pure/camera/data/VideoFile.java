package com.pure.camera.data;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;

public class VideoFile extends MediaFile {

    public static final String VIDEO_REX = ".mp4";

    public VideoFile(int fileWidth, int fileHeight, int fileOrientation) {
        super(null, fileWidth, fileHeight, fileOrientation,
                MIME_TYPE_VIDEO,
                FILE_TYPE_VIDEO, FILE_HEADER + gerateFileName(FILE_TYPE_VIDEO),
                VIDEO_REX);
    }

    public static VideoFile createVideoFile(int width, int height) {
        return new VideoFile(width, height, 0);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = super.toContentValues();
        cv.put(MediaStore.Video.VideoColumns.WIDTH, fileWidth);
        cv.put(MediaStore.Video.VideoColumns.HEIGHT, fileHeight);
        return cv;
    }

}
