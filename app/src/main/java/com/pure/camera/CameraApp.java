package com.pure.camera;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class CameraApp extends Application {

    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = getApplicationContext();
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

}
