package com.pure.camera.data;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.pure.camera.common.LogPrinter;

public class DataChangeNotifier extends ContentObserver {

    private static final String TAG = "DataChangeNotifier";

    private DataChangeListener dataChangeListener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DataChangeNotifier(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        LogPrinter.i(TAG, "DataChangeNotifier onChange : " + selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        LogPrinter.i(TAG, "DataChangeNotifier onChange : " + selfChange + "   " + uri);
        if(null != dataChangeListener) {
            dataChangeListener.onDataChange(selfChange, uri);
        }
    }

    public void setDataChangeListener(DataChangeListener l) {
        dataChangeListener = l;
    }

    public void removeDataChangeListener() {
        dataChangeListener = null;
    }

}
