package com.android.picshow.editor.scrawl;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.picshow.editor.utils.FileUtils;


public class ScrawlTools {
    private DrawingBoardView drawView;
    private Context context;

    private int mBrushColor;


    public ScrawlTools(Context context, DrawingBoardView drawView,
                       Bitmap bitmap) {
        this.drawView = drawView;
        this.context = context;
        drawView.setBackgroundBitmap(bitmap);
    }

    public void creatDrawPainter(DrawAttribute.DrawStatus drawStatus,
                                 Bitmap paintBitmap, int color) {
        drawView.setBrushBitmap(drawStatus, paintBitmap, color);
    }


    public void creatDrawPainter(DrawAttribute.DrawStatus drawStatus,
                                 PaintBrush paintBrush) {
        int color = paintBrush.getPaintColor();
        int size = paintBrush.getPaintSize();
        int num = paintBrush.getPaintSizeTypeNo();
        Bitmap bitmap = paintBrush.getPaintBitmap();

        Bitmap paintBitmap = FileUtils.ResizeBitmap(bitmap, num - (size - 1));
        drawView.setBrushBitmap(drawStatus, paintBitmap, color);
    }


    public void creatStampPainter(DrawAttribute.DrawStatus drawStatus, int[] res, int color) {
        drawView.setStampBitmaps(drawStatus, res, color);
    }


    public Bitmap getBitmap() {
        return drawView.getDrawBitmap();
    }

    public int getBrushColor() {
        return mBrushColor;
    }

    public void setBrushColor(int mBrushColor) {
        this.mBrushColor = mBrushColor;
    }
}
