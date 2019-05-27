package com.android.picshow.model;

import android.net.Uri;

public class SimpleMediaItem {

    public Uri itemUrl;
    public boolean isImage;
    public String itemType;

    public SimpleMediaItem(Uri u, boolean image, String type) {
        itemUrl = u;
        isImage = image;
        itemType = type;
    }

}
