package com.android.picshow.editor.graffti;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.picshow.R;

import cn.forward.androids.utils.Util;

public class ColorPickerDialog extends Dialog {
    private final boolean debug = true;
    private final String TAG = "ColorPicker";

    Context context;
    private String title;
    private int mInitialColor;
    private OnColorChangedListener mListener;


    public ColorPickerDialog(Context context, String title,
                             OnColorChangedListener listener) {
        this(context, Color.BLACK, title, listener);
    }

    public ColorPickerDialog(Context context, int initialColor,
                             String title, OnColorChangedListener listener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        mListener = listener;
        mInitialColor = initialColor;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mCenterPaint;
        private Paint mLinePaint;
        private Paint mRectPaint;

        private Shader rectShader;
        private float rectLeft;
        private float rectTop;
        private float rectRight;
        private float rectBottom;

        private final int[] mCircleColors;
        private final int[] mRectColors;

        private int mHeight;
        private int mWidth;
        private float r;
        private float centerRadius;

        private boolean downInCircle = true;
        private boolean downInRect;
        private boolean highlightCenter;
        private boolean highlightCenterLittle;
        private RectF mRectF = new RectF();

        public ColorPickerView(Context context, int height, int width) {
            super(context);
            this.mHeight = height;
            this.mWidth = width;
            setMinimumHeight(height);
            setMinimumWidth(width);
            mCircleColors = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
                    0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
            Shader s = new SweepGradient(0, 0, mCircleColors, null);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(Util.dp2px(context, 30));//圆环大小
            r = width / 2 * 0.7f - mPaint.getStrokeWidth() * 0.5f;

            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(mInitialColor);
            mCenterPaint.setStrokeWidth(5);
            centerRadius = (r - mPaint.getStrokeWidth() / 2) * 0.7f;

            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setColor(Color.parseColor("#72A1D1"));
            mLinePaint.setStrokeWidth(4);

            mRectColors = new int[]{0xFF000000, mCenterPaint.getColor(), 0xFFFFFFFF};
            mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRectPaint.setStrokeWidth(Util.dp2px(context, 5));
            rectLeft = -r - mPaint.getStrokeWidth() * 0.5f;
            rectTop = r + mPaint.getStrokeWidth() * 0.5f +
                    mLinePaint.getStrokeMiter() * 0.5f + 15;
            rectRight = r + mPaint.getStrokeWidth() * 0.5f;
            rectBottom = rectTop + Util.dp2px(context, 30);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate(mWidth / 2, mHeight / 2 - 50);
            canvas.drawCircle(0, 0, centerRadius, mCenterPaint);
            if (highlightCenter || highlightCenterLittle) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);
                if (highlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else if (highlightCenterLittle) {
                    mCenterPaint.setAlpha(0x90);
                }
                canvas.drawCircle(0, 0,
                        centerRadius + mCenterPaint.getStrokeWidth(), mCenterPaint);

                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
            mRectF.set(-r, -r, r, r);
            canvas.drawOval(mRectF, mPaint);
            if (downInCircle) {
                if (mRectColors[1] != mCenterPaint.getColor()) {
                    mRectColors[1] = mCenterPaint.getColor();
                    rectShader = new LinearGradient(rectLeft, 0, rectRight, 0, mRectColors, null, Shader.TileMode.MIRROR);
                }
            }
            if (rectShader == null) {
                rectShader = new LinearGradient(rectLeft, 0, rectRight, 0, mRectColors, null, Shader.TileMode.MIRROR);
            }

            mRectPaint.setShader(rectShader);
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mRectPaint);
            float offset = mLinePaint.getStrokeWidth() / 2;
            canvas.drawLine(rectLeft - offset, rectTop - offset * 2,
                    rectLeft - offset, rectBottom + offset * 2, mLinePaint);//左   
            canvas.drawLine(rectLeft - offset * 2, rectTop - offset,
                    rectRight + offset * 2, rectTop - offset, mLinePaint);//上   
            canvas.drawLine(rectRight + offset, rectTop - offset * 2,
                    rectRight + offset, rectBottom + offset * 2, mLinePaint);//右   
            canvas.drawLine(rectLeft - offset * 2, rectBottom + offset,
                    rectRight + offset * 2, rectBottom + offset, mLinePaint);//下

            super.onDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - mWidth / 2;
            float y = event.getY() - mHeight / 2 + 50;
            boolean inCircle = inColorCircle(x, y,
                    r + mPaint.getStrokeWidth() / 2, r - mPaint.getStrokeWidth() / 2);
            boolean inCenter = inCenter(x, y, centerRadius);
            boolean inRect = inRect(x, y);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downInCircle = inCircle;
                    downInRect = inRect;
                    highlightCenter = inCenter;
                case MotionEvent.ACTION_MOVE:
                    if (downInCircle && inCircle) {
                        float angle = (float) Math.atan2(y, x);
                        float unit = (float) (angle / (2 * Math.PI));
                        if (unit < 0) {
                            unit += 1;
                        }
                        mCenterPaint.setColor(interpCircleColor(mCircleColors, unit));
                    } else if (downInRect && inRect) {
                        mCenterPaint.setColor(interpRectColor(mRectColors, x));
                    }
                    if ((highlightCenter && inCenter) || (highlightCenterLittle && inCenter)) {
                        highlightCenter = true;
                        highlightCenterLittle = false;
                    } else if (highlightCenter || highlightCenterLittle) {
                        highlightCenter = false;
                        highlightCenterLittle = true;
                    } else {
                        highlightCenter = false;
                        highlightCenterLittle = false;
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (highlightCenter && inCenter) {
                        if (mListener != null) {
                            mListener.colorChanged(mCenterPaint.getColor());
                            ColorPickerDialog.this.dismiss();
                        }
                    }
                    if (downInCircle) {
                        downInCircle = false;
                    }
                    if (downInRect) {
                        downInRect = false;
                    }
                    if (highlightCenter) {
                        highlightCenter = false;
                    }
                    if (highlightCenterLittle) {
                        highlightCenterLittle = false;
                    }
                    invalidate();
                    break;
            }
            return true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(mWidth, mHeight);
        }

