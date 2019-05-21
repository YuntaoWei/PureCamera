package com.pure.camera.opengl.program;

import android.content.Context;
import android.opengl.Matrix;

import com.pure.camera.opengl.glutil.ShaderHelper;
import com.pure.camera.opengl.glutil.TextResourceReader;

import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
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

    public void setMatrix(int matrixHandler, float[] matrix) {
        if(null == matrix) {
            float[] identity = new float[16];
            Matrix.setIdentityM(identity, 0);
            glUniformMatrix4fv(matrixHandler, 1, false, identity, 0);
        } else {
            glUniformMatrix4fv(matrixHandler, 1, false, matrix, 0);
        }
    }

    public void setUniform(int handler, int value) {
        glUniform1i(handler, value);
    }

    public void setUniform(int handler, float value) {
        glUniform1f(handler, value);
    }

    protected void disablePointer(int pointer) {
        glDisableVertexAttribArray(pointer);
    }

    public abstract void initOpenGLEnvironment();

    public abstract int getVertexPositionHandler();

    public abstract int getTexturePositionHandler();
}
