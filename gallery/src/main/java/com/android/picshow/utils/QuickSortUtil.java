package com.android.picshow.utils;

import android.database.Cursor;

import com.pure.commonbase.LogPrinter;

public class QuickSortUtil {

    public static int quickGetPosition(Cursor c, long id) {
        if (null == c)
            return 0;

        int count = c.getCount();
        if (count < 2)
            return 0;

        int middle = count / 2;
        long itemId;
        while (c.moveToPosition(middle)) {
            itemId = c.getInt(0);
            LogPrinter.i("ttt", itemId + "  " + id);
            if (itemId == id)
                return middle;

            if (itemId > id) {
                middle++;
            } else {
                middle--;
            }

            if (middle >= count || middle <= 0)
                return 0;
        }

        return count;
    }


}
