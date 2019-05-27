package com.android.picshow.editor.operate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.android.picshow.R;

public class OperateUtils {
    private Activity activity;
    private int screenWidth;
    private int screenHeight;

    public static final int LEFTTOP = 1;
    public static final int RIGHTTOP = 2;
    public static final int LEFTBOTTOM = 3;
    public static final int RIGHTBOTTOM = 4;
    public static final int CENTER = 5;

    public OperateUtils(Activity activity) {
        this.activity = activity;
        if (screenWidth == 0) {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            screenWidth = metric.widthPixels;
            screenHeight = metric.widthPixels;
        }
    }

/*    public Bitmap decodeBitmapAndScale(String filePath, View contentView) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, opt);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int layoutHeight = contentView.getHeight();
        float scale = 0f;
        scale = height > width
                ? layoutHeight / (height * 1f)
                : screenWidth / (width * 1f);

    }*/

    public Bitmap compressionFiller(String filePath, View contentView) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, opt);

        float scale = 0f;
        int layoutHeight = contentView.getHeight();
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        scale = bitmapHeight > bitmapWidth
                ? layoutHeight / (bitmapHeight * 1f)
                : screenWidth / (bitmapWidth * 1f);
        Bitmap resizeBmp;
        if (scale != 0) {
            int bitmapheight = bitmap.getHeight();
            int bitmapwidth = bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                    bitmapheight, matrix, true);
        } else {
            resizeBmp = bitmap;
        }
        return resizeBmp;
    }

    public Bitmap compressionFiller(Bitmap bitmap, View contentView) {
        if(contentView == null) {
            return null;
        }
        int layoutHeight = contentView.getHeight();
        float scale = 0f;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        scale = bitmapHeight > bitmapWidth
                ? layoutHeight / (bitmapHeight * 1f)
                : screenWidth / (bitmapWidth * 1f);
        Bitmap resizeBmp;
        if (scale != 0) {
            int bitmapheight = bitmap.getHeight();
            int bitmapwidth = bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                    bitmapheight, matrix, true);
        } else {
            resizeBmp = bitmap;
        }
        return resizeBmp;
    }

    public TextObject getTextObject(String text) {
        TextObject textObj = null;
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(activity, "请添加文字", Toast.LENGTH_SHORT).show();
            return null;
        }

        Bitmap rotateBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.rotate);
        Bitmap deleteBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.delete);

        textObj = new TextObject(activity, text, 150, 150, rotateBm, deleteBm);
        textObj.setTextObject(true);
        return textObj;
    }

    public TextObject getTextObject(String text, OperateView operateView,
                                    int quadrant, int x, int y) {
        TextObject textObj = null;
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(activity, "请添加文字", Toast.LENGTH_SHORT).show();
            return null;
        }
        int width = operateView.getWidth();
        int height = operateView.getHeight();
        switch (quadrant) {
            case LEFTTOP:
                break;
            case RIGHTTOP:
                x = width - x;
                break;
            case LEFTBOTTOM:
                y = height - y;
                break;
            case RIGHTBOTTOM:
                x = width - x;
                y = height - y;
                break;
            case CENTER:
                x = width / 2;
                y = height / 2;
                break;
            default:
                break;
        }
        Bitmap rotateBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.rotate);
        Bitmap deleteBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.delete);
        textObj = new TextObject(activity, text, x, y, rotateBm, deleteBm);
        textObj.setTextObject(true);
        return textObj;
    }

    public ImageObject getImageObject(Bitmap srcBmp) {
        Bitmap rotateBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.rotate);
        Bitmap deleteBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.delete);
        ImageObject imgObject = new ImageObject(srcBmp, rotateBm, deleteBm);
        Point point = new Point(20, 20);
        imgObject.setPoint(point);
        return imgObject;
    }

    public ImageObject getImageObject(Bitmap srcBmp, OperateView operateView,
                                      int quadrant, int x, int y) {
        Bitmap rotateBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.rotate);
        Bitmap deleteBm = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.delete);
        int width = operateView.getWidth();
        int height = operateView.getHeight();
        switch (quadrant) {
            case LEFTTOP:
                break;
            case RIGHTTOP:
                x = width - x;
                break;
            case LEFTBOTTOM:
                y = height - y;
                break;
            case RIGHTBOTTOM:
                x = width - x;
                y = height - y;
                break;
            case CENTER:
                x = width / 2;
                y = height / 2;
                break;
            default:
                break;
        }
        ImageObject imgObject = new ImageObject(srcBmp, x, y, rotateBm,
                deleteBm);
        Point point = new Point(20, 20);
        imgObject.setPoint(point);
        return imgObject;
    }

}
