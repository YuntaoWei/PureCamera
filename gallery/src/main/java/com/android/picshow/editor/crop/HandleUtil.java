package com.android.picshow.editor.crop;

import android.content.Context;
import android.util.Pair;
import android.util.TypedValue;


public class HandleUtil {

    private static final int TARGET_RADIUS_DP = 24;

    public static float getTargetRadius(Context context) {

        final float targetRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TARGET_RADIUS_DP,
                context.getResources().getDisplayMetrics());
        return targetRadius;
    }

    public static Handle getPressedHandle(float x,
                                          float y,
                                          float left,
                                          float top,
                                          float right,
                                          float bottom,
                                          float targetRadius) {

        Handle pressedHandle = null;

        if (HandleUtil.isInCornerTargetZone(x, y, left, top, targetRadius)) {
            pressedHandle = Handle.TOP_LEFT;
        } else if (HandleUtil.isInCornerTargetZone(x, y, right, top, targetRadius)) {
            pressedHandle = Handle.TOP_RIGHT;
        } else if (HandleUtil.isInCornerTargetZone(x, y, left, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM_LEFT;
        } else if (HandleUtil.isInCornerTargetZone(x, y, right, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM_RIGHT;
        } else if (HandleUtil.isInCenterTargetZone(x, y, left, top, right, bottom) && focusCenter()) {
            pressedHandle = Handle.CENTER;
        } else if (HandleUtil.isInHorizontalTargetZone(x, y, left, right, top, targetRadius)) {
            pressedHandle = Handle.TOP;
        } else if (HandleUtil.isInHorizontalTargetZone(x, y, left, right, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM;
        } else if (HandleUtil.isInVerticalTargetZone(x, y, left, top, bottom, targetRadius)) {
            pressedHandle = Handle.LEFT;
        } else if (HandleUtil.isInVerticalTargetZone(x, y, right, top, bottom, targetRadius)) {
            pressedHandle = Handle.RIGHT;
        } else if (HandleUtil.isInCenterTargetZone(x, y, left, top, right, bottom) && !focusCenter()) {
            pressedHandle = Handle.CENTER;

        }

        return pressedHandle;
    }

    public static Pair<Float, Float> getOffset(Handle handle,
                                               float x,
                                               float y,
                                               float left,
                                               float top,
                                               float right,
                                               float bottom) {

        if (handle == null) {
            return null;
        }

        float touchOffsetX = 0;
        float touchOffsetY = 0;

        switch (handle) {

            case TOP_LEFT:
                touchOffsetX = left - x;
                touchOffsetY = top - y;
                break;
            case TOP_RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = top - y;
                break;
            case BOTTOM_LEFT:
                touchOffsetX = left - x;
                touchOffsetY = bottom - y;
                break;
            case BOTTOM_RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = bottom - y;
                break;
            case LEFT:
                touchOffsetX = left - x;
                touchOffsetY = 0;
                break;
            case TOP:
                touchOffsetX = 0;
                touchOffsetY = top - y;
                break;
            case RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = 0;
                break;
            case BOTTOM:
                touchOffsetX = 0;
                touchOffsetY = bottom - y;
                break;
            case CENTER:
                final float centerX = (right + left) / 2;
                final float centerY = (top + bottom) / 2;
                touchOffsetX = centerX - x;
                touchOffsetY = centerY - y;
                break;
        }

        final Pair<Float, Float> result = new Pair<Float, Float>(touchOffsetX, touchOffsetY);
        return result;
    }

    private static boolean isInCornerTargetZone(float x,
                                                float y,
                                                float handleX,
                                                float handleY,
                                                float targetRadius) {

        if (Math.abs(x - handleX) <= targetRadius && Math.abs(y - handleY) <= targetRadius) {
            return true;
        }
        return false;
    }

    private static boolean isInHorizontalTargetZone(float x,
                                                    float y,
                                                    float handleXStart,
                                                    float handleXEnd,
                                                    float handleY,
                                                    float targetRadius) {

        if (x > handleXStart && x < handleXEnd && Math.abs(y - handleY) <= targetRadius) {
            return true;
        }
        return false;
    }

    private static boolean isInVerticalTargetZone(float x,
                                                  float y,
                                                  float handleX,
                                                  float handleYStart,
                                                  float handleYEnd,
                                                  float targetRadius) {

        if (Math.abs(x - handleX) <= targetRadius && y > handleYStart && y < handleYEnd) {
            return true;
        }
        return false;
    }

    private static boolean isInCenterTargetZone(float x,
                                                float y,
                                                float left,
                                                float top,
                                                float right,
                                                float bottom) {

        if (x > left && x < right && y > top && y < bottom) {
            return true;
        }
        return false;
    }

    private static boolean focusCenter() {
        return (!CropOverlayView.showGuidelines());
    }
}
