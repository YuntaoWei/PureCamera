package com.pure.camera.filter.engine;

import com.pure.camera.filter.BaseFilter;
import com.pure.camera.opengl.program.CameraShaderProgram;

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
}
