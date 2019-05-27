package com.android.picshow.model;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yuntao.wei on 2017/12/12.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class SortCursor extends CursorWrapper implements Comparator<SortCursor.SortEntry> {


    Cursor mCursor;
    ArrayList<SortEntry> sortList = new ArrayList<SortEntry>();
    int mPos = -1;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public SortCursor(Cursor cursor) {
        super(cursor);
    }

    public static class SortEntry {
        public int order;
    }

    @Override
    public int compare(SortEntry entry1, SortEntry entry2) {
        return entry1.order - entry2.order;
    }

    public SortCursor(Cursor cursor, int nameColumnIndex) {
        super(cursor);
        mCursor = cursor;
        if (mCursor != null && mCursor.getCount() > 0) {
            int i = 0;
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext(), i++) {
                SortEntry sortKey = new SortEntry();
                int id = cursor.getInt(nameColumnIndex);
                sortKey.order = i;
                sortList.add(sortKey);
            }
        }
        Collections.sort(sortList, this);
    }

    public boolean moveToPosition(int position) {
        if (position >= 0 && position < sortList.size()) {
            mPos = position;
            int order = sortList.get(position).order;
            return mCursor.moveToPosition(order);
        }
        if (position < 0) {
            mPos = -1;
        }
        if (position >= sortList.size()) {
            mPos = sortList.size();
        }
        return mCursor.moveToPosition(position);
    }

    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    public boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }

    public boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }

    public boolean move(int offset) {
        return moveToPosition(mPos + offset);
    }

    public int getPosition() {
        return mPos;
    }
}
