package com.android.picshow.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.picshow.model.SimpleMediaItem;

import java.util.ArrayList;
import java.util.List;

public class PhotoPageUtils {

    public static SimpleMediaItem getUriFromCursor(Cursor c) {
        String type = c.getString(MediaSetUtils.INDEX_ITEM_TYPE);
        int rowID = c.getInt(MediaSetUtils.INDEX_ID);
        boolean image = PicShowUtils.isImage(type);
        Uri baseUri = image ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI :
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        return new SimpleMediaItem(baseUri.buildUpon().appendPath(String.valueOf(rowID)).build(), image, type);
    }

    public static ArrayList<Uri> mediaItem2Uris(List<SimpleMediaItem> items) {
        ArrayList<Uri> uris = new ArrayList<>(items.size());
        for (SimpleMediaItem item : items
        ) {
            uris.add(item.itemUrl);
        }
        return uris;
    }

}
