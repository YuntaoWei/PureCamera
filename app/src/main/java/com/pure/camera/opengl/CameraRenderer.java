package com.pure.camera.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.pure.camera.filter.BaseFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "CameraRenderer";

    private Context mContext;
    private TextureListener mListener;

    private PreviewController previewController;
    public CameraRenderer(Context ctx) {
        mContext = ctx;
        previewController = new PreviewController();
    }

    public void setTextureListener(TextureListener l) {
        mListener = l;
    }

    public void updateFilter(BaseFilter filter) {
        previewController.setFilter(filter);
    }

    public void pause() {
        //在这里需要通知PreviewController释放资源，比如texture，VBO,VAO,EBO等
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f, 0.5f, 0.5f, 1f);
        previewController.onDrawPrepare(mContext, mListener);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        previewController.drawPreviewFrame();
    }

}
