package com.android.picshow.editor.enhance;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by yuntao.wei on 2018/5/17.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PhotoEnhance {

    public static final int Enhance_Saturation = 0;
    public static final int Enhance_Brightness = 1;
    public static final int Enhance_Contrast = 2;
    public static final int Enhance_ColorTemperature = 3;

    private Bitmap mBitmap;

    private float saturationNum = 1.0f;
    private float brightNum = 0.0f;
    private float contrastNum = 1.0f;
    private float preSaturation, preBrightness, preConstrast;

    public PhotoEnhance(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public float getSaturation() {
        return saturationNum;
    }

    public void setSaturation(int saturationNum) {
        this.saturationNum = (float) (saturationNum * 1.0f / 128);
    }

    public float getBrightness() {
        return brightNum;
    }

    public void setBrightness(int brightNum) {
        this.brightNum = brightNum - 128;
    }

    public float getContrast() {
        return contrastNum;
    }

    public void setContrast(int contrastNum) {
        this.contrastNum = (float) ((contrastNum / 2 + 64) / 128.0);
    }

    public void reset() {
        preSaturation = saturationNum;
        preConstrast = contrastNum;
        preBrightness = brightNum;
        saturationNum = 1.0f;
        brightNum = 0.0f;
        contrastNum = 1.0f;
    }

    public void restore() {
        saturationNum = preSaturation;
        brightNum = preBrightness;
        contrastNum = preConstrast;
    }

    private ColorMatrix mAllMatrix = null;
    private ColorMatrix saturationMatrix = null;
    private ColorMatrix contrastMatrix = null;
    private ColorMatrix brightnessMatrix = null;

    public Bitmap handleImage(int type) {

        Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (mAllMatrix == null) {
            mAllMatrix = new ColorMatrix();
        }

        if (saturationMatrix == null) {
            saturationMatrix = new ColorMatrix();
        }

        if (contrastMatrix == null) {
            contrastMatrix = new ColorMatrix();
        }

        if (brightnessMatrix == null) {
            brightnessMatrix = new ColorMatrix();
        }

        switch (type) {
            case Enhance_Saturation:
                saturationMatrix.reset();
                saturationMatrix.setSaturation(saturationNum);
                break;

            case Enhance_Brightness:
                brightnessMatrix.reset();
                brightnessMatrix.set(new float[]{1, 0, 0, 0, brightNum, 0, 1,
                        0, 0, brightNum, 0, 0, 1, 0, brightNum, 0, 0, 0, 1, 0});
                break;
            case Enhance_Contrast:

                float regulateBright = 0;
                regulateBright = (1 - contrastNum) * 128;

                contrastMatrix.reset();
                contrastMatrix.set(new float[]{contrastNum, 0, 0, 0,
                        regulateBright, 0, contrastNum, 0, 0, regulateBright,
                        0, 0, contrastNum, 0, regulateBright, 0, 0, 0, 1, 0});
                break;

            default:
                break;
        }

        mAllMatrix.reset();
        mAllMatrix.postConcat(saturationMatrix);
        mAllMatrix.postConcat(brightnessMatrix);
        mAllMatrix.postConcat(contrastMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        return bmp;

    }

}
