package com.pure.camera.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.pure.camera.Constants;
import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.data.FullPreview;
import com.pure.camera.opengl.data.SmallFilterPreview;
import com.pure.camera.opengl.data.VertexArray;
import com.pure.camera.opengl.glutil.TextureHelper;
import com.pure.camera.opengl.program.CameraFilterShaderProgram;
import com.pure.camera.opengl.program.CameraShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

/**
 * 预览画面绘制主题类，滤镜效果的应用，都封装在内部，外部调用切换即可实现滤镜的无缝切换
 */
public class PreviewController {

    private static final String TAG = "PreviewController";

    private float[] textureMatrix;
    private int textureID;
    private SurfaceTexture surfaceTexture;
    private FullPreview fullPreview;
    private SmallFilterPreview smallFilterPreview;

    private boolean showFilter;

    public PreviewController() {
        textureMatrix = new float[16];
    }

    public void showFilter(boolean show) {
        showFilter = show;
    }

    /**
     * 预览绘制前的准备工作
     * @param mContext
     * @param mListener
     */
    public void onDrawPrepare(Context mContext, TextureListener mListener) {
        CameraShaderProgram previewShaderProgram = new CameraShaderProgram(mContext, R.raw.camera_preview_vertex,
                R.raw.camera_preview_fragment);
        fullPreview = new FullPreview(previewShaderProgram);

        CameraFilterShaderProgram filterShaderProgram = new CameraFilterShaderProgram(mContext, R.raw.small_camera_preview_vertex,
                R.raw.small_camera_preview_fragment);
        smallFilterPreview = new SmallFilterPreview(filterShaderProgram);

        textureID = TextureHelper.generateOESExternalTexture();
        if (null != mListener && textureID > 0) {
            surfaceTexture = new SurfaceTexture(textureID);
            mListener.onTexturePrepared(surfaceTexture);
        }
    }

    public void setFilter(BaseFilter filter) {
        fullPreview.setCameraPreviewFilter(filter);
    }

    public void updateTextureSize(int w, int h) {
        fullPreview.setPreviewSize(w, h);
        smallFilterPreview.setPreviewSize(w, h);
    }

    /**
     * 绘制预览界面
     */
    public void drawPreviewFrame() {
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(textureMatrix);

        //绘制大的预览界面
        fullPreview.draw(textureID, textureMatrix);

        if(!showFilter)
            return;
        //绘制全部的filter预览效果
        smallFilterPreview.draw(textureID, textureMatrix);
    }

}
