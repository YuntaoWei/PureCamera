package com.pure.camera.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.pure.camera.Constants;
import com.pure.camera.R;
import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.data.VertexArray;
import com.pure.camera.opengl.glutil.TextureHelper;
import com.pure.camera.opengl.program.CameraShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

/**
 * 预览画面绘制主题类，滤镜效果的应用，都封装在内部，外部调用切换即可实现滤镜的无缝切换
 */
public class PreviewController {

    private static final String TAG = "PreviewController";

    //vertex coordinate(x, y)  -  texture coordinate(s, t)
    private final float[] VERTEX_COORDINATES = {
            //x, y, s, t
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    private final float[] SMALL_VERTEX_COORDINATES = {
            0.5f, -0.5f, 0f, 1f,
            1f, -0.5f, 1f, 1f,
            0.5f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    private final float[] SMALL_VERTEX_COORDINATES_T = {
            -1f, -0.5f, 0f, 1f,
            -0.5f, -0.5f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            -0.5f, -1f, 1f, 0f
    };

    private float[] textureMatrix;
    private VertexArray previewVertexArray;
    private int textureID;
    private SurfaceTexture surfaceTexture;
    private CameraShaderProgram glEsPrograml;

    private BaseFilter cameraFilter;

    public PreviewController() {
        textureMatrix = new float[16];
        previewVertexArray = new VertexArray(VERTEX_COORDINATES);
        cameraFilter = CameraFilterManager.getInstance().getFilter(CameraFilterManager.FILTER_NAME_MOSAIC);
        LogPrinter.i(TAG, "PreviewController Create filter : " + cameraFilter);
    }

    /**
     * 预览绘制前的准备工作
     * @param mContext
     * @param mListener
     */
    public void onDrawPrepare(Context mContext, TextureListener mListener) {
        textureID = TextureHelper.generateOESExternalTexture();
        if (null != mListener && textureID > 0) {
            surfaceTexture = new SurfaceTexture(textureID);
            mListener.onTexturePrepared(surfaceTexture);
        }

        glEsPrograml = new CameraShaderProgram(mContext, R.raw.camera_preview_vertex,
                R.raw.camera_preview_fragment);
    }

    /**
     * 更新当前应用的滤镜.
     * @param filter 新的滤镜.
     */
    public void setFilter(BaseFilter filter) {
        if(cameraFilter == filter)
            return;

        cameraFilter = filter;
    }

    /**
     * 绘制预览界面
     */
    public void drawPreviewFrame() {
        glEsPrograml.useProgram();
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(textureMatrix);

        previewVertexArray.setVertexAttribPointer(0, glEsPrograml.getVPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        previewVertexArray.setVertexAttribPointer(2, glEsPrograml.getVtPositionHandler(),
                2, 4 * Constants.BYTES_PER_FLOAT);
        glEsPrograml.setMatrixUniform(textureMatrix);
        glEsPrograml.setTextureUniformExternal(textureID);

        //这里作滤镜的特殊处理，如果为空，则表示原始的图像，没有滤镜效果
        if(null != cameraFilter) {
            cameraFilter.doFilter(glEsPrograml);
            LogPrinter.i(TAG, "do filter : " + cameraFilter.toString());
        }

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

}
