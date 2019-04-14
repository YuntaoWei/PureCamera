package com.pure.camera.opengl.program;

import android.content.Context;

import com.pure.camera.util.ShaderHelper;
import com.pure.camera.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public abstract class Program {

    protected int mProgram;

    public Program(Context ctx, int vertexShaderRes, int fragShaderRes) {
        this(TextResourceReader.readTextFileFromResource(ctx, vertexShaderRes),
                TextResourceReader.readTextFileFromResource(ctx, fragShaderRes));
    }

    public Program(String vertexShaderSource, String fragShaderSource) {
        mProgram = ShaderHelper.buildGLProgram(vertexShaderSource, fragShaderSource);
        initOpenGLEnvironment();
    }

    public void useProgram() {
        glUseProgram(mProgram);
    }

    public abstract void initOpenGLEnvironment();

    public abstract int getVertexPositionHandler();

    public abstract int getTexturePositionHandler();
}
