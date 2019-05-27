package com.android.picshow.editor.graffti;

import android.graphics.Bitmap;

public interface GraffitiListener {


    void onSaved(Bitmap bitmap, Bitmap bitmapEraser);


    void onError(int i, String msg);


    void onReady();


    void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected);


    void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y);

}
