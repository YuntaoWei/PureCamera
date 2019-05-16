//
// Created by wyt on 2019/5/7.
//
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include <opencv2/opencv.hpp>
#include "convert.h"

#define DEBUG
#define LOG_TAG "filter"

#include "jni_log.h"

using namespace cv;

extern "C"

#define JAVA_CLASS "com/pure/camera/filter/engine/NativeFilter"

jboolean do_filter_gray(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_gray!");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat bgr = yuv420_to_bgr_mat((unsigned char *)cbuf, w, h, CV_YUV420p2BGRA);

    uchar *ptr = bgr.ptr(0);
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587 +
                               ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = grayScale;
        ptr[4 * i + 2] = grayScale;
        ptr[4 * i + 0] = grayScale;
    }

    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    Mat result = rotate_mat(bgr, orientation);
    bool success = imwrite(file_Path, result);;

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    bgr.release();
    result.release();

    return success;
}

jboolean do_filter_mosaic(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int square, int orientation, jstring file) {
    LOGI("do_filter_mosaic  square : %d, orientation : %d.", square, orientation);
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat bgr = yuv420_to_bgr_mat((unsigned char *)cbuf, w, h, CV_YUV420p2BGRA);
    LOGI("do_filter_mosaic  1");
    Mat tmp;
    if(square % 2 == 0)
        square += 1;


    //GaussianBlur(bgr, tmp, Size(square, square), 0, 0);
    blur(bgr, tmp, Size(square, square), Point(-1, -1), BORDER_CONSTANT);
    LOGI("do_filter_mosaic  2");

    Mat result;
    result = rotate_mat(bgr, orientation);
    LOGI("do_filter_mosaic  3");
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    result.release();
    bgr.release();
    tmp.release();

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    return success;
}

jboolean do_filter_relief(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_relief");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV420p2BGRA);

    uchar *ptr = imgData.ptr(0);
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587 +
                               ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = grayScale;
        ptr[4 * i + 2] = grayScale;
        ptr[4 * i + 0] = grayScale;
    }

    Mat result;
    result = rotate_mat(imgData, orientation);
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    result.release();
    imgData.release();
    return success;
}

jboolean do_yuv2rgb(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_yuv2rgb");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV2BGR_I420);
    Mat result;
    result = rotate_mat(imgData, orientation);
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    result.release();
    imgData.release();
    return success;
}

static JNINativeMethod gMethods[] = {
        {"doFilterGray",   "([BIIILjava/lang/String;)Z",  (void *) do_filter_gray},
        {"doFilterMosaic", "([BIIIILjava/lang/String;)Z", (void *) do_filter_mosaic},
        {"doFilterRelief", "([BIIILjava/lang/String;)Z",  (void *) do_filter_relief},
        {"doYuv2RGB", "([BIIILjava/lang/String;)Z",  (void *) do_yuv2rgb},
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    return registerNativeMethods(env, JAVA_CLASS, gMethods,
                                 sizeof(gMethods) / sizeof(gMethods[0]));
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        return -1;
    }

    return JNI_VERSION_1_4;
}
