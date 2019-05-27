package com.android.picshow.editor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.android.picshow.R;

/**
 * Created by yuntao.wei on 2018/5/15.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class BaseEditorManager {

    public interface LoadListener {

        public void onLoadSuccess(Object o);

        public void onLoadFailed();

    }

    private static final String TAG = "BaseEditorManager";

    public static final int NONE = 0x00;
    public static final int FILTER = 0X01;
    public static final int BEAUTY = 0X02;
    public static final int CROP = 0X03;
    public static final int ENHANCE = 0X04;
    public static final int MOSAIC = 0X05;
    public static final int PAINT = 0X06;

    public static final float MAX_SIZE = 1080f;

    public static final String SRC_PIC_PATH = "pic_path";
    public static final String SRC_PIC_NAME = "pic_name";

    public static final int CROP_MODE = 0x1;
    public static final int ROTATE_MODE = 0x2;

    public static final int MOSAIC_SMALL_RADIUS = 20;
    public static final int MOSAIC_BIG_RADIUS = 40;
    public static final int MOSIC_BLUR_RADIUS = 20;

    public static final int FILTER_THUMB_SIZE = 150;




    public static int getLayoutByType(int type) {

        int layout = -1;

        switch (type) {
            case NONE:
                layout = R.layout.main_bottom_menu;
                break;

            case BEAUTY:

            case CROP:
                layout = R.layout.crop_bottom_operation;
                break;

            case ENHANCE:
                layout = R.layout.enhance_bottom_operation;
                break;

            case MOSAIC:
                layout = R.layout.mosaic_bottom_operation;
                break;

            case PAINT:
                layout = R.layout.draw_bottom_operation;
                break;

            case FILTER:
                layout = R.layout.filter_bottom_layout;
                break;

            default:
                throw new UnsupportedOperationException("Not supported " + type +" edit operation.");

        }

        return layout;
    }

    public static Bitmap resize(Bitmap src, float wScale, float hScale) {
        Matrix m = new Matrix();
        m.postScale(wScale, hScale);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
        return dst;
    }

    public static Bitmap decodeBitmap(String src, int maxSize) {

        Bitmap bm;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, option);
        int width = option.outWidth;
        int height = option.outHeight;
        Log.i(TAG, "scale before : " + width + " x " + height);
        float scale = 0f;
        if (width > height) {
            if (width > maxSize) {
                scale = width / maxSize;
                option.outWidth = maxSize;
                option.outHeight /= scale;
                Log.i(TAG, "scale 1 : " + scale + " " + option.outWidth + " x " + option.outHeight);
            }
        } else {
            if (height > maxSize) {
                scale = height / maxSize;
                option.outHeight = maxSize;
                option.outWidth /= scale;
                Log.i(TAG, "scale 2 : " + scale + " " + option.outWidth + " x " + option.outHeight);
            }
        }

        option.inJustDecodeBounds = false;
        option.inSampleSize = (int) scale;
        bm = BitmapFactory.decodeFile(src, option);

        bm = BaseEditorManager.resize(bm, 1 / scale, 1 / scale);
        return bm;
    }

    public static void decodeBitmapAsync(final String src, final LoadListener l) {
        new Thread(){

            @Override
            public void run() {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inPreferredConfig = Bitmap.Config.RGB_565;
                option.inSampleSize = 1;

                Bitmap bm = BitmapFactory.decodeFile(src, option);

                if(l != null) {
                    l.onLoadSuccess(bm);
                }
            }

        }.start();
    }

    public static void decodeBitmapAsync(final String src, final float maxSize, final LoadListener l) {

        new Thread() {

            @Override
            public void run() {
                Bitmap bm;
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(src, option);
                int width = option.outWidth;
                int height = option.outHeight;
                Log.i(TAG, "scale before : " + width + " x " + height);
                float scale = 0f;
                if (width > height) {
                    if (width > maxSize) {
                        scale = width / maxSize;
                        option.outWidth = (int)maxSize;
                        option.outHeight /= scale;
                        Log.i(TAG, "scale 1 : " + scale + " " + option.outWidth + " x " + option.outHeight);
                    }
                } else {
                    if (height > maxSize) {
                        scale = height * 1.0f / maxSize;
                        option.outHeight = (int)maxSize;
                        option.outWidth /= scale;
                        Log.i(TAG, "scale 2 : " + scale + " " + option.outWidth + " x " + option.outHeight);
                    }
                }

                option.inJustDecodeBounds = false;
                option.inSampleSize = 1;
                bm = BitmapFactory.decodeFile(src, option);

                Bitmap resizeBitmap = BaseEditorManager.resize(bm, 1 / scale, 1 / scale);
                if(l != null) {
                    l.onLoadSuccess(resizeBitmap);
                }
            }
        }.start();

    }

}
