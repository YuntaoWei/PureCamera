package com.android.picshow.editor.graffti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.Util;

import static com.android.picshow.editor.graffti.DrawUtil.GRAFFITI_PIXEL_UNIT;
import static com.android.picshow.editor.graffti.DrawUtil.computeAngle;
import static com.android.picshow.editor.graffti.DrawUtil.drawArrow;
import static com.android.picshow.editor.graffti.DrawUtil.drawCircle;
import static com.android.picshow.editor.graffti.DrawUtil.drawLine;
import static com.android.picshow.editor.graffti.DrawUtil.drawRect;


public class GraffitiView extends View {

    public enum Pen {
        HAND,
        COPY,
        ERASER,
        TEXT,
        BITMAP
    }

    public enum Shape {
        HAND_WRITE,
        ARROW,
        LINE,
        FILL_CIRCLE,
        HOLLOW_CIRCLE,
        FILL_RECT,
        HOLLOW_RECT,
    }

    public static final int ERROR_INIT = -1;
    public static final int ERROR_SAVE = -2;

    private static final float VALUE = 1f;

    private GraffitiListener mGraffitiListener;

    private Bitmap mBitmap;
    private Bitmap mBitmapEraser;
    private Bitmap mGraffitiBitmap;
    private Canvas mBitmapCanvas;

    private int mOriginalWidth, mOriginalHeight;
    private float mOriginalPivotX, mOriginalPivotY;

    private float mPrivateScale;
    private int mPrivateHeight, mPrivateWidth;
    private float mCentreTranX, mCentreTranY;

    private float mScale = 1;
    private float mTransX = 0, mTransY = 0;


    private BitmapShader mBitmapShader;
    private BitmapShader mBitmapShaderEraser;
    private Path mCurrPath;
    private Path mTempPath;
    private CopyLocation mCopyLocation;

    private Paint mPaint;
    private int mTouchMode;
    private float mPaintSize;
    private GraffitiColor mColor;

    private boolean mIsPainting = false;
    private boolean isJustDrawOriginal;

    private boolean mIsDrawableOutside = false;
    private boolean mEraserImageIsResizeable;
    private boolean mReady = false;


    private CopyOnWriteArrayList<Undoable> mUndoStack = new CopyOnWriteArrayList<Undoable>();
    private CopyOnWriteArrayList<GraffitiPath> mPathStack = new CopyOnWriteArrayList<GraffitiPath>();
    private CopyOnWriteArrayList<GraffitiSelectableItem> mSelectableStack = new CopyOnWriteArrayList<>();

    private Pen mPen;
    private Shape mShape;

    private float mTouchDownX, mTouchDownY, mLastTouchX, mLastTouchY, mTouchX, mTouchY;
    private Matrix mShaderMatrix;
    private Matrix mShaderMatrixEraser;
    private Matrix mShaderMatrixColor;

    private float mAmplifierRadius;
    private Path mAmplifierPath;
    private float mAmplifierScale = 0;
    private Paint mAmplifierPaint;
    private int mAmplifierHorizonX;

    private GraffitiSelectableItem mSelectedItem;

    private float mSelectedItemX, mSelectedItemY;
    private boolean mIsRotatingSelectedItem;
    private float mRotateTextDiff;

    public GraffitiView(Context context, Bitmap bitmap, GraffitiListener listener) {
        this(context, bitmap, null, true, listener);
    }

    public GraffitiView(Context context, Bitmap bitmap, String eraser, boolean eraserImageIsResizeable, GraffitiListener listener) {
        super(context);

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mBitmap = bitmap;
        mGraffitiListener = listener;
        if (mGraffitiListener == null) {
            throw new RuntimeException("GraffitiListener is null!!!");
        }
        if (mBitmap == null) {
            throw new RuntimeException("Bitmap is null!!!");
        }

        if (eraser != null) {
            mBitmapEraser = ImageUtils.createBitmapFromPath(eraser, getContext());
        }
        mEraserImageIsResizeable = eraserImageIsResizeable;

        mOriginalWidth = mBitmap.getWidth();
        mOriginalHeight = mBitmap.getHeight();
        mOriginalPivotX = mOriginalWidth / 2f;
        mOriginalPivotY = mOriginalHeight / 2f;

        init();

    }

