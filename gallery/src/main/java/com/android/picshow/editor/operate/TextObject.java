package com.android.picshow.editor.operate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TextObject extends ImageObject {

    private int textSize = 90;
    private int color = Color.BLACK;
    private String typeface;
    private String text;
    private boolean bold = false;
    private boolean italic = false;
    private Context context;

    Paint paint = new Paint();

    public TextObject(Context context, String text, int x, int y,
                      Bitmap rotateBm, Bitmap deleteBm) {
        super(text);
        this.context = context;
        this.text = text;
        mPoint.x = x;
        mPoint.y = y;
        this.rotateBm = rotateBm;
        this.deleteBm = deleteBm;
        regenerateBitmap();
    }

    public TextObject() {
    }

    public void regenerateBitmap() {
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setTypeface(getTypefaceObj());
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);
        paint.setFlags(Paint.SUBPIXEL_TEXT_FLAG);
        String lines[] = text.split("\n");

        int textWidth = 0;
        for (String str : lines) {
            int temp = (int) paint.measureText(str);
            if (temp > textWidth)
                textWidth = temp;
        }
        if (textWidth < 1)
            textWidth = 1;
        if (srcBm != null)
            srcBm.recycle();
        srcBm = Bitmap.createBitmap(textWidth, textSize * (lines.length) + 8,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(srcBm);
        canvas.drawARGB(0, 0, 0, 0);
        for (int i = 1; i <= lines.length; i++) {
            canvas.drawText(lines[i - 1], 0, i * textSize, paint);
        }
        setCenter();
    }

    public Typeface getTypefaceObj() {
        Typeface tmptf = Typeface.DEFAULT;
        if (typeface != null) {
            if (OperateConstants.FACE_BY.equals(typeface)
                    || OperateConstants.FACE_BYGF.equals(typeface)) {
                tmptf = Typeface.createFromAsset(context.getAssets(), "fonts/"
                        + typeface + ".ttf");
            }
        }
        if (bold && !italic)
            tmptf = Typeface.create(tmptf, Typeface.BOLD);
        if (italic && !bold)
            tmptf = Typeface.create(tmptf, Typeface.ITALIC);
        if (italic && bold)
            tmptf = Typeface.create(tmptf, Typeface.BOLD_ITALIC);
        return tmptf;
    }

    public void commit() {
        regenerateBitmap();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTypeface() {
        return typeface;
    }

    public void setTypeface(String typeface) {
        this.typeface = typeface;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public int getX() {
        return mPoint.x;
    }

    public void setX(int x) {
        this.mPoint.x = x;
    }

    public int getY() {
        return mPoint.y;
    }

    public void setY(int y) {
        this.mPoint.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
