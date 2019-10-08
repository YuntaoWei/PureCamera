package com.android.picshow.model;

import android.app.Application;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class DataManager {

    private static DataManager INSTANCE;
    private HashMap<Uri, NotifyBroker> mNotifierMap =
            new HashMap<Uri, NotifyBroker>();
    private Application mApp;
    private Handler mDefaultMainHandler;

    private DataManager(Application app) {
        mApp = app;
        mDefaultMainHandler = new Handler();
    }

    public static DataManager getDataManager(Application app) {
        if (null == INSTANCE) {
            synchronized (DataManager.class) {
                INSTANCE = new DataManager(app);
            }
        }
        return INSTANCE;
    }

    public void registerObServer(Uri uri, ChangeNotify notify) {
        NotifyBroker broker = null;
        synchronized (mNotifierMap) {
            broker = mNotifierMap.get(uri);
            if (broker == null) {
                broker = new NotifyBroker(mDefaultMainHandler);
                mApp.getContentResolver()
                        .registerContentObserver(uri, true, broker);
                mNotifierMap.put(uri, broker);
            }
        }
        broker.registerNotifier(notify);
    }

    public void releaseAllObserver() {
        ContentResolver cr = mApp.getContentResolver();
        for (DataManager.NotifyBroker broker : mNotifierMap.values()
        ) {
            cr.unregisterContentObserver(broker);
            broker.clear();
        }
    }

    private class NotifyBroker extends ContentObserver {
        private WeakHashMap<ChangeNotify, Object> mNotifiers =
                new WeakHashMap<ChangeNotify, Object>();


        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public NotifyBroker(Handler handler) {
            super(handler);
        }

        public void registerNotifier(ChangeNotify n) {
            mNotifiers.put(n, null);
        }

        @Override
        public void onChange(boolean selfChange) {
            for (ChangeNotify notifier : mNotifiers.keySet()) {
                notifier.onChange(selfChange);
            }
        }

        public void clear() {
            mNotifiers.clear();
        }
    }

}
