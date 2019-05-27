package com.android.picshow.editor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.android.picshow.editor.filters.FilterType;
import com.android.picshow.editor.filters.Filters;
import com.android.picshow.editor.filters.NativeFilter;
import com.android.picshow.editor.filters.NewFilters;
import com.android.picshow.utils.LogPrinter;


public class ImageUtils {

    public interface ThumbLoadListener {

        void onLoadSuccess(Bitmap bm, int filterType);

        void onLoadFailed(int filterType);
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static void loadNewFilterThumb(final Context ctx, final Bitmap bm, final int width, final int height, final ThumbLoadListener l) {

        new Thread() {

            @Override
            public void run() {
                Bitmap scaleImage = Bitmap.createScaledBitmap(bm,
                        width, height, true);
                int w = scaleImage.getWidth();
                int h = scaleImage.getHeight();

                LogPrinter.i("ImageUtils",w + " x " + h);

                int[] filters = NewFilters.FilterType.getSupportFilters();
                for (int type : filters
                     ) {

                    NewFilters newFilters = NewFilters.getInstance(ctx);
                    Bitmap r = newFilters.filterImage(ctx, bm, type);

                    if(l != null) {
                        if(r != null)
                            l.onLoadSuccess(r, type);
                        else
                            l.onLoadFailed(type);
                    }
                }

            }

        }.start();

    }

    public static void loadFilterThumb(final Bitmap bm, final int width, final int height, final ThumbLoadListener l) {

        /*int w = bm.getWidth();
        int h = bm.getHeight();

        float xScale = BaseEditorManager.FILTER_THUMB_SIZE / w;
        float yScale = BaseEditorManager.FILTER_THUMB_SIZE / h;


        Matrix m = new Matrix();
        m.postScale(xScale , yScale);*/

        new Thread() {

            @Override
            public void run() {
                Bitmap scaleImage = Bitmap.createScaledBitmap(bm,
                        width, height, true);

                int w = scaleImage.getWidth();
                int h = scaleImage.getHeight();

                LogPrinter.i("ImageUtils",w + " x " + h);

                int[] filters = FilterType.getAllAvailableFilters();

                Filters f = null;

                for (int type : filters
                        ) {
                    Bitmap r = null;
                    if(type == FilterType.FILTER4NiHong) {
                        r = NativeFilter.neon(scaleImage);
                    } else {
                        f = new Filters(scaleImage.copy(scaleImage.getConfig(), true), type);
                        r = f.process(1);
                    }

                    if(l != null) {
                        if(r != null)
                            l.onLoadSuccess(r, type);
                        else
                            l.onLoadFailed(type);
                    }

                }
            }

        }.start();

    }
}
