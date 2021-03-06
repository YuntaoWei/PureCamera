package com.pure.camera.filter.engine;

import android.graphics.Bitmap;

import com.pure.camera.opengl.program.CameraShaderProgram;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * 黑白滤镜
 */
public class GrayFilter extends AbstractFilter {

    public final static String NAME = "GrayFilter";
    private final static int TYPE = 1;

    public GrayFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }

    @Override
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doFilterGray(pixels, w, h, orientation, filePath);
    }
}
