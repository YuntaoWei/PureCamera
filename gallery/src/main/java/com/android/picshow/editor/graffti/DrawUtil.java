package com.android.picshow.editor.graffti;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;


public class DrawUtil {

    public static float GRAFFITI_PIXEL_UNIT = 1;

    public static void drawArrow(Canvas canvas, float sx, float sy, float ex,
                                 float ey, Paint paint) {
        float arrowSize = paint.getStrokeWidth();
        double H = arrowSize;
        double L = arrowSize / 2;

        double awrad = Math.atan(L / 2 / H);
        double arraow_len = Math.sqrt(L / 2 * L / 2 + H * H) - 5;
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true,
                arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true,
                arraow_len);
        float x_3 = (float) (ex - arrXY_1[0]);
        float y_3 = (float) (ey - arrXY_1[1]);
        float x_4 = (float) (ex - arrXY_2[0]);
        float y_4 = (float) (ey - arrXY_2[1]);

        Path linePath = new Path();
        linePath.moveTo(sx, sy);
        linePath.lineTo(x_3, y_3);
        linePath.lineTo(x_4, y_4);
        linePath.close();
        canvas.drawPath(linePath, paint);

        awrad = Math.atan(L / H);
        arraow_len = Math.sqrt(L * L + H * H);
        arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        x_3 = (float) (ex - arrXY_1[0]);
        y_3 = (float) (ey - arrXY_1[1]);
        x_4 = (float) (ex - arrXY_2[0]);
        y_4 = (float) (ey - arrXY_2[1]);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x_3, y_3);
        triangle.lineTo(x_4, y_4);
        triangle.close();
        canvas.drawPath(triangle, paint);
    }

    public static double[] rotateVec(float px, float py, double ang,
                                     boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
        }
        mathstr[0] = vx;
        mathstr[1] = vy;
        return mathstr;
    }

    public static void drawLine(Canvas canvas, float sx, float sy, float dx, float dy, Paint paint) {
        canvas.drawLine(sx, sy, dx, dy, paint);
    }

    public static void drawCircle(Canvas canvas, float cx, float cy, float radius, Paint paint) {
        canvas.drawCircle(cx, cy, radius, paint);
    }

    public static void drawRect(Canvas canvas, float sx, float sy, float dx, float dy, Paint paint) {

        if (sx < dx) {
            if (sy < dy) {
                canvas.drawRect(sx, sy, dx, dy, paint);
            } else {
                canvas.drawRect(sx, dy, dx, sy, paint);
            }
        } else {
            if (sy < dy) {
                canvas.drawRect(dx, sy, sx, dy, paint);
            } else {
                canvas.drawRect(dx, dy, sx, sy, paint);
            }
        }
    }

    public static float computeAngle(float px1, float py1, float px2, float py2) {

        float x = px2 - px1;
        float y = py2 - py1;

        float arc = (float) Math.atan(y / x);

        float angle = (float) (arc / (Math.PI * 2) * 360);

        if (x >= 0 && y == 0) {
            angle = 0;
        } else if (x < 0 && y == 0) {
            angle = 180;
        } else if (x == 0 && y > 0) {
            angle = 90;
        } else if (x == 0 && y < 0) {
            angle = 270;
        } else if (x > 0 && y > 0) { // 1

        } else if (x < 0 && y > 0) { //2
            angle = 180 + angle;
        } else if (x < 0 && y < 0) { //3
            angle = 180 + angle;
        } else if (x > 0 && y < 0) { //4
            angle = 360 + angle;
        }

        return angle;
    }

    public static float[] restoreRotatePointInGraffiti(int nowDegree, int oldDegree, float x, float y, float mOriginalPivotX, float mOriginalPivotY) {
        int degree = nowDegree - oldDegree;
        if (degree != 0) {
            float px = mOriginalPivotX, py = mOriginalPivotY;
            if (oldDegree == 90 || oldDegree == 270) {
                float t = px;
                px = py;
                py = t;
            }
            if (Math.abs(degree) == 90 || Math.abs(degree) == 270) {
                x -= (py - px);
                y -= -(py - px);
            }

            float[] coords = rotatePoint(-degree, x,
                    y, px, py);

            return coords;
        }
        return new float[]{x, y};
    }

    public static float[] rotatePoint(int degree, float x, float y, float px, float py) {
        float[] coords = new float[2];
        float radian = (float) (degree * Math.PI / 180);
        coords[0] = (float) ((x - px) * Math.cos(radian) - (y - py) * Math.sin(radian) + px);
        coords[1] = (float) ((x - px) * Math.sin(radian) + (y - py) * Math.cos(radian) + py);

        return coords;
    }

    public static float[] rotatePointInGraffiti(int nowDegree, int oldDegree, float x, float y, float mOriginalPivotX, float mOriginalPivotY) {
        int degree = nowDegree - oldDegree;
        if (degree != 0) {
            float px = mOriginalPivotX, py = mOriginalPivotY;
            if (oldDegree == 90 || oldDegree == 270) {
                float t = px;
                px = py;
                py = t;
            }

            float[] coords = rotatePoint(degree, x,
                    y, px, py);
            if (Math.abs(degree) == 90 || Math.abs(degree) == 270) {
                coords[0] += (py - px);
                coords[1] += -(py - px);
            }
            return coords;
        }
        return new float[]{x, y};
    }

    public static float getGraffitiPixelUnit() {
        return GRAFFITI_PIXEL_UNIT;
    }

    public static void setGraffitiPixelUnit(float graffitiPixelUnit) {
        DrawUtil.GRAFFITI_PIXEL_UNIT = graffitiPixelUnit;
    }

    public static void main(String[] args) {

    }
}
