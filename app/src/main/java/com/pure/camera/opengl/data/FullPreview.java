package com.pure.camera.opengl.data;

import android.util.AndroidRuntimeException;

import com.pure.camera.Constants;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.opengl.program.CameraShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glGenVertexArrays;
import static com.pure.camera.Constants.HIGH_PERFORMANCE_DRAW;

public class FullPreview {
    private static final String TAG = "FullPreview";

    //vertex coordinate(x, y)  -  texture coordinate(s, t)
    private final float[] VERTEX_COORDINATES = {
            //x, y, s, t
            1f, 1f, 1f, 1f, //0
            -1f, 1f, 0f, 1f, //1
            -1f, -1f, 0f, 0f, //2
            1f, 1f, 1f, 1f, //3
            -1f, -1f, 0f, 0f, //4
            1f, -1f, 1f, 0f //5
    };

    private final float[] VERTEX_COORDINATES_NEW = {
            //x y s t
            1f, 1f, 1f ,1f, //index 0
            -1f ,1f, 0f, 1f, //index 1
            1f, -1f, 1f, 0f, //index 2
            -1f, -1f, 0f, 0f //index 3
    };

    private final byte[] VERTEX_INDEX = {
            0, 2, 3,
            3, 1, 0
            //0, 1, 2,
            //3, 4, 5
    };

    private int vaoID, iboID;

    CameraShaderProgram shaderProgram;
    VertexArray previewVertexArray;
    BaseFilter cameraFilter;
    PreviewSize previewSize = new PreviewSize(1520, 720);

    public FullPreview(CameraShaderProgram program) {
        shaderProgram = program;
        if(!HIGH_PERFORMANCE_DRAW) {
            previewVertexArray = new VertexArray(VERTEX_COORDINATES);
        } else {
            previewVertexArray = new VertexArray(VERTEX_COORDINATES_NEW);
            createVAO();
            createIBO();
        }
    }

    private void createVAO() {
        int[] vbo = new int[1];
        int[] vao = new int[1];
        glGenBuffers(1, vbo, 0);
        if(vbo[0] == 0) {
            throw new AndroidRuntimeException("FullPreview Create VBO failed!");
        }
        glGenVertexArrays(1, vao, 0);
        if(vao[0] == 0) {
            throw new AndroidRuntimeException("FullPreview Create VAO failed!");
        }
        glBindVertexArray(vao[0]);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, previewVertexArray.getSizePerByte(),
                previewVertexArray.getBuffer(), GL_STATIC_DRAW);
        previewVertexArray.setVertexAttribPointer2(shaderProgram.getVertexPositionHandler(), 2 , 16, 0);
        previewVertexArray.setVertexAttribPointer2(shaderProgram.getVtPositionHandler(), 2 , 16, 8);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        vaoID = vao[0];
    }

    private void createIBO() {
        int[] ibo = new int[1];
        glGenBuffers(1, ibo, 0);
        if(ibo[0] == 0) {
            throw new AndroidRuntimeException("Create IBO Buffer failed!");
        }

        ByteBuffer bf = ByteBuffer.allocateDirect(VERTEX_INDEX.length)
                .order(ByteOrder.nativeOrder())
                .put(VERTEX_INDEX);
        bf.position(0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VERTEX_INDEX.length, bf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        iboID = ibo[0];
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

        //uniform
        shaderProgram.setMatrixUniform(textureMatrix);
        shaderProgram.setTextureUniformExternal(textureID);
        if (null != cameraFilter) {
            LogPrinter.i(TAG, "do filter : " + cameraFilter.toString() + "  " + previewSize);
            cameraFilter.doFilter(shaderProgram);
            shaderProgram.setUniform(shaderProgram.getTextureWidthHandler(), (float) previewSize.width);
            shaderProgram.setUniform(shaderProgram.getTextureHeightHandler(), (float) previewSize.height);
        }

        //attribute
        if(HIGH_PERFORMANCE_DRAW) {
            drawHighPerformance();
        } else {
            drawNormal();
        }
    }

    public void drawNormal() {
        previewVertexArray.setVertexAttribPointer(0, shaderProgram.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        previewVertexArray.setVertexAttribPointer(2, shaderProgram.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        shaderProgram.disablePointer();
    }

    public void drawHighPerformance() {
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboID);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

}
