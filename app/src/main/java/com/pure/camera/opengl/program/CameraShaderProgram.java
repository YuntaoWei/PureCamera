package com.pure.camera.opengl.program;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * GL Program类，主要处理与GL Program的交互，下发命令等。
 */
public class CameraShaderProgram extends Program {

    private static final String V_POSITION = "v_Position";
    private static final String V_MATRIX = "v_Matrix";
    private static final String VT_POSITION = "vt_Position";
    private static final String V_TEXTURE = "v_Texture";
    private static final String FILTER_TYPE = "filter_Type";
    private static final String TEXTURE_WIDTH = "texture_Width";
    private static final String TEXTURE_HEIGHT = "texture_Height";
    private static final String MOSAIC_SIZE = "mosaic_Size";

    private int vPositionHandler;
    private int vMatrixHandler;
    private int vtPositionHandler;
    private int vTextureHandler;
    private int filterHandler;
    private int textureWidthHandler;
    private int textureHeightHandler;
    private int mosaicSizeHandler;
    private float[] identity = new float[16];

    public CameraShaderProgram(Context ctx, int vertexShaderRes, int fragShaderRes) {
        super(ctx, vertexShaderRes, fragShaderRes);
    }

    @Override
    public void initOpenGLEnvironment() {
        vPositionHandler = glGetAttribLocation(mProgram, V_POSITION);
        vtPositionHandler = glGetAttribLocation(mProgram, VT_POSITION);

        vMatrixHandler = glGetUniformLocation(mProgram, V_MATRIX);
        vTextureHandler = glGetUniformLocation(mProgram, V_TEXTURE);
        filterHandler = glGetUniformLocation(mProgram, FILTER_TYPE);
        textureWidthHandler = glGetUniformLocation(mProgram, TEXTURE_WIDTH);
        textureHeightHandler = glGetUniformLocation(mProgram, TEXTURE_HEIGHT);
        mosaicSizeHandler = glGetUniformLocation(mProgram, MOSAIC_SIZE);
    }

    @Override
    public int getVertexPositionHandler() {
        return vPositionHandler;
    }

    @Override
    public int getTexturePositionHandler() {
        return vtPositionHandler;
    }

    public int getVPositionHandler() {
        return vPositionHandler;
    }

    public int getVtPositionHandler() {
        return vtPositionHandler;
    }

    public int getFilterHandler() {
        return filterHandler;
    }

    public int getTextureWidthHandler() {
        return textureWidthHandler;
    }

    public int getTextureHeightHandler() {
        return textureHeightHandler;
    }

    public int getMosaicSizeHandler() {
        return mosaicSizeHandler;
    }

    public void setTextureUniformExternal(int textureID) {
        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        glUniform1i(vTextureHandler, 0);
    }

    public void setTextureUniformLocal(int textureID) {
        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(vTextureHandler, 0);
    }

    public void setMatrixUniform(float[] matrix) {
        setMatrix(vMatrixHandler, matrix);
    }

    public void setFilterType(int value) {
        setUniform(filterHandler, value);
    }

    @Override
    public String toString() {
        return "CameraShaderProgram{" +
                "mProgram=" + mProgram +
                ", vPositionHandler=" + vPositionHandler +
                ", vMatrixHandler=" + vMatrixHandler +
                ", vtPositionHandler=" + vtPositionHandler +
                ", vTextureHandler=" + vTextureHandler +
                ", filterHandler=" + filterHandler +
                ", textureWidthHandler=" + textureWidthHandler +
                ", textureHeightHandler=" + textureHeightHandler +
                ", mosaicSizeHandler=" + mosaicSizeHandler +
                '}';
    }
}
