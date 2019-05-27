package com.android.picshow.utils;

import android.os.Environment;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class BucketHelper {

    public static final int TENCENT_QQ_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Tencent/QQ_Images");
    public static final int TENCENT_WEIXIN_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Tencent/MicroMsg/WeiXin");
    public static final int PICTURE_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Pictures");
    public static final int SCREEN_SHOT_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Pictures/Screenshots");
    public static final int TENCENT_NEWS_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Pictures/TencentNews");
    public static final int SINA_WEIBO_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/sina/weibo/weibo");
    public static final int NEW_ARTICALS_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/news_article");
    public static final int LOCAL_CAMERA_BUCKET = getBucketID(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/DCIM/Camera");


    public static int getBucketID(String path) {
        return path.toLowerCase().hashCode();
    }

}
