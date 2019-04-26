package com.pure.camera.filter.engine;

import com.pure.camera.filter.BaseFilter;
import com.pure.camera.opengl.program.CameraShaderProgram;

public abstract class AbstractFilter implements BaseFilter {

    protected CameraShaderProgram mProgram;
    protected String filterName;
    protected int filterType;

    public AbstractFilter(String name, int type) {
        filterName = name;
        filterType = type;
    }

    protected abstract void doFilterImpl(CameraShaderProgram program);

    @Override
    public String getFilterName() {
        return filterName;
    }

    @Override
    public int getFilterType() {
        return filterType;
    }

    @Override
    public void doFilter(CameraShaderProgram program) {
        doFilterImpl(program);
    }

    @Override
    public String toString() {
        return "Name : " + filterName + ", Type : " + filterType;
    }
}
