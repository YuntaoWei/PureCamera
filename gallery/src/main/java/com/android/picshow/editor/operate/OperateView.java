package com.android.picshow.editor.operate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class OperateView extends View {
    private List<ImageObject> imgLists = new ArrayList<ImageObject>();
    private Rect mCanvasLimits;
    private Bitmap bgBmp;
    private Paint paint = new Paint();
    private boolean isMultiAdd;
    private float picScale = 0.4f;

    public void setPicScale(float picScale) {
        this.picScale = picScale;
    }

    public void setMultiAdd(boolean isMultiAdd) {
        this.isMultiAdd = isMultiAdd;
    }

    public OperateView(Context context, Bitmap resizeBmp) {
        super(context);
        bgBmp = resizeBmp;
        int width = bgBmp.getWidth();
        int height = bgBmp.getHeight();
        mCanvasLimits = new Rect(0, 0, width, height);
    }

    public void addItem(ImageObject imgObj) {
        if (imgObj == null) {
            return;
        }
        if (!isMultiAdd && imgLists != null) {
            imgLists.clear();
        }
        imgObj.setSelected(true);
        if (!imgObj.isTextObject) {
            imgObj.setScale(picScale);
        }
        ImageObject tempImgObj = null;
        for (int i = 0; i < imgLists.size(); i++) {
            tempImgObj = imgLists.get(i);
            tempImgObj.setSelected(false);
        }
        imgLists.add(imgObj);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sc = canvas.save();
        canvas.clipRect(mCanvasLimits);
        canvas.drawBitmap(bgBmp, 0, 0, paint);
        drawImages(canvas);
        canvas.restoreToCount(sc);
        for (ImageObject ad : imgLists) {
            if (ad != null && ad.isSelected()) {
                ad.drawIcon(canvas);
            }
        }
    }

    public void save() {
        ImageObject io = getSelected();
        if (io != null) {
            io.setSelected(false);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            handleSingleTouchManipulateEvent(event);
        } else {
            handleMultiTouchManipulateEvent(event);
        }
        invalidate();

        super.onTouchEvent(event);
        return true;
    }

    private boolean mMovedSinceDown = false;
    private boolean mResizeAndRotateSinceDown = false;
    private float mStartDistance = 0.0f;
    private float mStartScale = 0.0f;
    private float mStartRot = 0.0f;
    private float mPrevRot = 0.0f;
    static public final double ROTATION_STEP = 2.0;
    static public final double ZOOM_STEP = 0.01;
    static public final float CANVAS_SCALE_MIN = 0.25f;
    static public final float CANVAS_SCALE_MAX = 3.0f;
    private Point mPreviousPos = new Point(0, 0);
    float diff;
    float rot;

    private void handleMultiTouchManipulateEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                float x1 = event.getX(0);
                float x2 = event.getX(1);
                float y1 = event.getY(0);
                float y2 = event.getY(1);
                float delX = (x2 - x1);
                float delY = (y2 - y1);
                diff = (float) Math.sqrt((delX * delX + delY * delY));
                mStartDistance = diff;
                mPrevRot = (float) Math.toDegrees(Math.atan2(delX, delY));
                for (ImageObject io : imgLists) {
                    if (io.isSelected()) {
                        mStartScale = io.getScale();
                        mStartRot = io.getRotation();
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                x1 = event.getX(0);
                x2 = event.getX(1);
                y1 = event.getY(0);
                y2 = event.getY(1);
                delX = (x2 - x1);
                delY = (y2 - y1);
                diff = (float) Math.sqrt((delX * delX + delY * delY));
                float scale = diff / mStartDistance;
                float newscale = mStartScale * scale;
                rot = (float) Math.toDegrees(Math.atan2(delX, delY));
                float rotdiff = mPrevRot - rot;
                for (ImageObject io : imgLists) {
                    if (io.isSelected() && newscale < 10.0f && newscale > 0.1f) {
                        float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
                        if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
                                .abs(newrot - io.getRotation())) {
                            io.setScale(newscale);
                        } else {
                            io.setRotation(newrot % 360);
                        }
                        break;
                    }
                }

                break;
        }
    }

    private ImageObject getSelected() {
        for (ImageObject ibj : imgLists) {
            if (ibj.isSelected()) {
                return ibj;
            }
        }
        return null;
    }

    private long selectTime = 0;

    private void handleSingleTouchManipulateEvent(MotionEvent event) {

        long currentTime = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mMovedSinceDown = false;
                mResizeAndRotateSinceDown = false;
                int selectedId = -1;

                for (int i = imgLists.size() - 1; i >= 0; --i) {
                    ImageObject io = imgLists.get(i);
                    if (io.contains(event.getX(), event.getY())
                            || io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.RIGHTBOTTOM)
                            || io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.LEFTTOP)) {
                        io.setSelected(true);
                        imgLists.remove(i);
                        imgLists.add(io);
                        selectedId = imgLists.size() - 1;
                        currentTime = System.currentTimeMillis();
                        if (currentTime - selectTime < 300) {
                            if (myListener != null) {
                                if (getSelected().isTextObject()) {
                                    myListener
                                            .onClick((TextObject) getSelected());
                                }
                            }
                        }
                        selectTime = currentTime;
                        break;
                    }
                }
                if (selectedId < 0) {
                    for (int i = imgLists.size() - 1; i >= 0; --i) {
                        ImageObject io = imgLists.get(i);
                        if (io.contains(event.getX(), event.getY())
                                || io.pointOnCorner(event.getX(), event.getY(),
                                OperateConstants.RIGHTBOTTOM)
                                || io.pointOnCorner(event.getX(), event.getY(),
                                OperateConstants.LEFTTOP)) {
                            io.setSelected(true);
                            imgLists.remove(i);
                            imgLists.add(io);
                            selectedId = imgLists.size() - 1;
                            break;
                        }
                    }
                }
                for (int i = 0; i < imgLists.size(); ++i) {
                    ImageObject io = imgLists.get(i);
                    if (i != selectedId) {
                        io.setSelected(false);
                    }
                }

                ImageObject io = getSelected();
                if (io != null) {
                    if (io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.LEFTTOP)) {
                        imgLists.remove(io);
                    } else if (io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.RIGHTBOTTOM)) {
                        mResizeAndRotateSinceDown = true;
                        float x = event.getX();
                        float y = event.getY();
                        float delX = x - io.getPoint().x;
                        float delY = y - io.getPoint().y;
                        diff = (float) Math.sqrt((delX * delX + delY * delY));
                        mStartDistance = diff;
                        mPrevRot = (float) Math.toDegrees(Math
                                .atan2(delX, delY));
                        mStartScale = io.getScale();
                        mStartRot = io.getRotation();
                    } else if (io.contains(event.getX(), event.getY())) {
                        mMovedSinceDown = true;
                        mPreviousPos.x = (int) event.getX();
                        mPreviousPos.y = (int) event.getY();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                mMovedSinceDown = false;
                mResizeAndRotateSinceDown = false;

                break;

            case MotionEvent.ACTION_MOVE:
                if (mMovedSinceDown) {
                    int curX = (int) event.getX();
                    int curY = (int) event.getY();
                    int diffX = curX - mPreviousPos.x;
                    int diffY = curY - mPreviousPos.y;
                    mPreviousPos.x = curX;
                    mPreviousPos.y = curY;
                    io = getSelected();
                    Point p = io.getPosition();
                    int x = p.x + diffX;
                    int y = p.y + diffY;
                    if (p.x + diffX >= mCanvasLimits.left
                            && p.x + diffX <= mCanvasLimits.right
                            && p.y + diffY >= mCanvasLimits.top
                            && p.y + diffY <= mCanvasLimits.bottom)
                        io.moveBy((int) (diffX), (int) (diffY));
                }
                if (mResizeAndRotateSinceDown) {
                    io = getSelected();
                    float x = event.getX();
                    float y = event.getY();
                    float delX = x - io.getPoint().x;
                    float delY = y - io.getPoint().y;
                    diff = (float) Math.sqrt((delX * delX + delY * delY));
                    float scale = diff / mStartDistance;
                    float newscale = mStartScale * scale;
                    rot = (float) Math.toDegrees(Math.atan2(delX, delY));
                    float rotdiff = mPrevRot - rot;
                    if (newscale < 10.0f && newscale > 0.1f) {
                        float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
                        if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
                                .abs(newrot - io.getRotation())) {
                            io.setScale(newscale);
                        } else {
                            io.setRotation(newrot % 360);
                        }
                    }
                }
                break;
        }

        cancelLongPress();

    }

    private void drawImages(Canvas canvas) {
        for (ImageObject ad : imgLists) {
            if (ad != null) {
                ad.draw(canvas);
            }
        }
    }

    MyListener myListener;

    public void setOnListener(MyListener myListener) {
        this.myListener = myListener;
    }

    public interface MyListener {
        public void onClick(TextObject tObject);
    }
}
