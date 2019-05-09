package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

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
    public byte[] getFilterImage(byte[] pixels, int w, int h) {
        return NativeFilter.getInstance().doFilterGray(pixels, w, h);
    }
}
