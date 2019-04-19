package com.pure.camera.util;

import android.widget.Toast;

import com.pure.camera.CameraApp;

public class ToastManager {

    public static final int SHORT = Toast.LENGTH_SHORT;
    public static final int LONG = Toast.LENGTH_LONG;

    private Toast toast;

    private ToastManager() {
        toast = Toast.makeText(CameraApp.getGlobalContext(), "", Toast.LENGTH_SHORT);
    }

    public static class ToastManagerHolder {
        public static ToastManager instance = new ToastManager();
    }

    public void show(String text) {
        toast.setText(text);
        toast.show();
    }

    public ToastManager setDuration(int duration) {
        toast.setDuration(duration);
        return this;
    }

    public void release() {
        ToastManagerHolder.instance = null;
    }

}
