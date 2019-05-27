package com.android.picshow.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.picshow.utils.LogPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PhotoItem implements Parcelable {

    private static final String TAG = "PhotoItem";

    public static final int TYPE_IMAGE = 0X001;
    public static final int TYPE_VIDEO = 0X002;
    public static final int TYPE_GIF = 0X010;

    private int ID;
    private String mTitle;
    private String mPath;
    private long dateToken;
    private long dateAdd;
    private int itemType;

    public PhotoItem() {}

    public PhotoItem(int id, String title, String path, long date, long add, int type) {
        ID = id;
        mTitle = title;
        mPath = path;
        dateToken = date;
        dateAdd = add;
        itemType = type;
    }

    protected PhotoItem(Parcel in) {
        ID = in.readInt();
        mTitle = in.readString();
        mPath = in.readString();
        dateToken = in.readLong();
        dateAdd = in.readLong();
        itemType = in.readInt();
    }

    public int getID() {
        return ID;
    }

    public String getPath() {
        return mPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }

    public void setDateAdd(long add ) {
        dateAdd = add;
    }

    public String getDateAdd(String pattern) {
        return new SimpleDateFormat(pattern)
                .format(new Date(dateAdd*1000L));
    }

    public Path toPath() {
        return new Path(ID, itemType);
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(mTitle);
        dest.writeString(mPath);
        dest.writeLong(dateToken);
        dest.writeLong(dateAdd);
        dest.writeInt(itemType);
    }

    @Override
    public String toString() {
        String s = "ID = "+ID + ", Title = " + mTitle
                + ", Path = " + mPath + ", type = " + itemType;
        LogPrinter.i(TAG,s);
        return s;
    }
}
