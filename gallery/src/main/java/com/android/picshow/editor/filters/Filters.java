package com.android.picshow.editor.filters;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class Filters {

    private int filterType = 0;
    private int[] pixels;

    private NativeFilter nativeFilters;

    private int width, height;

    public Filters(Bitmap bitmap, int type) {
        this.filterType = type;
        nativeFilters = new NativeFilter();

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public Bitmap process(float factor) {
        int[] newPixels = null;

        switch (filterType) {
            case FilterType.FILTER4BlackWhite:

                break;

            case FilterType.FILTER4BROWN:

                newPixels = nativeFilters
                        .brown(pixels, width, height, factor);
                break;

            case FilterType.FILTER4CARVING:
                break;

            case FilterType.FILTER4COMICS:
                newPixels = nativeFilters.comics(pixels, width, height,
                        factor);

                break;

            case FilterType.FILTER4GRAY:
                newPixels = nativeFilters.gray(pixels, width, height, factor);
                break;

            case FilterType.FILTER4LOMO:
                newPixels = nativeFilters.lomo(pixels, width, height, factor);
                break;

            case FilterType.FILTER4MOSATIC:
                int mosatic = (int) (factor * 30);
                newPixels = nativeFilters.mosatic(pixels, width, height,
                        mosatic);
                break;

            case FilterType.FILTER4NEGATIVE:

                break;

            case FilterType.FILTER4NiHong:
                newPixels = nativeFilters.neon(pixels, width, height);
                break;

            case FilterType.FILTER4NOSTALGIC:
                newPixels = nativeFilters.nostalgic(pixels, width,
                        height, factor);
                break;

            case FilterType.FILTER4OVEREXPOSURE:
                break;

            case FilterType.FILTER4RELIEF:
                break;

            case FilterType.FILTER4RUIHUA:
                break;

            case FilterType.FILTER4SKETCH:
                break;

            case FilterType.FILTER4SKETCH_PENCIL:
                newPixels = nativeFilters.sketchPencil(pixels, width, height,
                        factor);
                break;

            case FilterType.FILTER4SOFTNESS:
                break;

            case FilterType.FILTER4WHITELOG:
                break;

            default:
                newPixels = pixels;
                break;
        }

        if(newPixels == null)
            return null;
        Bitmap newBitmap = Bitmap.createBitmap(newPixels, width, height,
                Config.ARGB_8888);
        return newBitmap;
    }

    public void setFilterType(int type) {
        this.filterType = type;
    }

}
