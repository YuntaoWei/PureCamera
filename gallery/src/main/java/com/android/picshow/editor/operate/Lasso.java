package com.android.picshow.editor.operate;

import android.graphics.PointF;
import android.util.Log;

import java.util.List;

public class Lasso {

    private static final String TAG = Lasso.class.getSimpleName();

    private float[] mPolyX, mPolyY;

    private int mPolySize;

    public Lasso(List<PointF> pointFs) {
        this.mPolySize = pointFs.size();

        this.mPolyX = new float[this.mPolySize];
        this.mPolyY = new float[this.mPolySize];

        for (int i = 0; i < this.mPolySize; i++) {
            this.mPolyX[i] = pointFs.get(i).x;
            this.mPolyY[i] = pointFs.get(i).y;
        }

        Log.d(TAG, "lasso size:" + mPolySize);
    }

    public boolean contains(float x, float y) {
        boolean result = false;

        for (int i = 0, j = mPolySize - 1; i < mPolySize; j = i++) {
            if ((mPolyY[i] < y && mPolyY[j] >= y)
                    || (mPolyY[j] < y && mPolyY[i] >= y)) {
                if (mPolyX[i] + (y - mPolyY[i]) / (mPolyY[j] - mPolyY[i])
                        * (mPolyX[j] - mPolyX[i]) < x) {
                    result = !result;
                }
            }
        }
        return result;
    }
}
