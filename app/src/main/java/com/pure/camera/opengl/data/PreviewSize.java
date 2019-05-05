package com.pure.camera.opengl.data;

public class PreviewSize {
    public int width;
    public int height;

    public PreviewSize(int w, int h) {
        width = w;
        height = h;
    }

    public float getAspectRatio() {
        return (float) width / (float) height;
    }

    public float getAspectRatioReverse() {
        return (float) height / (float) width;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
