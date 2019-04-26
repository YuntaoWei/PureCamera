package com.pure.camera.filter;

import com.pure.camera.opengl.program.CameraShaderProgram;

public interface BaseFilter {

    String getFilterName();

    int getFilterType();

    void doFilter(CameraShaderProgram program);

}
