package com.android.picshow.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.android.picshow.model.Album;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.model.SortCursor;
import com.pure.commonbase.ApiHelper;
import com.pure.commonbase.BucketHelper;

import java.util.ArrayList;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class MediaSetUtils {

    public static final int CAMERA_BUCKET_ID
            = BucketHelper.getBucketID(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/DCIM/Camera");

    public static final String BUCKET = "bucket";
    public static final String SET_NAME = "setname";
    public static final String PHOTO_ID = "_id";
    public static final String PHOTO_PATH = "_path";

    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    /**
     * for PhotoItem----start
     **/
    private static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED
    };
    public static final int INDEX_ID = 0;
    public static final int INDEX_DISPLAY_NAME = 1;
    public static final int INDEX_DATA = 2;
    public static final int INDEX_ITEM_TYPE = 2;
    public static final int INDEX_DATE = 3;
    public static final int INDEX_DATE_ADD = 4;
    /** for PhotoItem----end **/

    /**
     * for Album----start
     **/
    private static final String[] ALBUM_PROJECTION = {
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA,
            "count(*)"
    };
    private static final int ALBUM_BUCKET_INDEX = 0;
    private static final int ALBUM_NAME_INDEX = 1;
    private static final int ALBUM_DATE_INDEX = 2;
    private static final int ALBUM_DATA_INDEX = 3;
    /**
     * for Album----end
     **/

    private static final String[] ALBUM_PROJECTION_FROM_TABLE = {
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA,
            "count(*)",
            MediaStore.Files.FileColumns.MEDIA_TYPE
    };

    private static final int ALBUM_COUNT_INDEX = 4;
    private static final int ALBUM_MEDIA_TYPE = 5;


    private static final String ALBUM_GROUP_BY = "1) GROUP BY (1";

    private static final String WHERE = MediaStore.Images.Media.BUCKET_ID + " = ?";
    private static final String DEFAULT_SORT_ODER = "datetaken DESC";


    /**
     * Query the Images in the specified directory.
     *
     * @param mContext
     * @param items
     * @param bucketID
     */
    public static void queryImages(Context mContext, ArrayList<PhotoItem> items, long bucketID) {
        //query image;
        Cursor c = mContext.getContentResolver().query(IMAGE_URI,
                PROJECTION,
                WHERE,
                new String[]{bucketID + ""},
                DEFAULT_SORT_ODER
        );

        try {
            while (c.moveToNext()) {
                items.add(new PhotoItem(
                        c.getInt(INDEX_ID),
                        c.getString(INDEX_DISPLAY_NAME),
                        c.getString(INDEX_DATA),
                        c.getLong(INDEX_DATE),
                        c.getLong(INDEX_DATE_ADD),
                        PhotoItem.TYPE_IMAGE));
            }
        } finally {
            c.close();
        }
    }

    /**
     * Query the Videos in the specified directory.
     *
     * @param mContext
     * @param items
     * @param bucketID
     */
    public static void queryVideo(Context mContext, ArrayList<PhotoItem> items, long bucketID) {
        //query image;
        Cursor c = mContext.getContentResolver().query(VIDEO_URI,
                PROJECTION,
                WHERE,
                new String[]{bucketID + ""},
                DEFAULT_SORT_ODER
        );
        try {
            while (c.moveToNext()) {
                items.add(new PhotoItem(
                        c.getInt(INDEX_ID),
                        c.getString(INDEX_DISPLAY_NAME),
                        c.getString(INDEX_DATA),
                        c.getLong(INDEX_DATE),
                        c.getLong(INDEX_DATE_ADD),
                        PhotoItem.TYPE_VIDEO));
            }
        } finally {
            c.close();
        }
    }

    public static Cursor queryAllItemByBucketID(ContentResolver cr, int bucket) {
        Cursor result = null;
        Cursor[] results = new Cursor[2];

        results[0] = cr.query(IMAGE_URI, new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.TITLE
        }, WHERE, new String[]{bucket + ""}, DEFAULT_SORT_ODER);

        results[1] = cr.query(VIDEO_URI, new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.TITLE
        }, WHERE, new String[]{bucket + ""}, DEFAULT_SORT_ODER);

        if (results[0] != null) {
            if (results[1] != null)
                result = new MergeCursor(results);
            else
                result = results[0];
        } else if (results[1] != null)
            result = results[1];

        if (result != null)
            return new SortCursor(result, INDEX_ID);
        return null;
    }

    public static Album[] getAllAlbum(Context ctx) {
        if (ApiHelper.HAS_MEDIA_PROVIDER_FILES_TABLE)
            return queryAllAlbumSetFromFileTable(ctx);
        else
            return queryAllAlbumSet(ctx);
    }


    public static Album[] queryAllAlbumSetFromFileTable(Context mContext) {
        int type = 2 | 4;
        ArrayList<Album> buffer = new ArrayList<Album>();
        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor c = mContext.getContentResolver().query(uri,
                ALBUM_PROJECTION_FROM_TABLE,
                ALBUM_GROUP_BY,
                null,
                /*DEFAULT_SORT_ODER*/null);

        int typeBits = 0;
        if ((type & 2) != 0) {
            typeBits |= (1 << MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        }
        if ((type & 4) != 0) {
            typeBits |= (1 << MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        }

        try {
            while (c.moveToNext()) {
                if ((typeBits & (1 << c.getInt(ALBUM_MEDIA_TYPE))) != 0) {
                    Album entry = new Album(
                            c.getInt(ALBUM_BUCKET_INDEX),
                            c.getString(ALBUM_NAME_INDEX),
                            c.getInt(ALBUM_DATE_INDEX),
                            c.getString(ALBUM_DATA_INDEX),
                            c.getInt(ALBUM_COUNT_INDEX));
                    if (!buffer.contains(entry)) {
                        buffer.add(entry);
                    }
                }
            }
        } finally {
            c.close();
        }

        return buffer.toArray(new Album[buffer.size()]);
    }

    public static Album[] queryAllAlbumSet(Context mContext) {
        SparseArray<Album> allAlbums = new SparseArray<>();
        //All Image Album.
        Cursor cImage = mContext.getContentResolver().query(IMAGE_URI,
                ALBUM_PROJECTION,
                ALBUM_GROUP_BY,
                null,
                DEFAULT_SORT_ODER
        );
        if (cImage == null)
            return null;

        try {
            while (cImage.moveToNext()) {
                int bucket = cImage.getInt(ALBUM_BUCKET_INDEX);
                allAlbums.put(bucket, new Album(bucket,
                        cImage.getString(ALBUM_NAME_INDEX),
                        cImage.getInt(ALBUM_DATE_INDEX),
                        cImage.getString(ALBUM_DATA_INDEX),
                        cImage.getInt(ALBUM_COUNT_INDEX)));
            }
        } finally {
            cImage.close();
        }

        //All Video Album
        Cursor cVideo = mContext.getContentResolver().query(VIDEO_URI,
                ALBUM_PROJECTION,
                ALBUM_GROUP_BY,
                null,
                DEFAULT_SORT_ODER
        );

        if (cVideo == null)
            return null;
        try {
            while (cVideo.moveToNext()) {
                int bucket = cVideo.getInt(ALBUM_BUCKET_INDEX);
                //Maybe there are some videos and images in the same folder,
                //Prevent add repeatedly.
                Album newAlbum = allAlbums.get(bucket);
                int dateToken = cVideo.getInt(ALBUM_DATE_INDEX);
                int count = cVideo.getInt(ALBUM_COUNT_INDEX);
                if (newAlbum != null) {
                    if (newAlbum.dateToken > dateToken) {
                        newAlbum.addCount(count);
                        //newAlbum.absPath = cVideo.getString(ALBUM_DATA_INDEX);
                        continue;
                    } else
                        allAlbums.remove(bucket);
                }
                allAlbums.put(bucket, new Album(bucket,
                        cVideo.getString(ALBUM_NAME_INDEX),
                        dateToken,
                        cVideo.getString(ALBUM_DATA_INDEX),
                        count + (newAlbum == null ? 0 : newAlbum.count)));

            }
        } finally {
            cVideo.close();
        }
        int size = allAlbums.size();
        Album[] albums = new Album[size];
        for (int i = 0; i < size; i++) {
            albums[i] = allAlbums.valueAt(i);
        }
        allAlbums.clear();
        allAlbums = null;
        return albums;
    }

    public static String uriToPath(Context ctx, Uri uri) {
        String path = null;
        Cursor c = ctx.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if (c != null && c.moveToNext()) {
            path = c.getString(0);
            c.close();
            c = null;
        }
        return path;
    }


}
