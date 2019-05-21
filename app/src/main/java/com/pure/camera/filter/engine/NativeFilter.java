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

    public native boolean doFilterGray(byte[] pixels, int w, int h, int orientation, String savePath);
    public native boolean doFilterMosaic(byte[] pixels, int w, int h, int square, int orientation, String savePath);
    public native boolean doFilterRelief(byte[] pixels, int w, int h, int orientation, String savePath);
    public native boolean doFilterWB(byte[] pixels, int w, int h, int orientation, String savePath);
    public native boolean doFilterPositive(byte[] pixels, int w, int h, int orientation, String savePath);

    public native boolean doYuv2RGB(byte[] yuv, int w, int h, int orientation, String savePath);

}
