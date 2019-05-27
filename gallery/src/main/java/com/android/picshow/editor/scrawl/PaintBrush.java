package com.android.picshow.editor.scrawl;

import android.graphics.Bitmap;

public class PaintBrush {
    private Bitmap paintBitmap;

    private int paintSize;

    private int paintColor;

    private int paintSizeTypeNo;


    public Bitmap getPaintBitmap() {
        return paintBitmap;
    }

    public void setPaintBitmap(Bitmap paintBitmap) {
        this.paintBitmap = paintBitmap;
    }

    public int getPaintSize() {
        return paintSize;
    }

    public void setPaintSize(int paintSize) {
        if (paintSize >= paintSizeTypeNo) {
            this.paintSize = paintSizeTypeNo;
        } else if (paintSize <= 0) {
            this.paintSize = 1;
        } else {
            this.paintSize = paintSize;
        }

    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public int getPaintSizeTypeNo() {
        return paintSizeTypeNo;
    }

    public void setPaintSizeTypeNo(int paintSizeTypeNo) {
        this.paintSizeTypeNo = paintSizeTypeNo;
    }

}
