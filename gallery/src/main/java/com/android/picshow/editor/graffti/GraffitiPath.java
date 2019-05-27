package com.android.picshow.editor.graffti;

import android.graphics.Matrix;
import android.graphics.Path;

import static com.android.picshow.editor.graffti.DrawUtil.rotatePoint;
import static com.android.picshow.editor.graffti.DrawUtil.rotatePointInGraffiti;

public class GraffitiPath implements Undoable {
    GraffitiView.Pen mPen;
    GraffitiView.Shape mShape;
    float mStrokeWidth;
    GraffitiColor mColor;
    Path mPath;
    float mSx, mSy;
    float mDx, mDy;
    private Matrix mMatrix = new Matrix();
    int mRotateDegree = 0;
    float mPivotX, mPivotY;
    CopyLocation mCopy;

    public Path getPath(int currentDegree) {
        int degree = currentDegree - mRotateDegree;
        if (degree == 0) {
            return mPath;
        }
        Path path = new Path(mPath);
        Matrix matrix = new Matrix();

        float px = mPivotX, py = mPivotY;
        if (mRotateDegree == 90 || mRotateDegree == 270) {
            float t = px;
            px = py;
            py = t;
        }

        matrix.setRotate(degree, px, py);
        if (Math.abs(degree) == 90 || Math.abs(degree) == 270) {
            matrix.postTranslate((py - px), -(py - px));
        }
        path.transform(matrix);
        return path;
    }

    public float[] getDxDy(int currentDegree) {

        return rotatePointInGraffiti(currentDegree, mRotateDegree, mDx, mDy, mPivotX, mPivotY);
    }

    public float[] getSxSy(int currentDegree) {

        return rotatePointInGraffiti(currentDegree, mRotateDegree, mSx, mSy, mPivotX, mPivotY);
    }

    public Matrix getMatrix(int currentDegree) {
        if (mMatrix == null) {
            return null;
        }
        if (mPen == GraffitiView.Pen.COPY) {
            mMatrix.reset();

            int degree = currentDegree - mRotateDegree;
            if (degree == 0) {
                mMatrix.postTranslate(mCopy.getTouchStartX() - mCopy.getCopyStartX(), mCopy.getTouchStartY() - mCopy.getCopyStartY());
                return mMatrix;
            }
            float px = mPivotX, py = mPivotY;
            if (mRotateDegree == 90 || mRotateDegree == 270) {
                float t = px;
                px = py;
                py = t;
            }
            float[] coords = rotatePoint(degree, mCopy.getTouchStartX(), mCopy.getTouchStartY(), px, py);
            float[] coordsCopy = rotatePoint(degree, mCopy.getCopyStartX(), mCopy.getCopyStartY(), px, py);
            if (Math.abs(degree) == 90 || Math.abs(degree) == 270) {
                coords[0] += (py - px);
                coords[1] += -(py - px);
                coordsCopy[0] += (py - px);
                coordsCopy[1] += -(py - px);
            }
            mMatrix.postTranslate(coords[0] - coordsCopy[0], coords[1] - coordsCopy[1]);
            return mMatrix;
        } else {
            return mMatrix;
        }

    }

    static GraffitiPath toShape(GraffitiView.Pen pen, GraffitiView.Shape shape, float width, GraffitiColor color,
                                float sx, float sy, float dx, float dy, int degree, float px, float py, CopyLocation copyLocation) {
        GraffitiPath path = new GraffitiPath();
        path.mPen = pen;
        path.mShape = shape;
        path.mStrokeWidth = width;
        path.mColor = color;
        path.mSx = sx;
        path.mSy = sy;
        path.mDx = dx;
        path.mDy = dy;
        path.mRotateDegree = degree;
        path.mPivotX = px;
        path.mPivotY = py;
        path.mCopy = copyLocation;
        return path;
    }

    static GraffitiPath toPath(GraffitiView.Pen pen, GraffitiView.Shape shape, float width, GraffitiColor color, Path p, int degree, float px, float py, CopyLocation copyLocation) {
        GraffitiPath path = new GraffitiPath();
        path.mPen = pen;
        path.mShape = shape;
        path.mStrokeWidth = width;
        path.mColor = color;
        path.mPath = p;
        path.mRotateDegree = degree;
        path.mPivotX = px;
        path.mPivotY = py;
        path.mCopy = copyLocation;
        return path;
    }

    @Override
    public int getType() {
        return 1;
    }
}

