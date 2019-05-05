package com.pure.camera.opengl.data;

import com.pure.camera.Constants;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.program.CameraFilterShaderProgram;

import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

public class SmallFilterPreview {

    CameraFilterShaderProgram filterShaderProgram;
    VertexArray filterVertex;
    List<BaseFilter> allFilters;
    PreviewSize previewSize = new PreviewSize(1520, 720);

    public SmallFilterPreview(CameraFilterShaderProgram program) {
        filterShaderProgram = program;
        allFilters = CameraFilterManager.getInstance().getAllFilter();
        initialVertex();
    }

    private void initialVertex() {

    }

    public void setPreviewSize(int w, int h) {
        previewSize.width = h;
        previewSize.height = h;
    }

    public void draw(int textureID, float[] textureMatrix) {
        filterShaderProgram.useProgram();
        for (BaseFilter filter : allFilters
                ) {
            filterVertex.setVertexAttribPointer(0, filterShaderProgram.getVPositionHandler(),
                    2, 4 * Constants.BYTES_PER_FLOAT);
            filterVertex.setVertexAttribPointer(2, filterShaderProgram.getVtPositionHandler(),
                    2, 4 * Constants.BYTES_PER_FLOAT);

            filterShaderProgram.setMatrixUniform(textureMatrix);
            filterShaderProgram.setTextureUniformExternal(textureID);
            filterShaderProgram.setUniform(filterShaderProgram.getTextureWidthHandler(), previewSize.width);
            filterShaderProgram.setUniform(filterShaderProgram.getTextureHeightHandler(), previewSize.height);

            if (null != filter) {
                filter.doFilter(filterShaderProgram);
            }

            glDrawArrays(GL_TRIANGLES, 0, 6);
        }
    }
}
