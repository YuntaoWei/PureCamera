package com.pure.camera.common;

import android.content.Context;

public class SizeUtil {

    public static int dpToPixel(Context ctx, int dip) {
        float density = (float)ctx.getResources().getConfiguration().densityDpi / 160f;
        return (int) (dip * density);
    }
}
