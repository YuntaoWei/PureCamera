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
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doYuv2RGB(pixels, w, h, orientation, filePath);
    }
}
