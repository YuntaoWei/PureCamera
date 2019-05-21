package com.pure.camera.opengl.data;

import com.pure.camera.Constants;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.program.CameraFilterShaderProgram;
import com.pure.camera.opengl.program.FilterBorderProgram;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

public class SmallFilterPreview {

    private static final float DEFAULT_WIDTH = 0.2f;
    private static final float DEFAULT_PADDING = 0.02f;
    private static final int COMPONENT_PER_COORS = 4;
    private static final int COORDS_PER_RECTANGLE = 4;

    CameraFilterShaderProgram filterShaderProgram;
    FilterBorderProgram borderProgram;
    VertexArray filterVertex;
    VertexArray borderVertex;
    List<BaseFilter> allFilters;
    List<SmallPreviewRect> previewRects;
    PreviewSize previewSize = new PreviewSize(1520, 720);
    int currentSelectFilterIndex = 0;
    float[] borderColor = {1.0f, 0f, 0f, 1.0f};

    public SmallFilterPreview(CameraFilterShaderProgram program, FilterBorderProgram borderProgram) {
        filterShaderProgram = program;
        this.borderProgram = borderProgram;
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
            //每个顶点四个元素，前两个为vertex coord，后两个为texture coord
            //x y s t
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

        reCreateBorderVertex();
    }

    public void setPreviewSize(int w, int h) {
        previewSize.width = h;
        previewSize.height = h;

        //预览尺寸发生变化的时候，需要重新计算small preview的宽高以及各顶点元素
        //initialVertex();
    }

    public void updateCurrentIndex(int index) {
        if(index < 0 || index == currentSelectFilterIndex)
            return;
        currentSelectFilterIndex = index;

        reCreateBorderVertex();
    }

    /**
     * 重置滤镜选中边框的坐标信息
     */
    private void reCreateBorderVertex() {
        SmallPreviewRect rect = previewRects.get(currentSelectFilterIndex);
        float coords[] = new float[10];
        //使用GL_LINE_STRIP方式绘制，顺序为左上 右上 右下 左下 左上闭合
        coords[0] = rect.left;
        coords[1] = rect.top;
        coords[2] = rect.right;
        coords[3] = rect.top;
        coords[4] = rect.right;
        coords[5] = rect.bottom;
        coords[6] = rect.left;
        coords[7] = rect.bottom;
        coords[8] = rect.left;
        coords[8] = rect.top;

        if(null == borderVertex) {
            borderVertex = new VertexArray(coords);
        } else {
            borderVertex.reset(coords);
        }
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
            }

            glDrawArrays(GL_TRIANGLE_STRIP, i, 4);
            i += 4;
        }

        //绘制选中的边框
        borderProgram.useProgram();
        borderVertex.setVertexAttribPointer(0, borderProgram.getVertexPositionHandler(), 2, 0);
        borderProgram.setUniformColor(borderColor);
        glDrawArrays(GL_LINE_STRIP, 0, 5);

        filterShaderProgram.disablePointer();
        borderProgram.disablePointer();
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
