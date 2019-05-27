package com.android.picshow.editor.scrawl;

import android.content.Context;

import com.android.picshow.R;


/**
 * Created by yuntao.wei on 2018/5/18.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PaintUtils {

    public static final int COLOR_WHITE = 0X1;
    public static final int COLOR_GRAY = 0X2;
    public static final int COLOR_BLACK = 0X3;
    public static final int COLOR_ORANGE = 0X4;
    public static final int COLOR_YELLOW = 0X5;
    public static final int COLOR_GREEN = 0X6;
    public static final int COLOR_BLUE = 0X7;
    public static final int COLOR_RED = 0X8;


    public static int getColor(Context ctx, int colorIndex) {
        switch (colorIndex) {

            case COLOR_WHITE:
                return ctx.getResources().getColor(R.color.paint_white);

            case COLOR_GRAY:
                return ctx.getResources().getColor(R.color.paint_gray);

            case COLOR_BLACK:
                return ctx.getResources().getColor(R.color.paint_black);

            case COLOR_ORANGE:
                return ctx.getResources().getColor(R.color.paint_orange);

            case COLOR_YELLOW:
                return ctx.getResources().getColor(R.color.paint_yellow);

            case COLOR_GREEN:
                return ctx.getResources().getColor(R.color.paint_green);

            case COLOR_BLUE:
                return ctx.getResources().getColor(R.color.paint_blue);

            case COLOR_RED:
                return ctx.getResources().getColor(R.color.paint_red);

        }
        return 0xFFFFFFFF;
    }

    public static int getColorPickerBackground(int colorIndex) {
        switch (colorIndex) {

            case COLOR_WHITE:
                return R.drawable.circle_button_white;

            case COLOR_GRAY:
                return R.drawable.circle_button_gray;

            case COLOR_BLACK:
                return R.drawable.circle_button_black;

            case COLOR_ORANGE:
                return R.drawable.circle_button_orange;

            case COLOR_YELLOW:
                return R.drawable.circle_button_yellow;

            case COLOR_GREEN:
                return R.drawable.circle_button_green;

            case COLOR_BLUE:
                return R.drawable.circle_button_blue;

            case COLOR_RED:
                return R.drawable.circle_button_red;

        }
        return R.drawable.circle_button_red;
    }


}
