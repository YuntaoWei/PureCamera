package com.pure.camera.opengl.glutil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pure.camera.common.LogPrinter;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {

    private static final String TAG = "TextureHelper";

    public static int generateOESExternalTexture() {
        int[] textureID = new int[1];
        glGenTextures(1, textureID, 0);
        if(textureID[0] == 0) {
            LogPrinter.e(TAG, "Create texture failed!");
            return 0;
        }

        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureID[0]);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
        return textureID[0];
    }

    public static int generateLocalTexture(Resources res, int resID) {
        int[] textureID = new int[1];
        glGenTextures(1, textureID, 0);
        if(textureID[0] == 0) {
            LogPrinter.e(TAG, "Create texture failed!");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                res, resID, options);
        if(bitmap == null) {
            LogPrinter.e(TAG, "Bitmap create failed!");
            //删除纹理，参数意义同创建的时候
            glDeleteTextures(1, textureID, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glTexParameteri(GL_TEXTURE_2D,
                GL_TEXTURE_MIN_FILTER,
                GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureID[0];
    }

}
