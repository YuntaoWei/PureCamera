package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

public class PositiveColorFilter extends AbstractFilter {
    public static final String NAME = "PositiveColorFilter";
    private static final int TYPE = 5;

    public PositiveColorFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }

    @Override
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doFilterPositive(pixels, w, h, orientation, filePath);
    }
}