    public void init() {

        mScale = 1f;
        mColor = new GraffitiColor(Color.RED);
        mPaint = new Paint();
        mPaint.setStrokeWidth(mPaintSize);
        mPaint.setColor(mColor.getColor());
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPen = Pen.HAND;
        mShape = Shape.HAND_WRITE;


        mShaderMatrix = new Matrix();
        mShaderMatrixEraser = new Matrix();
        mTempPath = new Path();
        mCopyLocation = new CopyLocation(150, 150);

        mShaderMatrixColor = new Matrix();

        mAmplifierPaint = new Paint();
        mAmplifierPaint.setColor(0xaaffffff);
        mAmplifierPaint.setStyle(Paint.Style.STROKE);
        mAmplifierPaint.setAntiAlias(true);
        mAmplifierPaint.setStrokeJoin(Paint.Join.ROUND);
        mAmplifierPaint.setStrokeCap(Paint.Cap.ROUND);
        mAmplifierPaint.setStrokeWidth(Util.dp2px(getContext(), 10));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBG();
        if (!mReady) {
            mCopyLocation.updateLocation(toX(w / 2), toY(h / 2));
            mGraffitiListener.onReady();
            mReady = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchMode = 1;
                mTouchDownX = mTouchX = mLastTouchX = event.getX();
                mTouchDownY = mTouchY = mLastTouchY = event.getY();

                if (isPenSelectable()) {
                    mIsRotatingSelectedItem = false;
                    if (mSelectedItem != null) {
                        if (mSelectedItem.isCanRotate(mGraffitiRotateDegree, toX(mTouchX), toY(mTouchY))) {
                            mIsRotatingSelectedItem = true;
                            float[] xy = mSelectedItem.getXy(mGraffitiRotateDegree);
                            mRotateTextDiff = mSelectedItem.getItemRotate() -
                                    computeAngle(xy[0], xy[1], toX(mTouchX), toY(mTouchY));
                        }
                    }
                    if (!mIsRotatingSelectedItem) {
                        boolean found = false;
                        GraffitiSelectableItem item;
                        for (int i = mSelectableStack.size() - 1; i >= 0; i--) {
                            item = mSelectableStack.get(i);
                            if (item.isInIt(mGraffitiRotateDegree, toX(mTouchX), toY(mTouchY), mPen)) {
                                found = true;
                                mSelectedItem = item;
                                float[] xy = item.getXy(mGraffitiRotateDegree);
                                mSelectedItemX = xy[0];
                                mSelectedItemY = xy[1];
                                mGraffitiListener.onSelectedItem(mSelectedItem, true);
                                break;
                            }
                        }
                        if (!found) {
                            if (mSelectedItem != null) {
                                GraffitiSelectableItem old = mSelectedItem;
                                mSelectedItem = null;
                                mGraffitiListener.onSelectedItem(old, false);
                            } else {
                                mGraffitiListener.onCreateSelectableItem(mPen, toX(mTouchX), toY(mTouchY));
                            }
                        }
                    }
                } else {
                    if (mPen == Pen.COPY && mCopyLocation.isInIt(toX(mTouchX), toY(mTouchY), mPaintSize)) {
                        mCopyLocation.setRelocating(true);
                        mCopyLocation.setCopying(false);
                    } else {
                        if (mPen == Pen.COPY) {
                            if (!mCopyLocation.isCopying()) {
                                mCopyLocation.setStartPosition(toX(mTouchX), toY(mTouchY));
                                resetMatrix();
                            }
                            mCopyLocation.setCopying(true);
                        }
                        mCopyLocation.setRelocating(false);
                        mCurrPath = new Path();
                        mCurrPath.moveTo(toX(mTouchDownX), toY(mTouchDownY));
                        if (mShape == Shape.HAND_WRITE) {

                        } else {

                        }
                        mIsPainting = true;
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchMode = 0;
                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = event.getX();
                mTouchY = event.getY();

                if (mTouchDownX == mTouchX && mTouchDownY == mTouchY & mTouchDownX == mLastTouchX && mTouchDownY == mLastTouchY) {
                    mTouchX += VALUE;
                    mTouchY += VALUE;
                }

                if (isPenSelectable()) {
                    mIsRotatingSelectedItem = false;
                } else {
                    if (mIsPainting) {
                        if (mPen == Pen.COPY) {
                            if (mCopyLocation.isRelocating()) {
                                mCopyLocation.updateLocation(toX(mTouchX), toY(mTouchY));
                                mCopyLocation.setRelocating(false);
                            } else {
                                mCopyLocation.updateLocation(mCopyLocation.getCopyStartX() + toX(mTouchX) - mCopyLocation.getTouchStartX(),
                                        mCopyLocation.getCopyStartY() + toY(mTouchY) - mCopyLocation.getTouchStartY());
                            }
                        }

                        GraffitiPath path = null;

                        if (mShape == Shape.HAND_WRITE) {
                            mCurrPath.quadTo(
                                    toX(mLastTouchX),
                                    toY(mLastTouchY),
                                    toX((mTouchX + mLastTouchX) / 2),
                                    toY((mTouchY + mLastTouchY) / 2));
                            path = GraffitiPath.toPath(mPen, mShape, mPaintSize, mColor.copy(), mCurrPath, mGraffitiRotateDegree, mOriginalPivotX, mOriginalPivotY,
                                    getCopyLocation());
                        } else {
                            path = GraffitiPath.toShape(mPen, mShape, mPaintSize, mColor.copy(),
                                    toX(mTouchDownX), toY(mTouchDownY), toX(mTouchX), toY(mTouchY), mGraffitiRotateDegree, mOriginalPivotX, mOriginalPivotY,
                                    getCopyLocation());
                        }
                        addPath(path);
                        mIsPainting = false;
                    }
                }

                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode < 2) {
                    mLastTouchX = mTouchX;
                    mLastTouchY = mTouchY;
                    mTouchX = event.getX();
                    mTouchY = event.getY();

                    if (isPenSelectable()) {
                        if (mIsRotatingSelectedItem) {
                            float[] xy = mSelectedItem.getXy(mGraffitiRotateDegree);
                            mSelectedItem.setItemRotate(mRotateTextDiff + computeAngle(
                                    xy[0], xy[1], toX(mTouchX), toY(mTouchY)
                            ));
                        } else {
                            if (mSelectedItem != null) {
                                mSelectedItem.setXy(mGraffitiRotateDegree,
                                        mSelectedItemX + toX(mTouchX) - toX(mTouchDownX),
                                        mSelectedItemY + toY(mTouchY) - toY(mTouchDownY));
                            }
                        }
                    } else {
                        if (mPen == Pen.COPY && mCopyLocation.isRelocating()) {
                            mCopyLocation.updateLocation(toX(mTouchX), toY(mTouchY));
                        } else {
                            if (mPen == Pen.COPY) {
                                mCopyLocation.updateLocation(mCopyLocation.getCopyStartX() + toX(mTouchX) - mCopyLocation.getTouchStartX(),
                                        mCopyLocation.getCopyStartY() + toY(mTouchY) - mCopyLocation.getTouchStartY());
                            }
                            if (mShape == Shape.HAND_WRITE) {
                                mCurrPath.quadTo(
                                        toX(mLastTouchX),
                                        toY(mLastTouchY),
                                        toX((mTouchX + mLastTouchX) / 2),
                                        toY((mTouchY + mLastTouchY) / 2));
                            } else {

                            }
                        }
                    }
                } else {

                }

                invalidate();
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode -= 1;

                invalidate();
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchMode += 1;

                invalidate();
                return true;
        }
        return super.

                onTouchEvent(event);
    }

    private int mGraffitiRotateDegree = 0;

    public int getGraffitiRotateDegree() {
        return mGraffitiRotateDegree;
    }

    public void rotate(int degree) {
        degree = degree % 360;
        int absDegree = Math.abs(degree);
        if (absDegree > 0 && absDegree < 90) {
            degree = degree / absDegree * 90;
        } else if (absDegree > 90 && absDegree < 180) {
            degree = degree / absDegree * 180;
        } else if (absDegree > 180 && absDegree < 270) {
            degree = degree / absDegree * 2700;
        } else if (absDegree > 270 && absDegree < 360) {
            degree = 0;
        }

        if (degree == mGraffitiRotateDegree) {
            return;
        }
        int r = degree - mGraffitiRotateDegree;
        int originalDegree = mGraffitiRotateDegree;
        mGraffitiRotateDegree = degree;

        mBitmap = ImageUtils.rotate(mBitmap, r, true);
        if (mBitmapEraser != null) {
            mBitmapEraser = ImageUtils.rotate(mBitmapEraser, r, true);
        }
        setBG();

        mCopyLocation.rotatePosition(originalDegree, mGraffitiRotateDegree, mOriginalPivotX, mOriginalPivotY);

        invalidate();

    }

    private void setBG() {
        this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        if (mBitmapEraser != null) {
            this.mBitmapShaderEraser = new BitmapShader(this.mBitmapEraser, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        } else {
            this.mBitmapShaderEraser = mBitmapShader;
        }

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        if (nw > nh) {
            mPrivateScale = 1 / nw;
            mPrivateWidth = getWidth();
            mPrivateHeight = (int) (h * mPrivateScale);
        } else {
            mPrivateScale = 1 / nh;
            mPrivateWidth = (int) (w * mPrivateScale);
            mPrivateHeight = getHeight();
        }
        mCentreTranX = (getWidth() - mPrivateWidth) / 2f;
        mCentreTranY = (getHeight() - mPrivateHeight) / 2f;

        initCanvas();
        resetMatrix();

        if (mPathStack.size() > 0) {
            draw(mBitmapCanvas, mPathStack);
        }

        mAmplifierRadius = Math.min(getWidth(), getHeight()) / 4;
        mAmplifierPath = new Path();
        mAmplifierPath.addCircle(mAmplifierRadius, mAmplifierRadius, mAmplifierRadius, Path.Direction.CCW);
        mAmplifierHorizonX = (int) (Math.min(getWidth(), getHeight()) / 2 - mAmplifierRadius);

        DrawUtil.setGraffitiPixelUnit(Util.dp2px(getContext(), 1) / mPrivateScale);

        if (!mReady) {
            mPaintSize = 30 * GRAFFITI_PIXEL_UNIT;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap.isRecycled() || mGraffitiBitmap.isRecycled()) {
            return;
        }

        canvas.save();
        doDraw(canvas);
        canvas.restore();

        if (mAmplifierScale > 0) {
            canvas.save();

            if (mTouchY <= mAmplifierRadius * 2) {
                canvas.translate(mAmplifierHorizonX, getHeight() - mAmplifierRadius * 2);
            } else {
                canvas.translate(mAmplifierHorizonX, 0);
            }
            canvas.clipPath(mAmplifierPath);
            canvas.drawColor(0xff000000);

            canvas.save();
            float scale = mAmplifierScale / mScale;
            canvas.scale(scale, scale);
            canvas.translate(-mTouchX + mAmplifierRadius / scale, -mTouchY + mAmplifierRadius / scale);
            doDraw(canvas);
            canvas.restore();

            drawCircle(canvas, mAmplifierRadius, mAmplifierRadius, mAmplifierRadius, mAmplifierPaint);
            canvas.restore();
        }

    }

    private void doDraw(Canvas canvas) {
        float left = mCentreTranX + mTransX;
        float top = mCentreTranY + mTransY;

        canvas.translate(left, top);
        canvas.scale(mPrivateScale * mScale, mPrivateScale * mScale);

        canvas.save();
        if (!mIsDrawableOutside) {
            canvas.clipRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        }

        if (isJustDrawOriginal) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            return;
        }

        canvas.drawBitmap(mGraffitiBitmap, 0, 0, null);

        if (mIsPainting) {
            Path path;
            float span = 0;
            if (mTouchDownX == mTouchX && mTouchDownY == mTouchY && mTouchDownX == mLastTouchX && mTouchDownY == mLastTouchY) {
                mTempPath.reset();
                mTempPath.addPath(mCurrPath);
                mTempPath.quadTo(
                        toX(mLastTouchX),
                        toY(mLastTouchY),
                        toX((mTouchX + mLastTouchX + VALUE) / 2),
                        toY((mTouchY + mLastTouchY + VALUE) / 2));
                path = mTempPath;
                span = VALUE;
            } else {
                path = mCurrPath;
                span = 0;
            }
            mPaint.setStrokeWidth(mPaintSize);
            if (mShape == Shape.HAND_WRITE) {
                draw(canvas, mPen, mPaint, path, mPen == Pen.ERASER ? mShaderMatrixEraser : mShaderMatrix,
                        mColor, mGraffitiRotateDegree);
            } else {
                draw(canvas, mPen, mShape, mPaint,
                        toX(mTouchDownX), toY(mTouchDownY), toX(mTouchX + span), toY(mTouchY + span),
                        mPen == Pen.ERASER ? mShaderMatrixEraser : mShaderMatrix, mColor, mGraffitiRotateDegree);
            }
        }
        canvas.restore();


        if (mPen == Pen.COPY) {
            mCopyLocation.drawItSelf(canvas, mPaintSize);
        }

        for (GraffitiSelectableItem item : mSelectableStack) {
            draw(canvas, item);
        }
    }

    private void draw(Canvas canvas, Pen pen, Paint paint, Path path, Matrix matrix, GraffitiColor color, int degree) {
        resetPaint(pen, paint, matrix, color, degree);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }

    private void draw(Canvas canvas, Pen pen, Shape shape, Paint paint, float sx, float sy, float dx, float dy, Matrix matrix, GraffitiColor color, int degree) {
        resetPaint(pen, paint, matrix, color, degree);

        paint.setStyle(Paint.Style.STROKE);

        switch (shape) {
            case ARROW:
                paint.setStyle(Paint.Style.FILL);
                drawArrow(canvas, sx, sy, dx, dy, paint);
                break;
            case LINE:
                drawLine(canvas, sx, sy, dx, dy, paint);
                break;
            case FILL_CIRCLE:
                paint.setStyle(Paint.Style.FILL);
            case HOLLOW_CIRCLE:
                drawCircle(canvas, sx, sy,
                        (float) Math.sqrt((sx - dx) * (sx - dx) + (sy - dy) * (sy - dy)), paint);
                break;
            case FILL_RECT:
                paint.setStyle(Paint.Style.FILL);
            case HOLLOW_RECT:
                drawRect(canvas, sx, sy, dx, dy, paint);
                break;
            default:
                throw new RuntimeException("unknown shape:" + shape);
        }
    }

    private void draw(Canvas canvas, CopyOnWriteArrayList<GraffitiPath> pathStack) {
        for (GraffitiPath path : pathStack) {
            draw(canvas, path);
        }
    }

    private void draw(Canvas canvas, GraffitiPath path) {
        mPaint.setStrokeWidth(path.mStrokeWidth);
        if (path.mShape == Shape.HAND_WRITE) {
            draw(canvas, path.mPen, mPaint, path.getPath(mGraffitiRotateDegree), path.getMatrix(mGraffitiRotateDegree), path.mColor, path.mRotateDegree);
        } else {
            float[] sxy = path.getSxSy(mGraffitiRotateDegree);
            float[] dxy = path.getDxDy(mGraffitiRotateDegree);
            draw(canvas, path.mPen, path.mShape, mPaint,
                    sxy[0], sxy[1], dxy[0], dxy[1], path.getMatrix(mGraffitiRotateDegree), path.mColor, path.mRotateDegree);
        }
    }

    private void draw(Canvas canvas, GraffitiSelectableItem selectableItem) {
        canvas.save();

        float[] xy = selectableItem.getXy(mGraffitiRotateDegree);
        canvas.translate(xy[0], xy[1]);
        canvas.rotate(mGraffitiRotateDegree - selectableItem.getGraffitiRotate() + selectableItem.getItemRotate(), 0, 0); // 旋转坐标系

        if (selectableItem == mSelectedItem) {
            Rect rect = selectableItem.getBounds(mGraffitiRotateDegree);
            mPaint.setShader(null);
            // Rect
            /*if (selectableItem.getColor().getType() == GraffitiColor.Type.COLOR) {
                mPaint.setColor(Color.argb(126,
                        255 - Color.red(selectableItem.getColor().getColor()),
                        255 - Color.green(selectableItem.getColor().getColor()),
                        255 - Color.blue(selectableItem.getColor().getColor())));
            } else {*/
            mPaint.setColor(0x88888888);
//            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            canvas.drawRect(rect, mPaint);
            // border
            if (mIsRotatingSelectedItem) {
                mPaint.setColor(0x88ffd700);
            } else {
                mPaint.setColor(0x88888888);
            }
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(2 * GRAFFITI_PIXEL_UNIT);
            canvas.drawRect(rect, mPaint);
            // rotate
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(4 * GRAFFITI_PIXEL_UNIT);
            canvas.drawLine(rect.right, rect.top + rect.height() / 2,
                    rect.right + (GraffitiSelectableItem.ITEM_CAN_ROTATE_BOUND - 16) * GRAFFITI_PIXEL_UNIT, rect.top + rect.height() / 2, mPaint);
            canvas.drawCircle(rect.right + (GraffitiSelectableItem.ITEM_CAN_ROTATE_BOUND - 8) * GRAFFITI_PIXEL_UNIT, rect.top + rect.height() / 2, 8 * GRAFFITI_PIXEL_UNIT, mPaint);

        }
        resetPaint(Pen.TEXT, mPaint, null, selectableItem.getColor(), selectableItem.getGraffitiRotate());

        selectableItem.draw(canvas, this, mPaint);

        canvas.restore();

    }

    private void resetPaint(Pen pen, Paint paint, Matrix matrix, GraffitiColor color, int rotateDegree) {
        mPaint.setColor(Color.BLACK);
        switch (pen) {
            case HAND:
            case TEXT:
                paint.setShader(null);
                mShaderMatrixColor.reset();

                if (color.getType() == GraffitiColor.Type.BITMAP) {
                    if (mGraffitiRotateDegree != 0) {
                        float px = mOriginalPivotX, py = mOriginalPivotY;
                        if (mGraffitiRotateDegree == 90 || mGraffitiRotateDegree == 270) {
                            float t = px;
                            px = py;
                            py = t;
                        }
                        mShaderMatrixColor.postRotate(mGraffitiRotateDegree, px, py);
                        if (Math.abs(mGraffitiRotateDegree) == 90 || Math.abs(mGraffitiRotateDegree) == 270) {
                            mShaderMatrixColor.postTranslate((py - px), -(py - px));
                        }
                    }
                }

                color.initColor(paint, mShaderMatrixColor);
                break;
            case COPY:
                mBitmapShader.setLocalMatrix(matrix);
                paint.setShader(this.mBitmapShader);
                break;
            case ERASER:
                mBitmapShaderEraser.setLocalMatrix(matrix);
                if (mBitmapShader != mBitmapShaderEraser) {
                    mBitmapShaderEraser.setLocalMatrix(mShaderMatrixEraser);
                }
                paint.setShader(this.mBitmapShaderEraser);
                break;
        }
    }


    public final float toX(float touchX) {
        return (touchX - mCentreTranX - mTransX) / (mPrivateScale * mScale);
    }

    public final float toY(float touchY) {
        return (touchY - mCentreTranY - mTransY) / (mPrivateScale * mScale);
    }

    public final float toTouchX(float x) {
        return x * ((mPrivateScale * mScale)) + mCentreTranX + mTransX;
    }

    public final float toTouchY(float y) {
        return y * ((mPrivateScale * mScale)) + mCentreTranY + mTransY;
    }

    public final float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (mPrivateScale * mScale) + touchX - mCentreTranX;
    }

