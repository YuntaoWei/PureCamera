package com.android.picshow.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by yuntao.wei on 2017/12/15.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class ActionModeHandler implements Callback {


    public interface ActionModeCallBack {

        public void enterActionMode();

        public void exitActionMode();

        public void onItemClick(MenuItem item);

    }

    private ActionModeCallBack actionModeCallBack;
    private Toolbar topBar;
    private ActionMode actionMode;

    public ActionModeHandler() {}


    public ActionModeHandler(Context context, Toolbar toolbar, ActionModeCallBack callBack) {

        topBar = toolbar;
        actionModeCallBack = callBack;
    }

    public void startActionMode() {
        if(topBar == null)
            return;
        actionMode = topBar.startActionMode(this);

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionModeCallBack.enterActionMode();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        actionModeCallBack.onItemClick(item);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionModeCallBack.exitActionMode();
    }



}
