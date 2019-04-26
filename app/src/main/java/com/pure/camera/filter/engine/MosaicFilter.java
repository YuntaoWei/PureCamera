package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

public class MosaicFilter extends AbstractFilter {

    public final static String NAME = "MosaicFilter";
    private final static int TYPE = 3;

    public MosaicFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }
}
