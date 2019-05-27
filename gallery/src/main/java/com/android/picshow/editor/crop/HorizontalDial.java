package com.android.picshow.editor.crop;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.picshow.R;
import com.android.picshow.utils.LogPrinter;


/**
 * Created by yuntao.wei on 2018/5/22.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class HorizontalDial extends View implements View.OnTouchListener {

    public interface onSeekChangeListener {

        void onSeekStart(HorizontalDial v);

        void onProgressChange(float progress);

        void onSeekEnd(HorizontalDial v);

    }

    private final static int DEFAULT_WIDTH = 300;
    private final static int DEFAULT_HEIGHT = 100;
    private int lineNum = 0;
    private Paint linePaint, selectPaint;
    private int viewWidth = 0, viewHeight = 0;
    private final static int LINE_PADDING = 20;
    private final static int THUMB_WIDTH = 40;
    private int selectColor;

    private onSeekChangeListener seekChangeListener;

    public void setOnSeekChangeListener(onSeekChangeListener l) {
        seekChangeListener = l;
    }


    public HorizontalDial(Context context) {
        this(context, null);
    }


    public HorizontalDial(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public HorizontalDial(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectColor = context.getResources().getColor(R.color.select_text_color);
        initPaint();
        setOnTouchListener(this);
    }

    private void initPaint() {
        linePaint = new Paint();
        selectPaint = new Paint();

    }

    private LinearGradient leftShader, leftSelectShader;
    private void setShaderLeft() {
        leftShader = new LinearGradient(viewWidth / 2, viewHeight / 2,
                viewWidth, viewHeight / 2,
                Color.WHITE, Color.BLACK, Shader.TileMode.MIRROR);
        linePaint.setShader(leftShader);

        leftSelectShader = new LinearGradient(viewWidth / 2, viewHeight / 2,
                viewWidth, viewHeight / 2,
                selectColor, Color.BLACK, Shader.TileMode.MIRROR);
        selectPaint.setShader(leftSelectShader);
    }

    private LinearGradient rightShader, rightSelectShader;
    private void setShaderRight() {
        rightShader = new LinearGradient(0, viewHeight / 2,
                viewWidth / 2, viewHeight / 2,
                Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR);
        linePaint.setShader(rightShader);

        rightSelectShader = new LinearGradient(0, viewHeight / 2,
                viewWidth / 2, viewHeight / 2,
                Color.BLACK, selectColor, Shader.TileMode.MIRROR);
        selectPaint.setShader(rightSelectShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(viewHeight == 0)
            viewHeight = getHeight();

        if(viewWidth == 0)
            viewWidth = getWidth();

        if(lineNum == 0)
            lineNum = viewWidth / LINE_PADDING;

        drawLeftVerticalLine(canvas);
        drawRightVerticalLine(canvas);
        drawThumb(canvas);
        drawSelectLine(canvas);
    }

    private void drawLeftVerticalLine(Canvas canvas) {
        int startX;
        setShaderRight();
        if(lineNum % 2 == 0) {
            startX = viewWidth / 2 + LINE_PADDING / 2;
            for(int i = 0; i < lineNum / 2; i++) {
                canvas.drawLine(startX, viewHeight / 2, startX, viewHeight, linePaint);
                startX -= LINE_PADDING;
            }
        } else {
            startX = viewWidth / 2;
            for(int i = 0; i < lineNum /2 + 1; i++) {
                canvas.drawLine(startX, viewHeight / 2, startX, viewHeight, linePaint);
                startX -= LINE_PADDING;
            }
        }
    }

    private void drawRightVerticalLine(Canvas canvas) {
        int startX;
        setShaderLeft();
        if(lineNum % 2 == 0) {
            startX = viewWidth / 2 - LINE_PADDING / 2;
            for(int i = 0; i < lineNum / 2; i++) {
                canvas.drawLine(startX, viewHeight / 2, startX, viewHeight, linePaint);
                startX += LINE_PADDING;
            }
        } else {
            startX = viewWidth / 2 - LINE_PADDING;
            for(int i = 0; i < lineNum / 2; i++) {
                canvas.drawLine(startX, viewHeight / 2, startX, viewHeight, linePaint);
                startX += LINE_PADDING;
            }
        }
    }

    private void drawThumb(Canvas canvas) {
        canvas.drawLine(viewWidth / 2 - THUMB_WIDTH / 2, 0,
                viewWidth / 2, viewHeight / 2, selectPaint);

        canvas.drawLine(viewWidth / 2, viewHeight / 2,
                viewWidth / 2 + THUMB_WIDTH / 2, 0, selectPaint);

        canvas.drawLine(viewWidth / 2 - THUMB_WIDTH / 2, 0,
                viewWidth / 2 + THUMB_WIDTH / 2, 0, selectPaint);
    }

    private void drawSelectLine(Canvas canvas) {
        if(currentX < startX) {
            drawLeftSelectLine(canvas);
        } else {
            drawRightSelectLine(canvas);
        }
    }

    private void drawLeftSelectLine(Canvas canvas) {
        if(startX - currentX < LINE_PADDING) {
            return;
        }

        float x;
        int num = (int)((startX - currentX) / LINE_PADDING);
        if(lineNum / 2 == 0) {
            x = viewWidth / 2 - LINE_PADDING / 2;
            for(int i = 0 ; i < num; i ++) {
                canvas.drawLine(x, viewHeight / 2, x, viewHeight, selectPaint);
                x -= LINE_PADDING;
            }
        } else {
            x = viewWidth / 2;
            for(int i = 0; i < num; i ++) {
                canvas.drawLine(x, viewHeight / 2, x, viewHeight, selectPaint);
                x -= LINE_PADDING;
            }
        }
    }

    private void drawRightSelectLine(Canvas canvas) {
        if(currentX - startX < LINE_PADDING) {
            return;
        }
        float x;
        int num = (int)((currentX - startX) / LINE_PADDING);
        if(lineNum / 2 == 0) {
            x = viewWidth / 2 + LINE_PADDING / 2;
            for(int i = 0 ; i < num; i ++) {
                canvas.drawLine(x, viewHeight / 2, x, viewHeight, selectPaint);
                x += LINE_PADDING;
            }
        } else {
            x = viewWidth / 2;
            for(int i = 0; i < num; i ++) {
                canvas.drawLine(x, viewHeight / 2, x, viewHeight, selectPaint);
                x += LINE_PADDING;
            }
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode) {

            case MeasureSpec.UNSPECIFIED:
                width = DEFAULT_WIDTH;
                break;

            case MeasureSpec.AT_MOST:

            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;

        }

        switch (heightMode) {

            case MeasureSpec.UNSPECIFIED:
                height = DEFAULT_HEIGHT;
                break;

            case MeasureSpec.AT_MOST:

            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;

        }

        setMeasuredDimension(width, height);

    }

    private void showAngle() {

    }

    private void hideAngle() {

    }


    private float preAngle = 0;

    private float startX, currentX;
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = event.getX();

                if(seekChangeListener != null) {
                    seekChangeListener.onSeekStart(this);
                }

                return true;

            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                showAngle();

                if(seekChangeListener != null) {
                    int d = (int)(currentX - startX) * 45 / (viewWidth / 2);
                    LogPrinter.i("www",d + "   " + preAngle);
                    if(preAngle != 0) {
                        d += preAngle;
                    }
                    preAngle = d;
                    rectifyPreAngle();
                    seekChangeListener.onProgressChange(preAngle);
                }

                invalidate();
                return true;

            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_UP:
                hideAngle();
                currentX = event.getX();
                if(seekChangeListener != null) {
                    seekChangeListener.onSeekEnd(this);
                }

                preAngle = (int)(currentX - startX) * 45 / (viewWidth / 2);
                rectifyPreAngle();
                invalidate();
                return true;

        }

        return false;
    }

    private void rectifyPreAngle() {
        preAngle = preAngle > 45 ? 45 : preAngle;
        preAngle = preAngle < -45 ? -45 : preAngle;
    }

}
