package com.android.picshow.app;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.picshow.R;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.presenter.BaseFragment;
import com.android.picshow.utils.LogPrinter;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.view.fragment.PhotoViewFragmentDelegate;

/**
 * Created by yuntao.wei on 2017/12/11.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PhotoViewFragment extends BaseFragment<PhotoViewFragmentDelegate> implements View.OnClickListener {

    private static final String TAG = "PhotoViewFragment";

    private int currentPosition;
    private int currentPhotoID;
    private static final String TYPE = "type";
    private int type;
    private String currentPath;
    private static final int INVALID = -1;
    private static final String CURRENT_POSITION = "current_position";

    public PhotoViewFragment() {}

    public static Fragment newInstance(Cursor c, int position) {
        final Fragment f = new PhotoViewFragment();
        final Bundle b = new Bundle();

        int photoID = c.getInt(0);
        b.putInt(MediaSetUtils.PHOTO_ID, photoID);

        String path = c.getString(1);
        b.putString(MediaSetUtils.PHOTO_PATH, path);

        String type = c.getString(2);
        b.putInt(TYPE, type == null ? PhotoItem.TYPE_IMAGE :
                (type.startsWith("video") ? PhotoItem.TYPE_VIDEO : PhotoItem.TYPE_IMAGE));

        b.putInt(CURRENT_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewDelegate.showBitmap(currentPath);
        viewDelegate.setVideoIconVisibility(type == PhotoItem.TYPE_VIDEO ? true : false);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.photo, R.id.videoIcon);
    }

    private void init() {
        Bundle b = getArguments();
        currentPosition = b.getInt(CURRENT_POSITION, INVALID);
        currentPhotoID = b.getInt(MediaSetUtils.PHOTO_ID, INVALID);
        currentPath = b.getString(MediaSetUtils.PHOTO_PATH);
        type = b.getInt(TYPE);
        LogPrinter.i(TAG, "currentPosition:" + currentPosition + "  currentPhotoID:" + currentPhotoID
                + "    currentPath:" + currentPath);
    }

    @Override
    public void onDestroy() {
        viewDelegate.freeSource(this);
        super.onDestroy();
    }

    @Override
    protected Class getDelegateClass() {
        return PhotoViewFragmentDelegate.class;
    }

    private void playVideo() {
        Uri videoUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.getContentUri("external"), currentPhotoID);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(videoUri,"video/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.photo) {
            ((PhotoActivity) getActivity()).toggleFullScreen();

        } else if (i == R.id.videoIcon) {
            playVideo();

        }

    }
}
