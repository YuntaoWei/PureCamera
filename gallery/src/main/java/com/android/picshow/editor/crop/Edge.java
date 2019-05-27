package com.android.picshow.editor.crop;

import android.graphics.Rect;
import android.view.View;


public enum Edge {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    public static final int MIN_CROP_LENGTH_PX = 40;

    private float mCoordinate;

    public void setCoordinate(float coordinate) {
        mCoordinate = coordinate;
    }

    public void offset(float distance) {
        mCoordinate += distance;
    }

    public float getCoordinate() {
        return mCoordinate;
    }

    public void adjustCoordinate(float x, float y, Rect imageRect, float imageSnapRadius, float aspectRatio) {

        switch (this) {
            case LEFT:
                mCoordinate = adjustLeft(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case TOP:
                mCoordinate = adjustTop(y, imageRect, imageSnapRadius, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = adjustRight(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = adjustBottom(y, imageRect, imageSnapRadius, aspectRatio);
                break;
        }
    }


    public void adjustCoordinate(float aspectRatio) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        switch (this) {
            case LEFT:
                mCoordinate = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);
                break;
            case TOP:
                mCoordinate = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);
                break;
        }
    }

    public boolean isNewRectangleOutOfBounds(Edge edge, Rect imageRect, float aspectRatio) {

        float offset = edge.snapOffset(imageRect);

        switch (this) {
            case LEFT:
                if (edge.equals(Edge.TOP)) {
                    float top = imageRect.top;
                    float bottom = Edge.BOTTOM.getCoordinate() - offset;
                    float right = Edge.RIGHT.getCoordinate();
                    float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.BOTTOM)) {
                    float bottom = imageRect.bottom;
                    float top = Edge.TOP.getCoordinate() - offset;
                    float right = Edge.RIGHT.getCoordinate();
                    float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;

            case TOP:
                if (edge.equals(Edge.LEFT)) {
                    float left = imageRect.left;
                    float right = Edge.RIGHT.getCoordinate() - offset;
                    float bottom = Edge.BOTTOM.getCoordinate();
                    float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.RIGHT)) {
                    float right = imageRect.right;
                    float left = Edge.LEFT.getCoordinate() - offset;
                    float bottom = Edge.BOTTOM.getCoordinate();
                    float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;

            case RIGHT:
                if (edge.equals(Edge.TOP)) {
                    float top = imageRect.top;
                    float bottom = Edge.BOTTOM.getCoordinate() - offset;
                    float left = Edge.LEFT.getCoordinate();
                    float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.BOTTOM)) {
                    float bottom = imageRect.bottom;
                    float top = Edge.TOP.getCoordinate() - offset;
                    float left = Edge.LEFT.getCoordinate();
                    float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;


            case BOTTOM:
                if (edge.equals(Edge.LEFT)) {
                    float left = imageRect.left;
                    float right = Edge.RIGHT.getCoordinate() - offset;
                    float top = Edge.TOP.getCoordinate();
                    float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                } else if (edge.equals(Edge.RIGHT)) {
                    float right = imageRect.right;
                    float left = Edge.LEFT.getCoordinate() - offset;
                    float top = Edge.TOP.getCoordinate();
                    float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);

                }
                break;
        }
        return true;
    }

    private boolean isOutOfBounds(float top, float left, float bottom, float right, Rect imageRect) {
        return (top < imageRect.top || left < imageRect.left || bottom > imageRect.bottom || right > imageRect.right);
    }

    public float snapToRect(Rect imageRect) {

        final float oldCoordinate = mCoordinate;

        switch (this) {
            case LEFT:
                mCoordinate = imageRect.left;
                break;
            case TOP:
                mCoordinate = imageRect.top;
                break;
            case RIGHT:
                mCoordinate = imageRect.right;
                break;
            case BOTTOM:
                mCoordinate = imageRect.bottom;
                break;
        }

        final float offset = mCoordinate - oldCoordinate;
        return offset;
    }

    public float snapOffset(Rect imageRect) {

        final float oldCoordinate = mCoordinate;
        float newCoordinate = oldCoordinate;

        switch (this) {
            case LEFT:
                newCoordinate = imageRect.left;
                break;
            case TOP:
                newCoordinate = imageRect.top;
                break;
            case RIGHT:
                newCoordinate = imageRect.right;
                break;
            case BOTTOM:
                newCoordinate = imageRect.bottom;
                break;
        }

        final float offset = newCoordinate - oldCoordinate;
        return offset;
    }

    public void snapToView(View view) {

        switch (this) {
            case LEFT:
                mCoordinate = 0;
                break;
            case TOP:
                mCoordinate = 0;
                break;
            case RIGHT:
                mCoordinate = view.getWidth();
                break;
            case BOTTOM:
                mCoordinate = view.getHeight();
                break;
        }
    }

    public static float getWidth() {
        return Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate();
    }

    public static float getHeight() {
        return Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate();
    }

    public boolean isOutsideMargin(Rect rect, float margin) {

        boolean result = false;

        switch (this) {
            case LEFT:
                result = mCoordinate - rect.left < margin;
                break;
            case TOP:
                result = mCoordinate - rect.top < margin;
                break;
            case RIGHT:
                result = rect.right - mCoordinate < margin;
                break;
            case BOTTOM:
                result = rect.bottom - mCoordinate < margin;
                break;
        }
        return result;
    }

    public boolean isOutsideFrame(Rect rect) {

        double margin = 0;
        boolean result = false;

        switch (this) {
            case LEFT:
                result = mCoordinate - rect.left < margin;
                break;
            case TOP:
                result = mCoordinate - rect.top < margin;
                break;
            case RIGHT:
                result = rect.right - mCoordinate < margin;
                break;
            case BOTTOM:
                result = rect.bottom - mCoordinate < margin;
                break;
        }
        return result;
    }

    private static float adjustLeft(float x, Rect imageRect, float imageSnapRadius, float aspectRatio) {

        float resultX = x;

        if (x - imageRect.left < imageSnapRadius)
            resultX = imageRect.left;

        else {
            float resultXHoriz = Float.POSITIVE_INFINITY;
            float resultXVert = Float.POSITIVE_INFINITY;

            if (x >= Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX)
                resultXHoriz = Edge.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX;

            if (((Edge.RIGHT.getCoordinate() - x) / aspectRatio) <= MIN_CROP_LENGTH_PX)
                resultXVert = Edge.RIGHT.getCoordinate() - (MIN_CROP_LENGTH_PX * aspectRatio);

            resultX = Math.min(resultX, Math.min(resultXHoriz, resultXVert));
        }
        return resultX;
    }

    private static float adjustRight(float x, Rect imageRect, float imageSnapRadius, float aspectRatio) {

        float resultX = x;

        if (imageRect.right - x < imageSnapRadius)
            resultX = imageRect.right;

        else {
            float resultXHoriz = Float.NEGATIVE_INFINITY;
            float resultXVert = Float.NEGATIVE_INFINITY;

            if (x <= Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX)
                resultXHoriz = Edge.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX;

            if (((x - Edge.LEFT.getCoordinate()) / aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultXVert = Edge.LEFT.getCoordinate() + (MIN_CROP_LENGTH_PX * aspectRatio);
            }

            resultX = Math.max(resultX, Math.max(resultXHoriz, resultXVert));

        }

        return resultX;
    }

    private static float adjustTop(float y, Rect imageRect, float imageSnapRadius, float aspectRatio) {

        float resultY = y;

        if (y - imageRect.top < imageSnapRadius)
            resultY = imageRect.top;

        else {
            float resultYVert = Float.POSITIVE_INFINITY;
            float resultYHoriz = Float.POSITIVE_INFINITY;

            if (y >= Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX)
                resultYHoriz = Edge.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX;

            if (((Edge.BOTTOM.getCoordinate() - y) * aspectRatio) <= MIN_CROP_LENGTH_PX)
                resultYVert = Edge.BOTTOM.getCoordinate() - (MIN_CROP_LENGTH_PX / aspectRatio);

            resultY = Math.min(resultY, Math.min(resultYHoriz, resultYVert));

        }

        return resultY;
    }

    private static float adjustBottom(float y, Rect imageRect, float imageSnapRadius, float aspectRatio) {

        float resultY = y;

        if (imageRect.bottom - y < imageSnapRadius)
            resultY = imageRect.bottom;
        else {
            float resultYVert = Float.NEGATIVE_INFINITY;
            float resultYHoriz = Float.NEGATIVE_INFINITY;

            if (y <= Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_PX)
                resultYVert = Edge.TOP.getCoordinate() + MIN_CROP_LENGTH_PX;

            if (((y - Edge.TOP.getCoordinate()) * aspectRatio) <= MIN_CROP_LENGTH_PX)
                resultYHoriz = Edge.TOP.getCoordinate() + (MIN_CROP_LENGTH_PX / aspectRatio);

            resultY = Math.max(resultY, Math.max(resultYHoriz, resultYVert));
        }

        return resultY;
    }
}
