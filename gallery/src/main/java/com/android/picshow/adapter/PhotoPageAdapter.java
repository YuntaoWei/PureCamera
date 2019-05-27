package com.android.picshow.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.android.picshow.app.PhotoViewFragment;

/**
 * Created by yuntao.wei on 2017/12/11.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PhotoPageAdapter extends BaseCursorPagerAdapter {

    /**
     * Constructor that always enables auto-requery.
     *
     * @param context The context
     * @param fm
     * @param c       The cursor from which to get the data.
     */
    public PhotoPageAdapter(Context context, FragmentManager fm, Cursor c) {
        super(context, fm, c);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor, int position) {
        return createNewFragment(cursor, position);
    }


    @Override
    public Fragment createNewFragment(Cursor c, int position) {
        return PhotoViewFragment.newInstance(c, position);
    }
}
