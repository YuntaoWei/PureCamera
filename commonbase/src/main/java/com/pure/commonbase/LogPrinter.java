package com.pure.commonbase;

import android.util.Log;

/**
 * Log print helper class.
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class LogPrinter {

    private static final String DEBUG_TAG = "wytt";

    public static void DEBUG_D(String msg) {
        Log.d(DEBUG_TAG, msg);
    };

    public static void i(String tag, String msg) {
        Log.i(tag,msg);
    }

    public static void i_withTrace(String tag, String msg) {
        Log.i(tag,msg + " \n " + Log.getStackTraceString(new Throwable()));
    }

    public static void e(String tag, String msg) {
        Log.e(tag,msg);
    }

    public static void e_withTrace(String tag, String msg) {
        Log.e(tag,msg + " \n " + Log.getStackTraceString(new Throwable()));
    }

    public static void w(String tag, String msg) {
        Log.w(tag,msg);
    }

    public static void w_withTrace(String tag, String msg) {
        Log.w(tag,msg + " \n " + Log.getStackTraceString(new Throwable()));
    }

    public static void v(String tag, String msg) {
        Log.v(tag,msg);
    }

    public static void v_withTrace(String tag, String msg) {
        Log.v(tag,msg + " \n " + Log.getStackTraceString(new Throwable()));
    }

}
