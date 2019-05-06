package com.pure.camera.filter.engine;

import com.pure.camera.common.LogPrinter;
import com.pure.camera.opengl.program.CameraShaderProgram;

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
}
