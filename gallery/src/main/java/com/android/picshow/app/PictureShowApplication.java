package com.android.picshow.app;

import android.app.Application;
import android.os.StrictMode;

import com.android.picshow.BuildConfig;
import com.android.picshow.model.DataManager;
import com.android.picshow.utils.LogPrinter;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PictureShowApplication extends Application {

    DataManager mDataManager;

    @Override
    public void onCreate() {
        if(BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .penaltyDialog()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
        mDataManager = DataManager.getDataManager(this);
    }

    public DataManager getDataManager() {
        return mDataManager;
    }
}
