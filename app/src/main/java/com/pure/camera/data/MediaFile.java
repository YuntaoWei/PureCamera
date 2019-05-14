package com.pure.camera.data;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MediaFile {

    public static final String MIME_TYPE_PHOTO = "image/jpg";
    public static final String MIME_TYPE_VIDEO = "video/mp4";
    public static final int FILE_TYPE_IMG = 1;
    public static final int FILE_TYPE_VIDEO = 2;
    public static final String DIR_NAME = "PureCamera";
    public static final String DEFAUT_STORAGE_LOCATION =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() +
                    File.separator + DIR_NAME;

    public static final Uri URI_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri URI_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public static final String FILE_HEADER = "DCIM_CAMERA_";
    public static final String DATE_FORMATOR = "yyyy_MM_dd_HH_mm_ss";
    public static final SimpleDateFormat FORMATOR = new SimpleDateFormat(DATE_FORMATOR);

    protected byte[] fileData;
    protected int fileWidth;
    protected int fileHeight;
    protected int fileOrientation;
    protected String fileMimeType;
    protected String fileTitle;
    protected String fileDisplayName;
    protected String filePath;
    protected int fileType;

    public MediaFile() {}

    public MediaFile(byte[] fileData, int fileWidth, int fileHeight, int fileOrientation,
                     String mimeType, int fileType, String title, String rex) {
        this.fileData = fileData;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.fileOrientation = fileOrientation;
        this.fileMimeType = mimeType;
        this.fileType = fileType;
        this.fileTitle = title;
        this.fileDisplayName = title + rex;
        this.filePath = DEFAUT_STORAGE_LOCATION + "/" + fileDisplayName;
    }

    public MediaFile(byte[] fileData, int fileWidth, int fileHeight, int fileOrientation,
                     String mimeType, String title, String displayName, String filePath) {
        this.fileData = fileData;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.fileOrientation = fileOrientation;
        this.fileMimeType = mimeType;
        this.fileTitle = title;
        this.fileDisplayName = displayName;
        this.filePath = filePath;
    }

    public static String getMimeTypePhoto() {
        return MIME_TYPE_PHOTO;
    }

    public static String getMimeTypeVideo() {
        return MIME_TYPE_VIDEO;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public int getFileWidth() {
        return fileWidth;
    }

    public void setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public void setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
    }

    public int getFileOrientation() {
        return fileOrientation;
    }

    public void setFileOrientation(int fileOrientation) {
        this.fileOrientation = fileOrientation;
    }

    public String getMimeType() {
        return fileMimeType;
    }

    public void setMimeType(String mimeType) {
        this.fileMimeType = mimeType;
    }

    public String getTitle() {
        return fileTitle;
    }

    public void setTitle(String title) {
        this.fileTitle = title;
    }

    public String getDisplayName() {
        return fileDisplayName;
    }

    public void setDisplayName(String displayName) {
        this.fileDisplayName = displayName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Files.FileColumns.DATA, filePath);
        cv.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileDisplayName);
        cv.put(MediaStore.Files.FileColumns.TITLE, fileDisplayName);
        cv.put(MediaStore.Files.FileColumns.MIME_TYPE, fileMimeType);
        return cv;
    }

    public static String gerateFileName(int fileType) {
        Date now = new Date();
        return FILE_HEADER + (fileType == FILE_TYPE_IMG ? "IMG" : "VIDEO") + FORMATOR.format(now);
    }

    public Uri getUri() {
        return fileType == FILE_TYPE_IMG ? URI_IMAGE : URI_VIDEO;
    }
}
