package com.pure.camera.filter.engine;

import com.pure.camera.opengl.program.CameraShaderProgram;

public class ComicFilter extends AbstractFilter {
    public static final String NAME = "ComicFilter";
    public static final int TYPE = 6;

    public ComicFilter() {
        super(NAME, TYPE);
    }

    @Override
    protected void doFilterImpl(CameraShaderProgram program) {
        program.setFilterType(TYPE);
    }

    @Override
    public boolean getFilterImage(byte[] pixels, int w, int h, int orientation, String filePath) {
        return NativeFilter.getInstance().doFilterComic(pixels, w, h, orientation, filePath);
    }
}
