package com.pure.camera.opengl.data;

import com.pure.camera.Constants;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.program.CameraFilterShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

public class SmallFilterPreview {

    private static final float DEFAULT_WIDTH = 0.2f;
    private static final float DEFAULT_PADDING = 0.02f;
    private static final int COMPONENT_PER_COORS = 4;
    private static final int COORDS_PER_RECTANGLE = 4;

    CameraFilterShaderProgram filterShaderProgram;
    VertexArray filterVertex;
    List<BaseFilter> allFilters;
    List<SmallPreviewRect> previewRects;
    PreviewSize previewSize = new PreviewSize(1520, 720);


    public SmallFilterPreview(CameraFilterShaderProgram program) {
        filterShaderProgram = program;
        allFilters = CameraFilterManager.getInstance().getAllFilter();
        initialVertex();
    }

    private void initialVertex() {
        //float ratio = previewSize.getAspectRatioReverse();
        float height = DEFAULT_WIDTH * 3 / 4;
        int filterCount = allFilters.size();
        previewRects = new ArrayList<>(filterCount);
        //每个平面矩形需要4二维个顶点，8个float数表示，每个filter需要一个矩形来绘制预览效果
        //同样的纹理坐标数量也与顶点坐标相等
        int coordsPerRectangle = COMPONENT_PER_COORS * COORDS_PER_RECTANGLE;
        float[] coords = new float[filterCount * coordsPerRectangle];
        float currentStartX = 1f - DEFAULT_PADDING;
        //采用GL_TRIANGLE_STRIP的形式来绘制,从右上开始为顶点1，左上为2，右下为3，左下为4
        for (int i = 0; i < filterCount; i++) {
            float left = currentStartX - DEFAULT_WIDTH;
            float right = currentStartX;
            float top = -0.5f + height;
            float bottom = -0.5f;
            //顶点1
            coords[coordsPerRectangle * i] = right;
            coords[coordsPerRectangle * i + 1] = top;
            coords[coordsPerRectangle * i + 2] = 1f;
            coords[coordsPerRectangle * i + 3] = 1f;
            //顶点2
            coords[coordsPerRectangle * i + 4] = left;
            coords[coordsPerRectangle * i + 5] = top;
            coords[coordsPerRectangle * i + 6] = 0f;
            coords[coordsPerRectangle * i + 7] = 1f;
            //顶点3
            coords[coordsPerRectangle * i + 8] = right;
            coords[coordsPerRectangle * i + 9] = bottom;
            coords[coordsPerRectangle * i + 10] = 1f;
            coords[coordsPerRectangle * i + 11] = 0f;
            //顶点4
            coords[coordsPerRectangle * i + 12] = left;
            coords[coordsPerRectangle * i + 13] = bottom;
            coords[coordsPerRectangle * i + 14] = 0f;
            coords[coordsPerRectangle * i + 15] = 0f;

            SmallPreviewRect previewRect = new SmallPreviewRect(left, top, right, bottom);
            previewRects.add(previewRect);

            currentStartX -= DEFAULT_WIDTH;
            currentStartX -= DEFAULT_PADDING;
        }

        LogPrinter.debugCoords("ccc", coords, 4);

        if (null != filterVertex) {
            filterVertex.reset(coords);
        } else {
            filterVertex = new VertexArray(coords);
        }
    }

    public void setPreviewSize(int w, int h) {
        previewSize.width = h;
        previewSize.height = h;

        //预览尺寸发生变化的时候，需要重新计算small preview的宽高以及各顶点元素
        //initialVertex();
    }

    public void draw(int textureID, float[] textureMatrix) {
        filterShaderProgram.useProgram();

        filterVertex.setVertexAttribPointer(0, filterShaderProgram.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        filterVertex.setVertexAttribPointer(2, filterShaderProgram.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);

        int i = 0;
        for (BaseFilter filter : allFilters
                ) {
            filterShaderProgram.setMatrixUniform(textureMatrix);
            filterShaderProgram.setTextureUniformExternal(textureID);
            filterShaderProgram.setUniform(filterShaderProgram.getTextureWidthHandler(), previewSize.width);
            filterShaderProgram.setUniform(filterShaderProgram.getTextureHeightHandler(), previewSize.height);

            if (null != filter) {
                filter.doFilter(filterShaderProgram);
            } else {
                //filter为null就需要显示原始画面，不应用任何滤镜效果
            }

            glDrawArrays(GL_TRIANGLE_STRIP, i, 4);
            i += 4;
        }
    }

    public int isCovered(float x, float y) {
        int i = 0;
        for (SmallPreviewRect r : previewRects
             ) {
            if(r.isCovered(x, y)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}
