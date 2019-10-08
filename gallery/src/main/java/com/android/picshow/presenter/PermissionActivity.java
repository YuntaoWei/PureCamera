package com.android.picshow.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public abstract class PermissionActivity extends AppCompatActivity {


    private static final String TAG = "BaseActivity";
    public static final int PERMISSION_REQUEST_STORAGE = 1;
    private boolean permissionGranted = false;


    protected abstract void onGetPermissionsSuccess();

    protected abstract void onGetPermissionsFailure();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        requestPermission(permissions, PERMISSION_REQUEST_STORAGE);
    }

    protected void requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted = true;
            return;
        }

        boolean needRequest = false;
        ArrayList<String> permissionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
                needRequest = true;
            }
        }

        if (needRequest) {
            int count = permissionList.size();
            if (count > 0) {
                String[] permissionArray = new String[count];
                for (int i = 0; i < count; i++) {
                    permissionArray[i] = permissionList.get(i);
                }

                ActivityCompat.requestPermissions(this, permissionArray, requestCode);
            }
        }
        permissionGranted = !needRequest;
    }

    private boolean checkPermissionGrantResults(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = checkPermissionGrantResults(grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                if (permissionGranted) {
                    onGetPermissionsSuccess();
                } else {
                    onGetPermissionsFailure();
                }
            }
        }
    }

    protected boolean isPermissionGranted() {
        return permissionGranted;
    }

}
