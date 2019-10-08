package com.android.picshow.model;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class Album {

    public int bucketID;// folder bucket id
    public String bucketDisplayName;//album/folder name
    public long dateToken;
    public String absPath;
    public int count;

    public Album() {
    }

    public Album(int id, String name, long date, String path) {
        this(id, name, date, path, 0);
    }

    public Album(int id, String name, long date, String path, int count) {
        bucketID = id;
        bucketDisplayName = name;
        dateToken = date;
        absPath = path;
        this.count = count;
    }

    @Override
    public String toString() {
        return "id : " + bucketID + ", name : " + bucketDisplayName
                + ", date : " + dateToken + ", path = " + absPath + ", count : " + count;
    }

    public void addCount(int add) {
        count += add;
    }

    public String getLocalizedName() {
        return bucketDisplayName;
    }

}
