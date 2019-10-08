package com.android.picshow.app;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.android.picshow.R;
import com.android.picshow.editor.filters.NewFilters;
import com.android.picshow.utils.MediaSetUtils;
import com.android.picshow.utils.PathHelper;

/**
 * Created by yuntao.wei on 2018/5/24.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

@Route(path = PathHelper.PATH_FILTER)
public class FilterTestActivity extends Activity {

    private ImageView filterShow;


    private Bitmap srcImage;

    private int[] filters = NewFilters.FilterType.getSupportFilters();

    private int filtetIndex = 0;

    private NewFilters newFilters;

    ColorMatrix matrix = new ColorMatrix();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_test_layout);
        filterShow = findViewById(R.id.filter_img);

        filterShow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeFiltter();
            }

        });

        loadImage();
    }

    private void changeFiltter() {
        if(newFilters == null) {
            newFilters = NewFilters.getInstance(this);
        }

        Bitmap filterImage = newFilters.filterImage(this, srcImage,
                filters[filtetIndex % filters.length]);
        filterShow.setImageBitmap(filterImage);

        filtetIndex ++;
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadImage() {
        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Images.Media.DATA},
                MediaStore.Images.Media.BUCKET_ID + " = " + MediaSetUtils.CAMERA_BUCKET_ID,
                null, null);

        String path = null;
        if(c != null && c.moveToNext()) {
            path = c.getString(0);
            c.close();
            c = null;
        }

        if(path == null)
            return;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        options.inSampleSize = 2;

        srcImage = BitmapFactory.decodeFile(path, options);

        filterShow.setImageBitmap(srcImage);
    }

}