    public final float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (mPrivateScale * mScale) + touchY - mCentreTranY;
    }

    public CopyOnWriteArrayList<GraffitiPath> getPathStack() {
        return mPathStack;
    }

    public CopyOnWriteArrayList<GraffitiSelectableItem> getSelectedItemStack() {
        return mSelectableStack;
    }

    public final void addPath(GraffitiPath path) {
        mPathStack.add(path);
        mUndoStack.add(path);
        draw(mBitmapCanvas, path);
    }

    public final void removePath(GraffitiPath path) {
        mPathStack.remove(path);
        mUndoStack.remove(path);
        initCanvas();
        draw(mBitmapCanvas, mPathStack);
        invalidate();
    }

    public final void addSelectableItem(GraffitiSelectableItem item) {
        mSelectableStack.add(item);
        mUndoStack.add((Undoable) item);
    }

    public final void removeSelectableItem(GraffitiSelectableItem item) {
        mSelectableStack.remove(item);
        mUndoStack.remove(item);
    }

    public final void  topSelectableItem(GraffitiSelectableItem item) {
        removeSelectableItem(item);
        mSelectableStack.add(item);
        mUndoStack.add(item);
    }

    private void initCanvas() {
        if (mGraffitiBitmap != null) {
            mGraffitiBitmap.recycle();
        }
        mGraffitiBitmap = mBitmap.copy(Bitmap.Config.RGB_565, true);
        mBitmapCanvas = new Canvas(mGraffitiBitmap);
    }

    private void resetMatrix() {
        if (mPen == Pen.COPY) {
            this.mShaderMatrix.reset();
            this.mShaderMatrix.postTranslate(mCopyLocation.getTouchStartX() - mCopyLocation.getCopyStartX(), mCopyLocation.getTouchStartY() - mCopyLocation.getCopyStartY());
        } else {
            this.mShaderMatrix.reset();
        }
        mShaderMatrixEraser.reset();
        mShaderMatrixEraser.set(mShaderMatrix);

        if (mPen == Pen.ERASER && mBitmapShader != mBitmapShaderEraser) {
            if (mEraserImageIsResizeable) {
                mShaderMatrixEraser.preScale(mBitmap.getWidth() * 1f / mBitmapEraser.getWidth(), mBitmap.getHeight() * 1f / mBitmapEraser.getHeight());
            } else {
                if (mGraffitiRotateDegree == 90) {
                    mShaderMatrixEraser.preTranslate(mBitmap.getWidth() - mBitmapEraser.getWidth(), 0);
                } else if (mGraffitiRotateDegree == 180) {
                    mShaderMatrixEraser.preTranslate(mBitmap.getWidth() - mBitmapEraser.getWidth(), mBitmap.getHeight() - mBitmapEraser.getHeight());
                } else if (mGraffitiRotateDegree == 270) {
                    mShaderMatrixEraser.preTranslate(0, mBitmap.getHeight() - mBitmapEraser.getHeight());
                }
            }
        }
    }

    private void judgePosition() {
        boolean changed = false;
        if (mPrivateWidth * mScale < getWidth()) {
            if (mTransX + mCentreTranX < 0) {
                mTransX = -mCentreTranX;
                changed = true;
            } else if (mTransX + mCentreTranX + mPrivateWidth * mScale > getWidth()) {
                mTransX = getWidth() - mCentreTranX - mPrivateWidth * mScale;
                changed = true;
            }
        } else {
            if (mTransX + mCentreTranX > 0) {
                mTransX = -mCentreTranX;
                changed = true;
            } else if (mTransX + mCentreTranX + mPrivateWidth * mScale < getWidth()) {
                mTransX = getWidth() - mCentreTranX - mPrivateWidth * mScale;
                changed = true;
            }
        }
        if (mPrivateHeight * mScale < getHeight()) {
            if (mTransY + mCentreTranY < 0) {
                mTransY = -mCentreTranY;
                changed = true;
            } else if (mTransY + mCentreTranY + mPrivateHeight * mScale > getHeight()) {
                mTransY = getHeight() - mCentreTranY - mPrivateHeight * mScale;
                changed = true;
            }
        } else {
            if (mTransY + mCentreTranY > 0) {
                mTransY = -mCentreTranY;
                changed = true;
            } else if (mTransY + mCentreTranY + mPrivateHeight * mScale < getHeight()) {
                mTransY = getHeight() - mCentreTranY - mPrivateHeight * mScale;
                changed = true;
            }
        }
        if (changed) {
            resetMatrix();
        }
    }

    private CopyLocation getCopyLocation() {
        if (mPen == Pen.COPY) {
            return mCopyLocation.copy();
        }
        return null;
    }

    public boolean isPenSelectable() {
        return mPen == Pen.TEXT || mPen == Pen.BITMAP;
    }

    // ========================= api ================================

    public void save(){

        mSelectedItem = null;

        for (GraffitiSelectableItem item : mSelectableStack) {
            draw(mBitmapCanvas, item);
        }
        mGraffitiListener.onSaved(mGraffitiBitmap, mBitmapEraser);
    }

    public void clear() {
        mPathStack.clear();
        mSelectableStack.clear();
        mUndoStack.clear();
        initCanvas();
        invalidate();
    }

    public void undo() {
        if (mUndoStack.size() > 0) {
            Undoable undoable = mUndoStack.remove(mUndoStack.size() - 1);
            removed.add(undoable);
            mPathStack.remove(undoable);
            mSelectableStack.remove(undoable);
            if (undoable == mSelectedItem) {
                mSelectedItem = null;
            }

            initCanvas();
            draw(mBitmapCanvas, mPathStack);
            invalidate();
        }
    }

    private List<Undoable> removed = new ArrayList<>();


    public void revertUndo() {

        if(removed.size() > 0) {
            Undoable undoable = removed.remove(removed.size() - 1);
            if(undoable.getType() == 0) {
                mSelectableStack.add((GraffitiSelectableItem)undoable);
            } else {
                mPathStack.add((GraffitiPath) undoable);
            }
            mUndoStack.add(undoable);
            initCanvas();
            draw(mBitmapCanvas, mPathStack);
            invalidate();
        }
    }

    public boolean isModified() {
        return mUndoStack.size() != 0 || mGraffitiRotateDegree != 0;
    }

    public void centrePic() {
        mScale = 1;
        mTransX = 0;
        mTransY = 0;
        judgePosition();
        invalidate();
    }

    public void setJustDrawOriginal(boolean justDrawOriginal) {
        isJustDrawOriginal = justDrawOriginal;
        invalidate();
    }

    public boolean isJustDrawOriginal() {
        return isJustDrawOriginal;
    }

    public void setColor(int color) {
        mColor.setColor(color);
        invalidate();
    }

    public void setColor(Bitmap bitmap) {
        if (mBitmap == null) {
            return;
        }
        mColor.setColor(bitmap);
        invalidate();
    }

    public GraffitiColor getColor() {
        return mColor;
    }

    public void setColor(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        if (mBitmap == null) {
            return;
        }
        mColor.setColor(bitmap, tileX, tileY);
        invalidate();
    }

    public GraffitiColor getGraffitiColor() {
        return mColor;
    }

    public void setScale(float scale, float pivotX, float pivotY) {
        float touchX = toTouchX(pivotX);
        float touchY = toTouchY(pivotY);
        this.mScale = scale;

        mTransX = toTransX(touchX, pivotX);
        mTransY = toTransY(touchY, pivotY);

        judgePosition();
        resetMatrix();
        invalidate();
    }

    public void setScale(float scale) {
        setScale(scale, 0, 0);
    }

    public float getScale() {
        return mScale;
    }

    public void setPen(Pen pen) {
        if (pen == null) {
            throw new RuntimeException("Pen can't be null");
        }
        Pen old = mPen;
        mPen = pen;
        resetMatrix();

        if (!isPenSelectable() || old != mPen) {
            if (mSelectedItem != null) {
                GraffitiSelectableItem oldItem = mSelectedItem;
                mSelectedItem = null;
                mGraffitiListener.onSelectedItem(oldItem, false);
            }
        }

        invalidate();
    }

    public Pen getPen() {
        return mPen;
    }

    public void setShape(Shape shape) {
        if (shape == null) {
            throw new RuntimeException("Shape can't be null");
        }
        mShape = shape;
        invalidate();
    }

    public Shape getShape() {
        return mShape;
    }

    public void setTrans(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        judgePosition();
        resetMatrix();
        invalidate();
    }

    public void setTransX(float transX) {
        this.mTransX = transX;
        judgePosition();
        invalidate();
    }

    public float getTransX() {
        return mTransX;
    }

    public void setTransY(float transY) {
        this.mTransY = transY;
        judgePosition();
        invalidate();
    }

    public float getTransY() {
        return mTransY;
    }


    public void setPaintSize(float paintSize) {
        mPaintSize = paintSize;
        invalidate();
    }

    public float getPaintSize() {
        return mPaintSize;
    }

    public void setIsDrawableOutside(boolean isDrawableOutside) {
        mIsDrawableOutside = isDrawableOutside;
    }

    public boolean getIsDrawableOutside() {
        return mIsDrawableOutside;
    }

    public void setAmplifierScale(float amplifierScale) {
        mAmplifierScale = amplifierScale;
        invalidate();
    }

    public float getAmplifierScale() {
        return mAmplifierScale;
    }
    public int getBitmapWidthOnView() {
        return mPrivateWidth;
    }

    public int getBitmapHeightOnView() {
        return mPrivateHeight;
    }

    public float getSelectedItemSize() {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        return mSelectedItem.getSize();
    }

    public void setSelectedItemSize(float selectedTextSize) {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        mSelectedItem.setSize(selectedTextSize);
        invalidate();
    }

    public void setSelectedItemColor(int color) {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        mSelectedItem.getColor().setColor(color);
        invalidate();
    }

    public void setSelectedItemColor(Bitmap bitmap) {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        if (mBitmap == null) {
            return;
        }
        mSelectedItem.getColor().setColor(bitmap);
        invalidate();
    }

    public GraffitiColor getSelectedItemColor() {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        return mSelectedItem.getColor();
    }

    public boolean isSelectedItem() {
        return mSelectedItem != null;
    }

    public GraffitiSelectableItem getSelectedItem() {
        return mSelectedItem;
    }

    public void removeSelectedItem() {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        removeSelectableItem(mSelectedItem);
        GraffitiSelectableItem oldItem = mSelectedItem;
        mSelectedItem = null;
        mGraffitiListener.onSelectedItem(oldItem, false);
        invalidate();
    }

    public void topSelectedItem() {
        if (mSelectedItem == null) {
            throw new NullPointerException("Selected item is null!");
        }
        topSelectableItem(mSelectedItem);
        invalidate();
    }

    public float getOriginalPivotX() {
        return mOriginalPivotX;
    }

    public float getOriginalPivotY() {
        return mOriginalPivotY;
    }
}
