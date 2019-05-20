package com.pure.camera.opengl.data;

import com.pure.camera.Constants;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.opengl.program.CameraShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

public class FullPreview {
    private static final String TAG = "FullPreview";

    //vertex coordinate(x, y)  -  texture coordinate(s, t)
    private final float[] VERTEX_COORDINATES = {
            //x, y, s, t
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    CameraShaderProgram shaderProgram;
    VertexArray previewVertexArray;
    BaseFilter cameraFilter;
    PreviewSize previewSize = new PreviewSize(1520, 720);

    public FullPreview(CameraShaderProgram program) {
        previewVertexArray = new VertexArray(VERTEX_COORDINATES);
        shaderProgram = program;
    }

    /**
     * 更新当前应用的滤镜.
     * @param filter 新的滤镜.
     */
    public void setCameraPreviewFilter(BaseFilter filter) {
        if(cameraFilter == filter)
            return;

        cameraFilter = filter;
    }

    public void setPreviewSize(int w, int h) {
        previewSize.width = h;
        previewSize.height = h;
    }

    public void draw(int textureID, float[] textureMatrix) {
        shaderProgram.useProgram();
        previewVertexArray.setVertexAttribPointer(0, shaderProgram.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        previewVertexArray.setVertexAttribPointer(2, shaderProgram.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);

        shaderProgram.setMatrixUniform(textureMatrix);
        shaderProgram.setTextureUniformExternal(textureID);

        if(null != cameraFilter) {
            cameraFilter.doFilter(shaderProgram);
            LogPrinter.i(TAG, "do filter : " + cameraFilter.toString() + "  " + previewSize);
            shaderProgram.setUniform(shaderProgram.getTextureWidthHandler(), (float)previewSize.width);
            shaderProgram.setUniform(shaderProgram.getTextureHeightHandler(), (float)previewSize.height);
        }

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
