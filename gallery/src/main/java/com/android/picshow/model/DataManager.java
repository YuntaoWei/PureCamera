package com.android.picshow.model;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.android.picshow.app.PictureShowApplication;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class DataManager {

    private HashMap<Uri, NotifyBroker> mNotifierMap =
            new HashMap<Uri, NotifyBroker>();
    private PictureShowApplication mApp;
    private Handler mDefaultMainHandler;

    public DataManager(PictureShowApplication app) {
        mApp = app;
        mDefaultMainHandler = new Handler();
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
            for(ChangeNotify notifier : mNotifiers.keySet()) {
                notifier.onChange(selfChange);
            }
        }
    }

}
