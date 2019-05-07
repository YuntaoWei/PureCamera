package com.pure.camera.opengl.glview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.CameraFilterManager;
import com.pure.camera.opengl.data.PreviewSize;
import com.pure.camera.opengl.glutil.NormalizeUtil;
import com.pure.camera.opengl.renderer.CameraRenderer;
import com.pure.camera.opengl.TextureListener;

public class CameraGLView extends GLSurfaceView implements View.OnTouchListener {

    private static final String TAG = "CameraGLView";
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
        setOnTouchListener(this);
    }

    public void setTextureListener(TextureListener l) {
        cameraRenderer.setTextureListener(l);
    }


    public CameraRenderer getCameraRenderer() {
        return cameraRenderer;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    PreviewSize screenSize;
    public void setScreenSize(PreviewSize size) {
        screenSize = size;
    }

    private float downX, downY;
    private boolean isMove;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = 0 - event.getY();
                downX = NormalizeUtil.normalize(x, screenSize.width, 0, 1, -1);
                downY = NormalizeUtil.normalize(y, 0, 0 - screenSize.height, 1, -1);
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(!isMove) {
                    int index = cameraRenderer.isCovered(downX, downY);
                    if(index != -1) {
                        BaseFilter filter = CameraFilterManager.getInstance().getFilterByIndex(index);
                        cameraRenderer.updateFilter(filter);
                    }
                }
                isMove = false;
                break;
        }

        return true;
    }
}