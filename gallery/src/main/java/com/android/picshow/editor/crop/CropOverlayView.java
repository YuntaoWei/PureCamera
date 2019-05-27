package com.android.picshow.editor.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CropOverlayView extends View {

    private static final int SNAP_RADIUS_DP = 6;
    private static final float DEFAULT_SHOW_GUIDELINES_LIMIT = 100;

    private static final float DEFAULT_CORNER_THICKNESS_DP = PaintUtil
            .getCornerThickness();
    private static final float DEFAULT_LINE_THICKNESS_DP = PaintUtil
            .getLineThickness();
    private static final float DEFAULT_CORNER_OFFSET_DP = (DEFAULT_CORNER_THICKNESS_DP / 2)
            - (DEFAULT_LINE_THICKNESS_DP / 2);
    private static final float DEFAULT_CORNER_EXTENSION_DP = DEFAULT_CORNER_THICKNESS_DP
            / 2 + DEFAULT_CORNER_OFFSET_DP;
    private static final float DEFAULT_CORNER_LENGTH_DP = 20;

    private static final int GUIDELINES_OFF = 0;
    private static final int GUIDELINES_ON_TOUCH = 1;
    private static final int GUIDELINES_ON = 2;

    private Paint mBorderPaint;

    private Paint mGuidelinePaint;


    private Paint mCornerPaint;

    private Paint mBackgroundPaint;

    private Rect mBitmapRect;

    private float mHandleRadius;

    private float mSnapRadius;

    private Pair<Float, Float> mTouchOffset;

    private Handle mPressedHandle;

    private boolean mFixAspectRatio = CropImageView.DEFAULT_FIXED_ASPECT_RATIO;

    private int mAspectRatioX = CropImageView.DEFAULT_ASPECT_RATIO_X;
    private int mAspectRatioY = CropImageView.DEFAULT_ASPECT_RATIO_Y;

    private float mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

    private int mGuidelines;

    private boolean initializedCropWindow = false;

    private float mCornerExtension;
    private float mCornerOffset;
    private float mCornerLength;

    private CropOperationListener cropOperationListener;

    public CropOverlayView(Context context) {
        super(context);
        init(context);
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Bitmap mBitmap = null;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        initCropWindow(mBitmapRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        drawBackground(canvas, mBitmapRect);

        if (showGuidelines()) {
            if (mGuidelines == GUIDELINES_ON) {
                drawRuleOfThirdsGuidelines(canvas);
                drawCornerText(canvas);
            } else if (mGuidelines == GUIDELINES_ON_TOUCH) {
                if (mPressedHandle != null) {
                    drawRuleOfThirdsGuidelines(canvas);
                    drawCornerText(canvas);
                }

            } else if (mGuidelines == GUIDELINES_OFF) {
            }
        }

        canvas.drawRect(Edge.LEFT.getCoordinate(), Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(), Edge.BOTTOM.getCoordinate(),
                mBorderPaint);


        drawCorners(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    private Bitmap mCornerBitmap = null;
    private int mCornerWidth = 0;
    private int mCornerHeight = 0;

    public void setBitmapSize(int width, int height) {
        this.mBitmapWidth = width;
        this.mBitmapHeight = height;
    }

    public void setOnCropOperationListener(CropOperationListener l) {
        cropOperationListener = l;
    }

    public void setCropOverlayCornerBitmap(Bitmap bitmap) {
        mCornerBitmap = bitmap;
        if (mCornerBitmap != null) {
            mCornerWidth = mCornerBitmap.getWidth();
            mCornerHeight = mCornerBitmap.getHeight();
        }
    }


    public void setBitmapRect(Rect bitmapRect) {
        mBitmapRect = bitmapRect;
        initCropWindow(mBitmapRect);
    }

    public void resetCropOverlayView() {

        if (initializedCropWindow) {
            initCropWindow(mBitmapRect);
            invalidate();
        }
    }

    public void setGuidelines(int guidelines) {
        if (guidelines < 0 || guidelines > 2)
            throw new IllegalArgumentException(
                    "Guideline value must be set between 0 and 2. See documentation.");
        else {
            mGuidelines = guidelines;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }

    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mFixAspectRatio = fixAspectRatio;

        if (initializedCropWindow) {
            initCropWindow(mBitmapRect);
            invalidate();
        }
    }

    public void setAspectRatioX(int aspectRatioX) {
        if (aspectRatioX <= 0)
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }

    public void setAspectRatioY(int aspectRatioY) {
        if (aspectRatioY <= 0)
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }

    public void setInitialAttributeValues(int guidelines,
                                          boolean fixAspectRatio, int aspectRatioX, int aspectRatioY) {
        if (guidelines < 0 || guidelines > 2)
            throw new IllegalArgumentException(
                    "Guideline value must be set between 0 and 2. See documentation.");
        else
            mGuidelines = guidelines;

        mFixAspectRatio = fixAspectRatio;

        if (aspectRatioX <= 0)
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

        if (aspectRatioY <= 0)
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

    }


    private void init(Context context) {

        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();

        mHandleRadius = HandleUtil.getTargetRadius(context);

        mSnapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SNAP_RADIUS_DP, displayMetrics);

        mBorderPaint = PaintUtil.newBorderPaint(context);
        mGuidelinePaint = PaintUtil.newGuidelinePaint();
        mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
        mCornerPaint = PaintUtil.newCornerPaint(context);

        mCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_OFFSET_DP, displayMetrics);
        mCornerExtension = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CORNER_EXTENSION_DP,
                displayMetrics);
        mCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_LENGTH_DP, displayMetrics);

        mGuidelines = CropImageView.DEFAULT_GUIDELINES;

    }

    private void initCropWindow(Rect bitmapRect) {

        if (initializedCropWindow == false)
            initializedCropWindow = true;

        if (mFixAspectRatio) {

            if (AspectRatioUtil.calculateAspectRatio(bitmapRect) > mTargetAspectRatio) {

                Edge.TOP.setCoordinate(bitmapRect.top);
                Edge.BOTTOM.setCoordinate(bitmapRect.bottom);

                final float centerX = getWidth() / 2f;

                final float cropWidth = Math
                        .max(Edge.MIN_CROP_LENGTH_PX, AspectRatioUtil
                                .calculateWidth(Edge.TOP.getCoordinate(),
                                        Edge.BOTTOM.getCoordinate(),
                                        mTargetAspectRatio));

                if (cropWidth == Edge.MIN_CROP_LENGTH_PX)
                    mTargetAspectRatio = (Edge.MIN_CROP_LENGTH_PX)
                            / (Edge.BOTTOM.getCoordinate() - Edge.TOP
                            .getCoordinate());

                final float halfCropWidth = cropWidth / 2f;
                Edge.LEFT.setCoordinate(centerX - halfCropWidth);
                Edge.RIGHT.setCoordinate(centerX + halfCropWidth);

            } else {

                Edge.LEFT.setCoordinate(bitmapRect.left);
                Edge.RIGHT.setCoordinate(bitmapRect.right);

                final float centerY = getHeight() / 2f;

                final float cropHeight = Math
                        .max(Edge.MIN_CROP_LENGTH_PX, AspectRatioUtil
                                .calculateHeight(Edge.LEFT.getCoordinate(),
                                        Edge.RIGHT.getCoordinate(),
                                        mTargetAspectRatio));

                if (cropHeight == Edge.MIN_CROP_LENGTH_PX)
                    mTargetAspectRatio = (Edge.RIGHT.getCoordinate() - Edge.LEFT
                            .getCoordinate()) / Edge.MIN_CROP_LENGTH_PX;

                final float halfCropHeight = cropHeight / 2f;
                Edge.TOP.setCoordinate(centerY - halfCropHeight);
                Edge.BOTTOM.setCoordinate(centerY + halfCropHeight);
            }

        } else {

            final float horizontalPadding = 0.1f * bitmapRect.width();
            final float verticalPadding = 0.1f * bitmapRect.height();

            Edge.LEFT.setCoordinate(bitmapRect.left + horizontalPadding);
            Edge.TOP.setCoordinate(bitmapRect.top + verticalPadding);
            Edge.RIGHT.setCoordinate(bitmapRect.right - horizontalPadding);
            Edge.BOTTOM.setCoordinate(bitmapRect.bottom - verticalPadding);
        }
    }

    public static boolean showGuidelines() {
        if ((Math.abs(Edge.LEFT.getCoordinate() - Edge.RIGHT.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)
                || (Math.abs(Edge.TOP.getCoordinate()
                - Edge.BOTTOM.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT))
            return false;
        else
            return true;
    }

    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

    private void drawBackground(Canvas canvas, Rect bitmapRect) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        canvas.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, top,
                mBackgroundPaint);
        canvas.drawRect(bitmapRect.left, bottom, bitmapRect.right,
                bitmapRect.bottom, mBackgroundPaint);
        canvas.drawRect(bitmapRect.left, top, left, bottom, mBackgroundPaint);
        canvas.drawRect(right, top, bitmapRect.right, bottom, mBackgroundPaint);
    }

    private void drawCornerText(Canvas canvas) {

        float displayedImageWidth = mBitmapRect.width();
        float displayedImageHeight = mBitmapRect.height();

        float scaleFactorWidth = mBitmapWidth / displayedImageWidth;
        float scaleFactorHeight = mBitmapHeight / displayedImageHeight;

        int actualCropWidth = (int) (Edge.getWidth() * scaleFactorWidth);
        int actualCropHeight = (int) (Edge.getHeight() * scaleFactorHeight);

        mBorderPaint.setARGB(255, 255, 255, 255);
        mBorderPaint.setStrokeWidth(0);
        mBorderPaint.setTextAlign(Align.CENTER);
        mBorderPaint.setTextSize(25);
        canvas.drawText(
                actualCropWidth + "x" + actualCropHeight,
                Edge.LEFT.getCoordinate() / 2 + +Edge.RIGHT.getCoordinate() / 2,
                Edge.TOP.getCoordinate() / 2 + Edge.BOTTOM.getCoordinate() / 2,
                mBorderPaint);

    }

    private void drawCorners(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();


        if (mCornerBitmap != null) {
            canvas.drawBitmap(mCornerBitmap, left - mCornerWidth / 2, top
                    - mCornerHeight / 2, null);

            canvas.drawBitmap(mCornerBitmap, right - mCornerWidth / 2, top
                    - mCornerHeight / 2, null);

            canvas.drawBitmap(mCornerBitmap, left - mCornerWidth / 2, bottom
                    - mCornerHeight / 2, null);

            canvas.drawBitmap(mCornerBitmap, right - mCornerWidth / 2, bottom
                    - mCornerHeight / 2, null);

        } else {
            canvas.drawLine(left - mCornerOffset, top - mCornerExtension, left
                    - mCornerOffset, top + mCornerLength, mCornerPaint);
            canvas.drawLine(left, top - mCornerOffset, left + mCornerLength,
                    top - mCornerOffset, mCornerPaint);

            canvas.drawLine(right + mCornerOffset, top - mCornerExtension,
                    right + mCornerOffset, top + mCornerLength, mCornerPaint);
            canvas.drawLine(right, top - mCornerOffset, right - mCornerLength,
                    top - mCornerOffset, mCornerPaint);

            canvas.drawLine(left - mCornerOffset, bottom + mCornerExtension,
                    left - mCornerOffset, bottom - mCornerLength, mCornerPaint);
            canvas.drawLine(left, bottom + mCornerOffset, left + mCornerLength,
                    bottom + mCornerOffset, mCornerPaint);

            canvas.drawLine(right + mCornerOffset, bottom + mCornerExtension,
                    right + mCornerOffset, bottom - mCornerLength, mCornerPaint);
            canvas.drawLine(right, bottom + mCornerOffset, right
                    - mCornerLength, bottom + mCornerOffset, mCornerPaint);
        }

    }

    private void onActionDown(float x, float y) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right,
                bottom, mHandleRadius);

        if (mPressedHandle == null)
            return;

        mTouchOffset = HandleUtil.getOffset(mPressedHandle, x, y, left, top,
                right, bottom);

        invalidate();
    }

    public boolean shouldDoCrop(float x, float y) {
        return (Edge.LEFT.getCoordinate() + 10 < x && x < Edge.RIGHT.getCoordinate() - 10) &&
                (Edge.TOP.getCoordinate() + 10 < y && y < Edge.BOTTOM.getCoordinate() - 10);
    }

    private void onActionUp(float x, float y) {

        if (mPressedHandle == null)
            return;

        mPressedHandle = null;

        if(shouldDoCrop(x, y) && cropOperationListener != null)
            cropOperationListener.onCrop();

        invalidate();
    }

    private void onActionMove(float x, float y) {

        if (mPressedHandle == null)
            return;

        x += mTouchOffset.first;
        y += mTouchOffset.second;

        if (mFixAspectRatio) {
            mPressedHandle.updateCropWindow(x, y, mTargetAspectRatio,
                    mBitmapRect, mSnapRadius);
        } else {
            mPressedHandle.updateCropWindow(x, y, mBitmapRect, mSnapRadius);
        }
        invalidate();
    }
}
