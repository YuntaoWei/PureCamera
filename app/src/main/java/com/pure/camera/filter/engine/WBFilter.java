package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

public class WBFilter extends AbstractFilter {
    public static final String NAME = "WBFilter";
    private final static int TYPE = 4;


    public WBFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }

    @Override
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doFilterWB(pixels, w, h, orientation, filePath);
    }
}
