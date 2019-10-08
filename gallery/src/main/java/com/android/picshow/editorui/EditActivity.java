package com.android.picshow.editorui;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.graffti.DialogController;
import com.android.picshow.editor.graffti.GraffitiParams;
import com.android.picshow.editor.operate.OperateUtils;
import com.android.picshow.editor.utils.FileUtils;
import com.android.picshow.editorui.utils.Constants;
import com.android.picshow.editorui.utils.FragmentUtils;
import com.pure.commonbase.LogPrinter;
import com.pure.commonbase.PathHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yuntao.wei on 2018/5/15.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

@Route(path = PathHelper.PATH_EDIT)
public class EditActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private LinearLayout content_layout;
    private ImageView pictureShow;

    private View topLayout;

    private String photoPath = null, tempPath = null;

    int width = 0;

    OperateUtils operateUtils;
    private Uri srcUri;

    private FrameLayout bottomMainContainer;

    private BaseEditor currentEditor;
    private FragmentManager fragmentManager;

    private Button btnCancel, btnSave, btnCompare;
    private Bundle mBundle;
    private Bitmap editedPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editactivity_layout);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        srcUri = intent.getData();
        operateUtils = new OperateUtils(this);
        if (srcUri == null) {
            photoPath = intent.getStringExtra(BaseEditorManager.SRC_PIC_PATH);
            if (photoPath == null) {
                return;
            }
        }
        mBundle = new Bundle();
    }

    private void initView() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;

        pictureShow = (ImageView) findViewById(R.id.pictureShow);
        content_layout = (LinearLayout) findViewById(R.id.mainLayout);
        topLayout = findViewById(R.id.top_layout);
        bottomMainContainer = (FrameLayout) findViewById(R.id.bottom_panel);
        btnCancel = (Button) findViewById(R.id.cancel);
        btnSave = (Button) findViewById(R.id.save);
        btnCompare = (Button) findViewById(R.id.compare);

        btnCompare.setOnTouchListener(this);

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        changeBottomPanelByType(BaseEditorManager.NONE);
        loadPicture();
    }

    private void changeBottomPanelByType(int type) {

        final int layoutID = BaseEditorManager.getLayoutByType(type);
        View v = getLayoutInflater().inflate(layoutID, null);
        bottomMainContainer.removeAllViews();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        bottomMainContainer.addView(v, layoutParams);

        if (type == BaseEditorManager.NONE) {
            initBottomMenu();
        }
    }

    public void onFragmentAttached() {

        currentEditor.initPanelView(bottomMainContainer.getChildAt(0));
        pictureShow.setVisibility(View.GONE);

    }

    private Button btnFilter, btnBeauty, btnCrop, btnEnhance, btnMosaic, btnPaint;

    private void initBottomMenu() {

        btnFilter = (Button) findViewById(R.id.edit_filter);
        btnBeauty = (Button) findViewById(R.id.edit_beatuty);
        btnCrop = (Button) findViewById(R.id.edit_crop);
        btnEnhance = (Button) findViewById(R.id.edit_enhance);
        btnMosaic = (Button) findViewById(R.id.edit_mosatic);
        btnPaint = (Button) findViewById(R.id.edit_draw);

        btnFilter.setOnClickListener(this);
        btnBeauty.setOnClickListener(this);
        btnCrop.setOnClickListener(this);
        btnEnhance.setOnClickListener(this);
        btnMosaic.setOnClickListener(this);
        btnPaint.setOnClickListener(this);

    }

    public void showCompare() {
        if (btnCompare != null)
            btnCompare.setVisibility(View.VISIBLE);
    }

    public void hideCompare() {
        if (btnCompare != null)
            btnCompare.setVisibility(View.INVISIBLE);
    }

    public void hideTopBar() {
        if (topLayout != null)
            topLayout.setVisibility(View.GONE);
    }

    public void showTopBar() {
        if (topLayout != null)
            topLayout.setVisibility(View.VISIBLE);
    }

    private void addFragment(int type) {
        hideTopBar();

        if (type == BaseEditorManager.PAINT) {
            GraffitiParams params = new GraffitiParams();
            params.mImagePath = tempPath;
            params.mPaintSize = 20;
            mBundle.putParcelable(DrawBaseFragment.KEY_PARAMS, params);
        }

        BaseEditor f = FragmentUtils.getFragment(type, mBundle);

        if (f == null)
            return;
        currentEditor = f;

        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    if (fragmentManager.getBackStackEntryCount() == 0) {
                        LogPrinter.i("www", "show new pic : " + (editedPic == null ? true : editedPic.isRecycled()));
                        if (editedPic != null && !editedPic.isRecycled()) {
                            pictureShow.setImageBitmap(editedPic);
                        }
                        pictureShow.setVisibility(View.VISIBLE);
                        changeBottomPanelByType(BaseEditorManager.NONE);
                        currentEditor = null;
                        hideCompare();
                        showTopBar();
                    }
                    ;
                }

            });
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.mainLayout, (Fragment) currentEditor);
        transaction.addToBackStack(null);
        transaction.commit();

        changeBottomPanelByType(type);
    }

    private void loadPicture() {
        new Thread() {

            @Override
            public void run() {
                compressed();
            }

        }.start();
    }

    private void compressed() {
        final Bitmap resizeBmp = operateUtils.compressionFiller(photoPath,
                content_layout);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pictureShow.setImageBitmap(resizeBmp);
            }
        });
        tempPath = saveTmpBitmap(resizeBmp, "Edit_" + System.currentTimeMillis());
        mBundle.putString(BaseEditorManager.SRC_PIC_PATH, tempPath);
    }

    public String saveTmpBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.filePath + name + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.edit_filter) {
            addFragment(BaseEditorManager.FILTER);

        } else if (i == R.id.edit_beatuty) {
            addFragment(BaseEditorManager.BEAUTY);

        } else if (i == R.id.edit_crop) {
            addFragment(BaseEditorManager.CROP);

        } else if (i == R.id.edit_enhance) {
            addFragment(BaseEditorManager.ENHANCE);

        } else if (i == R.id.edit_mosatic) {
            addFragment(BaseEditorManager.MOSAIC);

        } else if (i == R.id.edit_draw) {
            addFragment(BaseEditorManager.PAINT);

        } else if (i == R.id.cancel) {//if(currentEditor != null)
            //    currentEditor.onCancel();
            finish();

        } else if (i == R.id.save) {
            if (editedPic == null)
                return;
            DialogController.showEnterCancelDialog(this, getString(R.string.saving_image),
                    null, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Dialog d = DialogController.showProgressDialog(EditActivity.this,
                                    getString(R.string.saving_image));

                            int width = editedPic.getWidth();
                            int height = editedPic.getHeight();

                            File f = new File(tempPath);
                            try {
                                FileOutputStream fops = new FileOutputStream(f);
                                editedPic.compress(Bitmap.CompressFormat.JPEG, 100, fops);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            editedPic.recycle();
                            editedPic = null;
                            FileUtils.updateDB(EditActivity.this, tempPath, null,
                                    null, width, height);
                            d.dismiss();
                            finish();
                        }

                    }, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }

                    });
            //if(currentEditor != null)
            //    currentEditor.onSave();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (editedPic != null)
            editedPic.recycle();
        editedPic = null;
    }

    public void shouldReLoadPicture(Bitmap bm) {
        editedPic = bm;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                currentEditor.onCompareStart();
                return true;

            case MotionEvent.ACTION_UP:
                currentEditor.onCompareEnd();
                return true;

        }
        return false;
    }
}
