package com.pure.camera.filter.engine;

public class NativeFilter {

    static {
        System.loadLibrary("camera_filter");
    }

    private static NativeFilter INSTANCE;

    private NativeFilter() {}

    static NativeFilter getInstance() {
        if(null == INSTANCE) {
            INSTANCE = new NativeFilter();
        }

        return INSTANCE;
    }

    public native byte[] doFilterGray(byte[] pixels, int w, int h);
    public native byte[] doFilterMosaic(byte[] pixels, int w, int h, int square);
    public native byte[] doFilterRelief(byte[] pixels, int w, int h);

}
