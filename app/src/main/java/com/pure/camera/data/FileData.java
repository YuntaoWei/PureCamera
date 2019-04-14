package com.pure.camera.data;

import android.content.ContentValues;
import android.text.TextUtils;

public class FileData {

    public static final String MIME_TYPE_PHOTO = "image/jpg";
    public static final String MIME_TYPE_VIDEO = "video/mp4";

    protected byte[] fileData;
    protected int fileWidth;
    protected int fileHeight;
    protected int fileOrientation;
    protected int fileSize;
    protected String fileMimeType;
    protected String fileTitle;
    protected String fileDisplayName;
    protected String filePath;

    public FileData() {}

    public FileData(byte[] fileData, int fileWidth, int fileHeight, int fileOrientation,
                    int fileSize, String mimeType, String title, String displayName, String filePath) {
        this.fileData = fileData;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.fileOrientation = fileOrientation;
        this.fileSize = fileSize;
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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
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

        return cv;
    }

}
