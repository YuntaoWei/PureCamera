package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

/**
 * 浮雕滤镜
 */
public class ReliefFilter extends AbstractFilter {

    public final static String NAME = "ReliefFilter";
    private final static int TYPE = 2;

    public ReliefFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }

    @Override
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doFilterRelief(pixels, w, h, orientation, filePath);
    }
}
