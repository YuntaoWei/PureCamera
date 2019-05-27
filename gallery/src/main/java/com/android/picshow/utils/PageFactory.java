package com.android.picshow.utils;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.android.picshow.app.AlbumSetPage;
import com.android.picshow.app.TimeLinePage;

import java.util.ArrayList;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PageFactory {

    private static SparseArray<Fragment> frg = new SparseArray<>();

    public static final int INDEX_TIMELINE = 0;
    public static final int INDEX_ALBUMSET = 1;
    public static final int INDEX_ALBUM = 2;

    static enum PageEnum {
        PAGE_TIMELINE,PAGE_ALBUMSET,PAGE_ALBUM
    };

    public static Fragment loadPage(int pos) {
        if (frg.get(pos) == null) {
            frg.put(pos, createPage(pos));
        }
        return frg.get(pos);
    }

    public static ArrayList<Fragment> getMainPage() {
        ArrayList<Fragment> pages = new ArrayList<>();
        pages.add(loadPage(INDEX_TIMELINE));
        pages.add(loadPage(INDEX_ALBUMSET));
        return pages;
    }

    private static Fragment createPage(int position) {
        PageEnum frg = PageEnum.values()[position];
        switch (frg) {
            case PAGE_TIMELINE:
                return new TimeLinePage();
            case PAGE_ALBUMSET:
                return new AlbumSetPage();
        }
        return null;
    }


}
