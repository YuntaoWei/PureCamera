package com.pure.camera.view;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pure.camera.common.ToastManager;
import com.pure.camera.opengl.data.PreviewSize;

public abstract class BaseView {

    private SparseArray<View> childViews = new SparseArray<>();
    private View rootView;
    private ToastManager toastManager;
    protected Handler mainHandler;

    public View onCreateView(LayoutInflater inflater, int layout, ViewGroup group) {
        rootView = inflater.inflate(layout, group);
        return rootView;
    }

    public <T extends AppCompatActivity>T getContext() {
        return (T) rootView.getContext();
    }

    public ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

    public LayoutInflater getLayoutInflater() {
        return getContext().getLayoutInflater();
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View view) {
        if(null != view) {
            rootView = view;
        }
    }

    private <T extends View>T bindView(int id) {
        T v = (T) childViews.get(id);
        if(v == null) {
            v = rootView.findViewById(id);
            childViews.put(id, v);
        }

        return v;
    }

    public void runOnUiThread(Runnable r) {
        if(null == mainHandler) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        mainHandler.post(r);
    }

    public void runOnUiThreadDelay(Runnable r, int delay) {
        if(null == mainHandler) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        mainHandler.postDelayed(r, delay);
    }

    public <T extends View>T getView(int id) {
        return bindView(id);
    }

    public void setOnClickListener(View.OnClickListener l, int... ids) {
        for(int id : ids) {
            View v = getView(id);
            if(v != null) {
                v.setOnClickListener(l);
            }
        }
    }

    public void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    public void toast(final String msg, final int duration) {
        if(null == toastManager) {
            toastManager = ToastManager.ToastManagerHolder.instance;
        }

        if(Thread.currentThread().getName().equals("main")) {
            toastManager.setDuration(duration).show(msg);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastManager.setDuration(duration).show(msg);
                }
            });
        }
    }

    public String getString(int id) {
        return getContext().getString(id);
    }

    public String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    public abstract void resume();

    public abstract void pause();

    public void destroy() {
        childViews.clear();
        rootView = null;
        toastManager = null;
    }
}
