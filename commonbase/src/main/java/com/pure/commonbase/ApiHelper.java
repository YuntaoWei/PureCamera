package com.pure.commonbase;

import android.os.Build;
import android.view.View;

/**
 * Created by yuntao.wei on 2017/12/14.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class ApiHelper {

    public static final boolean HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE =
            hasField(View.class, "SYSTEM_UI_FLAG_LAYOUT_STABLE");

    public static final boolean HAS_MEDIA_PROVIDER_FILES_TABLE =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;



    private static boolean hasField(Class<?> klass, String fieldName) {
        try {
            klass.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

}
