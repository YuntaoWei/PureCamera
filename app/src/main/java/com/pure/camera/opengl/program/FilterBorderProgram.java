package com.pure.camera.opengl.program;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4fv;

public class FilterBorderProgram extends Program {

    private static final String VERTEX_COORD_NAME = "vPosition";
    private static final String FRAG_COLOR_NAME = "fColor";

    private int vertextCoordHandler, fragColorHandler;

    public FilterBorderProgram(Context ctx, int vertexShaderRes, int fragShaderRes) {
        super(ctx, vertexShaderRes, fragShaderRes);
    }

    @Override
    public void initOpenGLEnvironment() {
        vertextCoordHandler = glGetAttribLocation(mProgram, VERTEX_COORD_NAME);
        fragColorHandler = glGetUniformLocation(mProgram, FRAG_COLOR_NAME);
    }

    @Override
    public int getVertexPositionHandler() {
        return vertextCoordHandler;
    }

    @Override
    public int getTexturePositionHandler() {
        return -1;
    }

    public void setUniformColor(float[] color) {
        glUniform4fv(fragColorHandler, 1, color, 0);
    }

    public void disablePointer() {
        disablePointer(vertextCoordHandler);
    }

}
