package com.pure.camera.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CameraGLView extends GLSurfaceView {

    private Context mContext;
    private CameraRenderer cameraRenderer;

    public CameraGLView(Context context) {
        this(context, null);
    }

    public CameraGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        cameraRenderer = new CameraRenderer(mContext);
        setEGLContextClientVersion(3);
        setRenderer(cameraRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void setTextureListener(TextureListener l) {
        cameraRenderer.setTextureListener(l);
    }

}
