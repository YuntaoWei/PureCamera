package com.android.picshow.editor.filters;

import android.content.Context;
import android.graphics.Bitmap;

import com.pure.commonbase.LogPrinter;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSmoothToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;

/**
 * Created by yuntao.wei on 2018/5/23.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public final class NewFilters {

    private static final String TAG = "NewFilters";

    private GPUImage gpuImage;

    private static NewFilters mInstance;

    private NewFilters(Context ctx) {
        gpuImage = new GPUImage(ctx);
    }

    public static NewFilters getInstance(Context ctx) {
        if (mInstance != null)
            return mInstance;
        else {
            synchronized (NewFilters.class) {
                if (mInstance == null)
                    mInstance = new NewFilters(ctx);
                return mInstance;
            }
        }
    }

    public Bitmap filterImage(Context ctx, Bitmap bm, int type) {
        if(gpuImage != null) {
        }
        gpuImage.setImage(bm);

        LogPrinter.i(TAG,"filterImage : " + type);

        switch (type) {
            case FilterType.GAUSSIAN_BLUR_FILTER:
                gpuImage.setFilter(new GPUImageGaussianBlurFilter());
                break;

            case FilterType.GAUSSIAN_SELECT_BLUR_FILTER:
                gpuImage.setFilter(new GPUImageGlassSphereFilter());
                break;

            case FilterType.SOBEL_EDGE_DETECTION_FILTER:
                gpuImage.setFilter(new GPUImageSobelEdgeDetection());
                break;

            case FilterType.LOW_PASS_FILTER:
                gpuImage.setFilter(new GPUImageBrightnessFilter(7));
                break;

            case FilterType.SKETCH_FILTER:
                gpuImage.setFilter(new GPUImageSketchFilter());
                break;

            case FilterType.TOON_FILTER:
                LogPrinter.i(TAG, "TOON_FILTER");
                gpuImage.setFilter(new GPUImageToonFilter());
                break;

            case FilterType.SMOOTH_TOON_FILTER:
                LogPrinter.i(TAG, "SMOOTH_TOON_FILTER");
                gpuImage.setFilter(new GPUImageSmoothToonFilter());
                break;

            case FilterType.MOSAIC_FILTER:
                gpuImage.setFilter(new GPUImageMonochromeFilter());
                break;

            case FilterType.GRAY_FILTER:
                gpuImage.setFilter(new GPUImageGrayscaleFilter());
                break;
        }

        return gpuImage.getBitmapWithFilterApplied();
    }

    public final static class FilterType {

        // all blur filter
        public final static int GRAY_FILTER = 0x0;
        public final static int FAST_BLUR_FILER = 0x1;
        public final static int GAUSSIAN_BLUR_FILTER = 0X2;
        public final static int GAUSSIAN_SELECT_BLUR_FILTER = 0x3;
        public final static int BOX_BLUR_FILTER = 0X4;
        public final static int TILT_SHIFT_FILTER = 0x5;
        public final static int MEDIAN_FILTER = 0X6;
        public final static int BILATERAL_FILTER = 0x7;
        public final static int EROSION_FILTER = 0x8;
        public final static int RGB_EROSION_FILTER = 0x9;
        public final static int DILATION_FILTER = 0xA;
        public final static int RGB_DILATION_FILTER = 0xA;
        public final static int OPENING_FILTER = 0xB;
        public final static int RGB_OPENING_FILTER = 0xC;
        public final static int CLOSING_FILTER = 0xD;
        public final static int RGB_CLOSING_FILTER = 0xE;
        public final static int LANCZOS_RESAMPLING_FILTER = 0xF;
        public final static int NON_MAXIMUM_SUPPRESSION_FILTER = 0x10;
        public final static int THRESHOLDED_NON_MAXIMUM_SUPPRESSION_FILTER = 0x11;

        //all Linear change filter
        public final static int CROSSHAIR_GENERATOR_FILTER = 0x12;
        public final static int LINE_GENERATOR = 0x13;
        public final static int TRANSFORM_FILTER = 0x14;
        public final static int CROP_FILTER = 0x15;
        public final static int SHARPEN_FILTER = 0x16;
        public final static int UNSHARP_MASK_FILTER = 0x17;

        //all other filter,like comic, pencil.etc
        public final static int SOBEL_EDGE_DETECTION_FILTER = 0x18;
        public final static int CANNY_EDGE_DETECTION_FILTER = 0x19;
        public final static int THRESHOLD_EDGE_DETECTION_FILTER = 0x1A;
        public final static int PREWITT_EDGE_DETECTION_FILTER = 0x1B;
        public final static int XY_DERIVATIVE_FILTER = 0x1C;
        public final static int HARRIS_CORNER_DETECTION_FILTER = 0x1D;
        public final static int NOBLE_CORNER_DETECTION_FILTER = 0x1E;
        public final static int SHI_TOMASI_FEATURE_DETECTION_FILTER = 0x1F;
        public final static int MOTION_DETECTOR = 0x20;
        public final static int HOUGH_TRANSFORM_LINE_DETECTOR = 0x21;
        public final static int PARALLEL_COORDINATE_LINE_TRANSFORM_FILTER = 0x22;
        public final static int LOCAL_BINARY_PATTERN_FILTER = 0x23;
        public final static int LOW_PASS_FILTER = 0x24;
        public final static int HIGH_PASS_FILTER = 0x25;
        public final static int SKETCH_FILTER = 0x26;
        public final static int THRESHOLD_SKETCH_FILTER = 0x27;
        public final static int TOON_FILTER = 0x28;
        public final static int SMOOTH_TOON_FILTER = 0x29;
        public final static int KUWAHARA_FILTER = 0x2A;
        public final static int MOSAIC_FILTER = 0x2B;
        public final static int PIXELLATE_FILTER = 0x2C;
        public final static int POLAR_PIXELLATE_FILTER = 0x2D;
        public final static int CROSSHATCH_FILTER = 0x2E;
        public final static int COLOR_PACKING_FILTER = 0x2F;
        public final static int VIGNETTE_FILTER = 0x30;
        public final static int SWIRL_FILTER = 0x31;
        public final static int BULGE_DISTORTION_FILTER = 0x32;
        public final static int PINCH_DISTORTION_FILTER = 0x33;
        public final static int STRETCH_DISTORTION_FILTER = 0x34;
        public final static int GLASS_SPHERE_FILTER = 0x35;
        public final static int SPHERE_REFRACTION_FILTER = 0x36;
        public final static int POSTERIZE_FILTER = 0x37;
        public final static int CGA_COLORSPACE_FILTER = 0x38;
        public final static int PERLIN_NOISE_FILTER = 0x39;
        public final static int CONVOLUTION_3X3_FILTER = 0x3A;
        public final static int EMBOSS_FILTER = 0x3B;
        public final static int POLKA_DOTFILTER = 0x3C;
        public final static int HALFTONE_FILTER = 0x3D;

        //all blend mode filter
        public final static int MULTIPLY_BLEND_FILTER = 0x3E;
        public final static int NORMAL_BLEND_FILTER = 0x3F;
        public final static int ALPHA_BLEND_FILTER = 0x40;
        public final static int DISSOLVE_BLEND_FILTER = 0x41;
        public final static int OVERLAY_BLEND_FILTER = 0x42;
        public final static int DARKEN_BLEND_FILTER = 0x43;
        public final static int LIGHTEN_BLEND_FILTER = 0x44;
        public final static int SOURCE_OVER_BLEND_FILTER = 0x45;
        public final static int COLOR_BURN_BLEND_FILTER = 0x46;
        public final static int COLOR_DODGE_BLEND_FILTER = 0x47;
        public final static int SCREEN_BLEND_FILTER = 0x48;
        public final static int EXCLUSION_BLEND_FILTER = 0x49;
        public final static int DIFFERENCE_BLEND_FILTER = 0x4A;
        public final static int SUBTRACT_BLEND_FILTER = 0x4B;
        public final static int HARD_LIGHT_BLEND_FILTER = 0x4C;
        public final static int SOFT_LIGHT_BLEND_FILTER = 0x4D;
        public final static int CHROMAKEY_BLEND_FILTER = 0x4E;
        public final static int MASK_FILTER = 0x4F;
        public final static int HAZE_FILTER = 0x50;
        public final static int LUMINANCE_THRESHOLD_FILTER = 0x51;
        public final static int ADAPTIVE_THRESHOLD_FILTER = 0x52;
        public final static int ADD_BLEND_FILTER = 0x53;
        public final static int DIVIDE_BLEND_FILTER = 0x54;

        private final static int[] LESS_FILTER = new int[] {
                GRAY_FILTER,
                SMOOTH_TOON_FILTER,
                TOON_FILTER,
                GAUSSIAN_BLUR_FILTER,
                GAUSSIAN_SELECT_BLUR_FILTER,
                SOBEL_EDGE_DETECTION_FILTER,
                LOW_PASS_FILTER,
                SKETCH_FILTER,
                MOSAIC_FILTER,
        };

        private final static int[] SUPPORT_FILTERS = new int[] {
                GRAY_FILTER,
                GAUSSIAN_BLUR_FILTER,
                GAUSSIAN_SELECT_BLUR_FILTER,
                SOBEL_EDGE_DETECTION_FILTER,
                LOW_PASS_FILTER,
                SKETCH_FILTER,
                THRESHOLD_SKETCH_FILTER,
                TOON_FILTER,
                SMOOTH_TOON_FILTER,
                MOSAIC_FILTER,
                PIXELLATE_FILTER,
                CROSSHATCH_FILTER,
                SWIRL_FILTER,
                BULGE_DISTORTION_FILTER,
                PINCH_DISTORTION_FILTER,
                STRETCH_DISTORTION_FILTER,
                GLASS_SPHERE_FILTER,
                SPHERE_REFRACTION_FILTER,
                EMBOSS_FILTER,
                CGA_COLORSPACE_FILTER,
                MULTIPLY_BLEND_FILTER,
                DISSOLVE_BLEND_FILTER,
        };

        public static int[] getSupportFilters() {
            return LESS_FILTER;
        }

    }


}
