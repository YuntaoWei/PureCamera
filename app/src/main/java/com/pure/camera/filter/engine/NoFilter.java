package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

/**
 * 无滤镜效果
 */
public class NoFilter extends AbstractFilter {
    public final static String NAME = "NoFilter";
    private final static int TYPE = 0;

    public NoFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(0);
    }

    @Override
    public byte[] getFilterImage(byte[] pixels, int w, int h) {
        return pixels;
    }
}
