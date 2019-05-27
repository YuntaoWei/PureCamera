package com.android.picshow.editor.graffti;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import static com.android.picshow.editor.graffti.DrawUtil.GRAFFITI_PIXEL_UNIT;
import static com.android.picshow.editor.graffti.DrawUtil.restoreRotatePointInGraffiti;
import static com.android.picshow.editor.graffti.DrawUtil.rotatePoint;
import static com.android.picshow.editor.graffti.DrawUtil.rotatePointInGraffiti;

public abstract class GraffitiSelectableItem implements Undoable {

    public final static int ITEM_CAN_ROTATE_BOUND = 80;

    private GraffitiView.Pen mPen;
    private float mSize;
    private GraffitiColor mColor;
    private float mItemRotate;
    private int mGraffitiDegree;
    float mPivotX, mPivotY;
    private float mX, mY;

    private Rect mRect = new Rect();

    public GraffitiSelectableItem(GraffitiView.Pen pen, float size, GraffitiColor color, int textRotate, int rotateDegree, float x, float y, float px, float py) {
        this.mPen = pen;
        this.mSize = size;
        this.mColor = color;
        this.mItemRotate = textRotate;
        this.mGraffitiDegree = rotateDegree;
        this.mX = x;
        this.mY = y;
        this.mPivotX = px;
        this.mPivotY = py;

        resetBounds(mRect);
    }

    public Rect getBounds() {
        return mRect;
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        mSize = size;
        resetBounds(mRect);
    }

    public void setXy(int currentRotate, float x, float y) {
        float[] xy = restoreRotatePointInGraffiti(currentRotate, mGraffitiDegree, x, y, mPivotX, mPivotY);
        mX = xy[0];
        mY = xy[1];
    }

    public float[] getXy(int currentDegree) {
        return rotatePointInGraffiti(currentDegree, mGraffitiDegree, mX, mY, mPivotX, mPivotY);
    }

    public GraffitiColor getColor() {
        return mColor;
    }

    public void setColor(GraffitiColor color) {
        mColor = color;
    }

    public Rect getBounds(int currentRotate) {
        return mRect;
    }

    public void setItemRotate(float textRotate) {
        mItemRotate = textRotate;
    }

    public float getItemRotate() {
        return mItemRotate;
    }

    public int getGraffitiRotate() {
        return mGraffitiDegree;
    }


    public boolean isInIt(int currentRotate, float x, float y, GraffitiView.Pen pen) {
        if (pen != mPen) {
            return false;
        }
        float[] xy = getXy(currentRotate);
        x = x - xy[0];
        y = y - xy[1];
        float[] rectXy = rotatePoint((int) -(currentRotate - mGraffitiDegree + mItemRotate), x, y, 0, 0);
        return mRect.contains((int) rectXy[0], (int) rectXy[1]);
    }

    public boolean isCanRotate(int currentRotate, float x, float y) {
        float[] xy = getXy(currentRotate);
        x = x - xy[0];
        y = y - xy[1];
        float[] rectXy = rotatePoint((int) -(currentRotate - mGraffitiDegree + mItemRotate), x, y, 0, 0);

        return rectXy[0] >= mRect.right && rectXy[0] <= mRect.right + ITEM_CAN_ROTATE_BOUND * GRAFFITI_PIXEL_UNIT
                && rectXy[1] >= mRect.top && rectXy[1] <= mRect.bottom;
    }

    public abstract void resetBounds(Rect rect);

    public abstract void draw(Canvas canvas, GraffitiView graffitiView, Paint paint);

    @Override
    public int getType() {
        return 0;
    }
}
