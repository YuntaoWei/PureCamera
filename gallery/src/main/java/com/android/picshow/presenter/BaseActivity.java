package com.android.picshow.presenter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.android.picshow.R;
import com.android.picshow.view.IDelegate;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public abstract class BaseActivity<T extends IDelegate> extends PermissionActivity {

    protected T viewDelegate;
    protected boolean icBlack = false;

    public BaseActivity() {
        try {
            viewDelegate = getDelegateClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("create IDelegate error");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("create IDelegate error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(viewDelegate.getRootView());
        if(isPermissionGranted()) {
            initView();
        }
    }

    protected void initView() {
        initToolbar();
        viewDelegate.initWidget();
        bindEvenListener();

        setTitle("");
        updateStatusBar();
    }

    protected void initToolbar() {
        Toolbar toolbar = viewDelegate.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if(icBlack) {
                toolbar.setNavigationIcon(R.drawable.back_black_n);
            } else {
                toolbar.setNavigationIcon(R.mipmap.ic_back);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewDelegate == null) {
            try {
                viewDelegate = getDelegateClass().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("create IDelegate error");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("create IDelegate error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewDelegate.getOptionsMenuId() != 0) {
            getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewDelegate = null;
    }

    @Override
    protected void onGetPermissionsFailure() {
        finish();
    }

    @Override
    protected void onGetPermissionsSuccess() {
        initView();
    }

    private void updateStatusBar() {

    }

    protected void bindEvenListener() {
    }

    protected abstract Class<T> getDelegateClass();

}
