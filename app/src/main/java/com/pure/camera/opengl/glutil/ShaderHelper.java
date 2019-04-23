package com.pure.camera.opengl.glutil;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int buildGLProgram(String vertex, String frag) {
        int vertexShader = createShader(vertex, GL_VERTEX_SHADER);
        int fragShader = createShader(frag, GL_FRAGMENT_SHADER);
        if(vertexShader == 0 || fragShader == 0)
            return 0;

        int program = glCreateProgram();
        if(program == 0) {
            Log.e(TAG, "Create program failed!");
            return 0;
        }
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);
        int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if(status[0] == 0) {
            Log.e(TAG, "Link program failed : " + glGetProgramInfoLog(program));
            glDeleteProgram(program);
            return 0;
        }

        return program;
    }

    public static int createShader(String shaderSource, int shaderType) {
        int shader = glCreateShader(shaderType);
        if(shader == 0) {
            Log.e(TAG, "Create shader failed!");
            return 0;
        }
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        int[] status = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0) {
            Log.e(TAG, "Compile shader failed : " + glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

}
