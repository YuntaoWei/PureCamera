package com.android.picshow.editor.filters;

import android.view.View;

/**
 * Created by yuntao.wei on 2018/5/16.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public interface BaseEditor {

    public void initPanelView(View v);

    public void onSave();

    public void onCancel();

    public void onCompareStart();

    public void onCompareEnd();

}
