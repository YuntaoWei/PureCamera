package com.android.picshow.editor.warp;


@SuppressWarnings("JniMissingFunction")
public class Picwarp {
    static {
        System.loadLibrary("picwarp");
    }

    public native int initArray();

    public native int[] warpPhotoFromC(int[] image, int height, int width,
                                       double max_dist, double orig_x, double orig_y, double cur_x,
                                       double cur_y);
}
