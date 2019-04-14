package com.pure.camera.opengl.data;

import com.pure.camera.Constants;
import com.pure.camera.opengl.program.Program;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES30.glGenVertexArrays;

public abstract class VertexArrayObject {

    protected int vaoObject;
    protected int vboObject;
    protected int vertexCount;
    protected FloatBuffer floatBuffer;

    public VertexArrayObject(float[] vertexs) {
        floatBuffer = ByteBuffer.allocateDirect(vertexs.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexs);

        floatBuffer.position(0);
    }

    public abstract void initVertexArrayObject(Program program, int vertexComponentCount, int textureComponentCount);

    public int getVaoObject() {
        return vaoObject;
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
