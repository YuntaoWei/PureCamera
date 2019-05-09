package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

/**
 * 马赛克滤镜
 */
public class MosaicFilter extends AbstractFilter {

    public final static String NAME = "MosaicFilter";
    private final static int TYPE = 3;
    private final static float MOSAIC_DEFAULT_SQUARE = 16.0f;

    public MosaicFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
        program.setUniform(program.getMosaicSizeHandler(), MOSAIC_DEFAULT_SQUARE);
    }

    @Override
    public byte[] getFilterImage(byte[] pixels, int w, int h) {
        return NativeFilter.getInstance().doFilterMosaic(pixels, w, h, (int)MOSAIC_DEFAULT_SQUARE);
    }
}
