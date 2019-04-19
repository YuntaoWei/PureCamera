package com.pure.camera.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePermissionActivity extends AppCompatActivity {

    protected boolean gotAllPermission = false;

    private static final String TAG = "Camera_permission";

    public static String[] REQUIRE_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requirePermission();
    }

    private void requirePermission() {
        String[] permissions = checkPermission();
        Log.i(TAG, "requirePermission : " +
                (permissions == null ?
                        "got all permission" :
                        (permissions.length + " permissions need grant!")));
        if (null != permissions && permissions.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            gotAllPermission = true;
        }
    }

    private String[] checkPermission() {
        List<String> needGrant = new ArrayList<>();
        for (String p : REQUIRE_PERMISSIONS
                ) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                needGrant.add(p);
            }
        }

        if (needGrant.size() > 0) {
            String[] p = new String[needGrant.size()];
            needGrant.toArray(p);
            return p;
        } else {
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int r : grantResults
                ) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                onGetPermissionFailure();
                return;
            }
        }

        onGetPermissionSuccess();
    }

    public abstract void onGetPermissionSuccess();

    public abstract void onGetPermissionFailure();

}
