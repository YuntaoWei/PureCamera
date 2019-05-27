package com.android.picshow.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PageControlAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mainPages;

    public PageControlAdapter(FragmentManager fm, ArrayList<Fragment> pages) {
        super(fm);
        mainPages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return mainPages.get(position);
    }

    @Override
    public int getCount() {
        return mainPages.size();
    }

}
