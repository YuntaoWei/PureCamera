package com.pure.camera.util;

import android.util.Log;

public class LogPrinter {

    public static final boolean DEBUG = true;
    public static final int LEVEL_VERBOSE = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_DEBUG = 4;
    public static final int LEVEL_ERROR = 5;

    public static final int DEFAULT_LEVEL = 2;


    public static void i(String tag, String msg) {
        if(DEBUG && DEFAULT_LEVEL <= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    public static void i_withTrace(String tag, String msg) {
        i(tag, msg + "\n" + Log.getStackTraceString(new Throwable()));
    }

    public static void v(String tag, String msg) {
        if(DEBUG && DEFAULT_LEVEL <= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void v_withTrace(String tag, String msg) {
        v(tag, msg + "\n" + Log.getStackTraceString(new Throwable()));
    }

    public static void w(String tag, String msg) {
        if(DEBUG && DEFAULT_LEVEL <= LEVEL_WARNING) {
            Log.w(tag, msg);
        }
    }

    public static void w_withTrace(String tag, String msg) {
        w(tag, msg + "\n" + Log.getStackTraceString(new Throwable()));
    }

    public static void e(String tag, String msg) {
        if(DEBUG && DEFAULT_LEVEL <= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void e_withTrace(String tag, String msg) {
        e(tag, msg + "\n" + Log.getStackTraceString(new Throwable()));
    }

    public static void d(String tag, String msg) {
        if(DEBUG && DEFAULT_LEVEL <= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d_withTrace(String tag, String msg) {
        d(tag, msg + "\n" + Log.getStackTraceString(new Throwable()));
    }

}