        private boolean inColorCircle(float x, float y, float outRadius, float inRadius) {
            double outCircle = Math.PI * outRadius * outRadius;
            double inCircle = Math.PI * inRadius * inRadius;
            double fingerCircle = Math.PI * (x * x + y * y);
            if (fingerCircle < outCircle && fingerCircle > inCircle) {
                return true;
            } else {
                return false;
            }
        }

        private boolean inCenter(float x, float y, float centerRadius) {
            double centerCircle = Math.PI * centerRadius * centerRadius;
            double fingerCircle = Math.PI * (x * x + y * y);
            if (fingerCircle < centerCircle) {
                return true;
            } else {
                return false;
            }
        }

        private boolean inRect(float x, float y) {
            if (x <= rectRight && x >= rectLeft && y <= rectBottom && y >= rectTop) {
                return true;
            } else {
                return false;
            }
        }

        private int interpCircleColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int) p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index   
            int c0 = colors[i];
            int c1 = colors[i + 1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
        }

        private int interpRectColor(int colors[], float x) {
            int a, r, g, b, c0, c1;
            float p;
            if (x < 0) {
                c0 = colors[0];
                c1 = colors[1];
                p = (x + rectRight) / rectRight;
            } else {
                c0 = colors[1];
                c1 = colors[2];
                p = x / rectRight;
            }
            a = ave(Color.alpha(c0), Color.alpha(c1), p);
            r = ave(Color.red(c0), Color.red(c1), p);
            g = ave(Color.green(c0), Color.green(c1), p);
            b = ave(Color.blue(c0), Color.blue(c1), p);
            return Color.argb(a, r, g, b);
        }

        private int ave(int s, int d, float p) {
            return s + Math.round(p * (d - s));
        }
    }

    @Override
    public void show() {
        super.show();
        int height = Util.dp2px(context, 220);
        int width = Util.dp2px(context, 180);
        ColorPickerView myView = new ColorPickerView(context, height, width);

        ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.graffiti_color_selector_dialog, null);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                mListener.colorChanged(imageView.getDrawable());
                dismiss();
            }
        };

        ViewGroup shaderContainer = (ViewGroup) viewGroup.findViewById(R.id.graffiti_shader_container);
        for (int i = 0; i < shaderContainer.getChildCount(); i++) {
            shaderContainer.getChildAt(i).setOnClickListener(listener);
        }

        ViewGroup container = (ViewGroup) viewGroup.findViewById(R.id.graffiti_color_selector_container);
        container.addView(myView, 0, new ViewGroup.LayoutParams(height, width));

        setContentView(viewGroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setCanceledOnTouchOutside(true);
    }

    public interface OnColorChangedListener {

        void colorChanged(int color);

        void colorChanged(Drawable color);
    }
}  
