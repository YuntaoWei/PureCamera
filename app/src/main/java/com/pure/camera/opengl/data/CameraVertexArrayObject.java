package com.pure.camera.opengl.data;

import android.opengl.GLES20;

import com.pure.camera.Constants;
import com.pure.camera.opengl.program.Program;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glGenVertexArrays;

public class CameraVertexArrayObject extends VertexArrayObject {

    public CameraVertexArrayObject(float[] d) {
        super(d);
    }

    @Override
    public void initVertexArrayObject(Program program, int vertexComponentCount, int textureComponentCount) {
        vertexCount = floatBuffer.capacity() / (vertexComponentCount + textureComponentCount);
        int[] vao = new int[1];
        int[] vbo = new int[1];

        glGenBuffers(1, vbo, 0);
        glGenVertexArrays(1, vao, 0);
        if(vao[0] == 0 || vbo[0] == 0)
            throw new RuntimeException("VAO create failed!");

        vaoObject = vao[0];
        vboObject = vbo[0];

        int stride = (vertexComponentCount + textureComponentCount) * Constants.BYTES_PER_FLOAT;
        glBindVertexArray(vaoObject);
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboObject);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer.capacity() * Constants.BYTES_PER_FLOAT,
                floatBuffer, GL_STATIC_DRAW);
        //绑定顶点坐标数据
        glVertexAttribPointer(program.getVertexPositionHandler(), vertexComponentCount, GL_FLOAT,
                false, stride, 0);
        glEnableVertexAttribArray(program.getVertexPositionHandler());
        //绑定纹理坐标数据
        glVertexAttribPointer(program.getTexturePositionHandler(), textureComponentCount, GL_FLOAT,
                false, stride, vertexComponentCount);
        glEnableVertexAttribArray(program.getVertexPositionHandler());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}
