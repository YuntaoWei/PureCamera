package com.android.picshow.editor.crop;

import android.graphics.Rect;

public class AspectRatioUtil {

    public static float calculateAspectRatio(float left, float top, float right, float bottom) {

        final float width = right - left;
        final float height = bottom - top;
        final float aspectRatio = width / height;

        return aspectRatio;
    }

    public static float calculateAspectRatio(Rect rect) {

        final float aspectRatio = (float) rect.width() / (float) rect.height();

        return aspectRatio;
    }

    public static float calculateLeft(float top, float right, float bottom, float targetAspectRatio) {

        final float height = bottom - top;
        final float left = right - (targetAspectRatio * height);

        return left;
    }

    public static float calculateTop(float left, float right, float bottom, float targetAspectRatio) {

        final float width = right - left;
        final float top = bottom - (width / targetAspectRatio);

        return top;
    }

    public static float calculateRight(float left, float top, float bottom, float targetAspectRatio) {

        final float height = bottom - top;
        final float right = (targetAspectRatio * height) + left;

        return right;
    }

    public static float calculateBottom(float left, float top, float right, float targetAspectRatio) {

        final float width = right - left;
        final float bottom = (width / targetAspectRatio) + top;

        return bottom;
    }

    public static float calculateWidth(float top, float bottom, float targetAspectRatio) {

        final float height = bottom - top;
        final float width = targetAspectRatio * height;

        return width;
    }

    public static float calculateHeight(float left, float right, float targetAspectRatio) {

        final float width = right - left;
        final float height = width / targetAspectRatio;

        return height;
    }
}
