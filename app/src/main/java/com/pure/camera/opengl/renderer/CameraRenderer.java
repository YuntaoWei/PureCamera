package com.pure.camera.opengl.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.pure.camera.filter.BaseFilter;
import com.pure.camera.opengl.PreviewController;
import com.pure.camera.opengl.TextureListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ALPHA;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
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

    public void updateFilter(BaseFilter filter, int index) {
        previewController.setFilter(filter, index);
    }

    public void showFilterPreview(boolean flag) {
        previewController.showFilter(flag);
    }

    public void updateTexture(int w, int h) {
        previewController.updateTextureSize(w, h);
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

    public void pause() {
        //在这里需要通知PreviewController释放资源，比如texture，VBO,VAO,EBO等
    }

    public int isCovered(float x, float y) {
        return previewController.isCovered(x, y);
    }

}
