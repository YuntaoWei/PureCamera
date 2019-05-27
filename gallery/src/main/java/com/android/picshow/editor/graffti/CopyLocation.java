package com.android.picshow.editor.graffti;

import android.graphics.Canvas;
import android.graphics.Paint;

import static com.android.picshow.editor.graffti.DrawUtil.drawCircle;
import static com.android.picshow.editor.graffti.DrawUtil.rotatePointInGraffiti;


public class CopyLocation {

    private float mCopyStartX, mCopyStartY;
    private float mTouchStartX, mTouchStartY;
    private float mX, mY;

    private Paint mPaint;

    private boolean mIsRelocating = true;
    private boolean mIsCopying = false;

    public CopyLocation(float copyStartX, float copyStartY, float touchStartX, float touchStartY) {
        this.mCopyStartX = copyStartX;
        this.mCopyStartY = copyStartY;
        this.mTouchStartX = touchStartX;
        this.mTouchStartY = touchStartY;
    }

    public CopyLocation(float x, float y) {
        mX = x;
        mY = y;
        mTouchStartX = x;
        mTouchStartY = y;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }


    public float getTouchStartX() {
        return mTouchStartX;
    }

    public float getTouchStartY() {
        return mTouchStartY;
    }

    public float getCopyStartX() {
        return mCopyStartX;
    }

    public float getCopyStartY() {
        return mCopyStartY;
    }

    public boolean isCopying() {
        return mIsCopying;
    }

    public boolean isRelocating() {
        return mIsRelocating;
    }

    public void setCopying(boolean copying) {
        mIsCopying = copying;
    }

    public void setRelocating(boolean relocating) {
        mIsRelocating = relocating;
    }

    public void updateLocation(float x, float y) {
        mX = x;
        mY = y;
    }

    public void setStartPosition(float x, float y) {
        mCopyStartX = mX;
        mCopyStartY = mY;
        mTouchStartX = x;
        mTouchStartY = y;
    }

    public void drawItSelf(Canvas canvas, float mPaintSize) {
        mPaint.setStrokeWidth(mPaintSize / 4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xaa666666);
        drawCircle(canvas, mX, mY, mPaintSize / 2 + mPaintSize / 8, mPaint);

        mPaint.setStrokeWidth(mPaintSize / 16);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xaaffffff);
        drawCircle(canvas, mX, mY, mPaintSize / 2 + mPaintSize / 32, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        if (!mIsCopying) {
            mPaint.setColor(0x44ff0000);
            drawCircle(canvas, mX, mY, mPaintSize / 2, mPaint);
        } else {
            mPaint.setColor(0x44000088);
            drawCircle(canvas, mX, mY, mPaintSize / 2, mPaint);
        }
    }


    public boolean isInIt(float x, float y, float mPaintSize) {
        if ((mX - x) * (mX - x) + (mY - y) * (mY - y) <= mPaintSize * mPaintSize) {
            return true;
        }
        return false;
    }

    public CopyLocation copy() {
        CopyLocation copyLocation = new CopyLocation(mCopyStartX, mCopyStartY, mTouchStartX, mTouchStartY);
        return copyLocation;
    }

    public void rotatePosition(int originalDegree, int mRotateDegree, float mOriginalPivotX, float mOriginalPivotY) {

        float[] coords = rotatePointInGraffiti(mRotateDegree, originalDegree, this.mX,
                this.mY, mOriginalPivotX, mOriginalPivotY);
        this.mX = coords[0];
        this.mY = coords[1];

        coords = rotatePointInGraffiti(mRotateDegree, originalDegree, this.mCopyStartX,
                this.mCopyStartY, mOriginalPivotX, mOriginalPivotY);
        this.mCopyStartX = coords[0];
        this.mCopyStartY = coords[1];

        coords = rotatePointInGraffiti(mRotateDegree, originalDegree, this.mTouchStartX,
                this.mTouchStartY, mOriginalPivotX, mOriginalPivotY);
        this.mTouchStartX = coords[0];
        this.mTouchStartY = coords[1];
    }

}

