package com.pure.camera.opengl.glutil;

public class NormalizeUtil {

    public static float normalize(float src, float max, float min, float normalMax, float normalMin) {
        float k = (normalMax - normalMin) / (max - min);
        return k * (src - min) + normalMin;
    }

}
