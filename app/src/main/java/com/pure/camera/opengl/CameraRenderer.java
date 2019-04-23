package com.pure.camera.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.pure.camera.Constants;
import com.pure.camera.R;
import com.pure.camera.opengl.data.VertexArray;
import com.pure.camera.opengl.program.CameraShaderProgram;
import com.pure.camera.opengl.glutil.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glViewport;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private final float[] VERTEX_COORDINATES = {
            //x, y, s, t
            /*1f, 1f, 1f, 1f,
            1f, -1f, 1f, 0f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f*/
            1f,  1f,  1f,  1f,
            -1f,  1f,  0f,  1f,
            -1f, -1f,  0f,  0f,
            1f,  1f,  1f,  1f,
            -1f, -1f,  0f,  0f,
            1f, -1f,  1f,  0f
    };

    private final float[] SMALL_VERTEX_COORDINATES = {
            0.5f, -0.5f,  0f, 1f,
            1f, -0.5f,    1f, 1f,
            0.5f, -1f,    0f, 0f,
            1f, -1f,      1f, 0f
    };

    private final float[] SMALL_VERTEX_COORDINATES_T = {
             -1f, -0.5f,     0f, 1f,
            -0.5f, -0.5f,    1f, 1f,
            -1f, -1f,        0f, 0f,
            -0.5f, -1f,      1f, 0f
    };

    private int textureID;
    private Context mContext;
    private TextureListener mListener;
    private SurfaceTexture surfaceTexture;
    private float[] mSTMatrix;
    private CameraShaderProgram shaderProgram;
    private CameraShaderProgram smallShaderProgram;
    private VertexArray vertexArray;
    private VertexArray smallVertexArray;
    private VertexArray smallVertexArrayT;

    public CameraRenderer(Context ctx) {
        mContext = ctx;
        mSTMatrix = new float[16];
        vertexArray = new VertexArray(VERTEX_COORDINATES);
        //smallVertexArray = new VertexArray(SMALL_VERTEX_COORDINATES);
        //smallVertexArrayT = new VertexArray(SMALL_VERTEX_COORDINATES_T);
    }

    public void setTextureListener(TextureListener l) {
        mListener = l;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f, 0.5f, 0.5f, 1f);
        textureID = TextureHelper.generateOESExternalTexture();
        Log.i("ttt", "onSurfaceCreated : " + textureID);
        if (null != mListener && textureID > 0) {
            surfaceTexture = new SurfaceTexture(textureID);
            mListener.onTexturePrepared(surfaceTexture);
        }

        shaderProgram = new CameraShaderProgram(mContext, R.raw.camera_preview_vertex,
                R.raw.camera_preview_fragment);
        /*smallShaderProgram = new CameraShaderProgram(mContext, R.raw.small_camera_preview_vertex,
                R.raw.small_camera_preview_fragment);*/

        //smallShaderProgram = new CameraShaderProgram(mContext, R.raw.camera_preview_vertex,
        //       R.raw.camera_preview_fragment);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Log.i("size", "onSurfaceChanged : " + width + "  " + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        shaderProgram.useProgram();
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mSTMatrix);

        vertexArray.setVertexAttribPointer(0, shaderProgram.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        vertexArray.setVertexAttribPointer(2, shaderProgram.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        shaderProgram.setMatrixUniform(mSTMatrix);
        shaderProgram.setUniformInt(1);
        shaderProgram.setTextureUniformExternal(textureID);
        glDrawArrays(GL_TRIANGLES, 0, 6);


        /*smallShaderProgram.useProgram();
        smallVertexArray.setVertexAttribPointer(0, smallShaderProgram.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        smallVertexArray.setVertexAttribPointer(2, smallShaderProgram.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        smallShaderProgram.setMatrixUniform(mSTMatrix);
        smallShaderProgram.setTextureUniformExternal(textureID);
        smallShaderProgram.setUniformInt(1);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);*/
    }
}
