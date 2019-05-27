package com.android.picshow.editor.filters;


import android.graphics.Bitmap;

@SuppressWarnings("JniMissingFunction")
public class NativeFilter {
    static {
        System.loadLibrary("nativefilter");
    }

    public native String test();


    public native int[] gray(int[] pixels, int width, int height, float factor);


    public native int[] mosatic(int[] pixels, int width, int height,
                                int factor);

    public native int[] lomo(int[] pixels, int width, int height, float factor);


    public native int[] nostalgic(int[] pixels, int width, int height,
                                  float factor);

    public native int[] comics(int[] pixels, int width, int height,
                               float factor);

    public native int[] brown(int[] pixels, int width, int height,
                              float factor);

    public native int[] sketchPencil(int[] pixels, int width, int height,
                                     float factor);

    public native int[] neon(int[] pixels, int width, int height);

    public static Bitmap neon(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r,g,b,r1,g1,b1,r2,g2,b2;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for(int y = 0; y < h - 1; y++){
            for(int x = 0; x < w - 1; x++){
                color = oldPx[x+y*w];

                r = (color >> 16) & 0xFF;
                g = (color >> 8) & 0xFF;
                b = (color >> 0) & 0xFF;

                int newcolor = oldPx[x+1+y*w];

                r1 = (newcolor >> 16) & 0xFF;
                g1 = (newcolor >> 8) & 0xFF;
                b1 = (newcolor >> 0) & 0xFF;

                int newcolor2 = oldPx[x+(y+1)*w];

                r2 = (newcolor2 >> 16) & 0xFF;
                g2 = (newcolor2 >> 8) & 0xFF;
                b2 = (newcolor2 >> 0) & 0xFF;

                int tr = (int) (2* Math.sqrt(((r-r1)*(r-r1)+(r-r2)*(r-r2))));
                int tg = (int) (2* Math.sqrt(((g-g1)*(g-g1)+(g-g2)*(g-g2))));
                int tb = (int) (2* Math.sqrt(((b-b1)*(b-b1)+(b-b2)*(b-b2))));

                newPx[x+y*w] = (255 << 24)|(tr << 16)|(tg << 8)|(tb);
            }
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);
        return resultBitmap;
    }
}
