package com.pure.camera.data;

import android.net.Uri;

public interface DataChangeListener {

    void onDataChange(boolean selfChange, Uri uri);

}
