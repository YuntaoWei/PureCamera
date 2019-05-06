package com.pure.camera.opengl.data;

import com.pure.camera.common.LogPrinter;

public class SmallPreviewRect {

    public float left;
    public float top;
    public float right;
    public float bottom;

    public SmallPreviewRect(float left, float top, float right, float bottom) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public boolean isCovered(float x, float y) {
        return (left <= x && x <= right) && (bottom <= y && y <= top);
    }

    @Override
    public String toString() {
        return left + " " + right + "  " + top + "  " + bottom;
    }
}
