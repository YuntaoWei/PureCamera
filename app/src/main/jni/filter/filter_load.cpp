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

jbyteArray do_filter_gray(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h) {
    LOGI("do_filter_gray!");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    //Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);
    Mat yuv(h + h / 2, w, CV_8UC1, (uchar *)cbuf);
    Mat imgData;
    cvtColor(yuv, imgData, CV_YUV2BGR_I420);

    uchar *ptr = imgData.ptr(0);
    imgData.data;
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587 +
                               ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = grayScale;
        ptr[4 * i + 2] = grayScale;
        ptr[4 * i + 0] = grayScale;
    }

    int size = w * h;
    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0, size, cbuf);
    env->ReleaseByteArrayElements(buf, cbuf, 0);
    return result;
}

jbyteArray do_filter_mosaic(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int square) {
    LOGI("test print log!");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);

    uchar *ptr = imgData.ptr(0);
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587 +
                               ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = grayScale;
        ptr[4 * i + 2] = grayScale;
        ptr[4 * i + 0] = grayScale;
    }

    Mat dst(h, w, CV_8UC4);
    cvtColor(imgData, dst, CV_YUV2BGR_I420);

    int size = w * h;
    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0, size, reinterpret_cast<const jbyte *>(dst.ptr(0)));
    env->ReleaseByteArrayElements(buf, cbuf, 0);
    dst.release();
    imgData.release();
    return result;
}

jbyteArray do_filter_relief(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h) {
    LOGI("test print log!");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);

    uchar *ptr = imgData.ptr(0);
    for (int i = 0; i < w * h; i++) {
        int grayScale = (int) (ptr[4 * i + 2] * 0.299 + ptr[4 * i + 1] * 0.587 +
                               ptr[4 * i + 0] * 0.114);
        ptr[4 * i + 1] = grayScale;
        ptr[4 * i + 2] = grayScale;
        ptr[4 * i + 0] = grayScale;
    }

    int size = w * h;
    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0, size, cbuf);
    env->ReleaseByteArrayElements(buf, cbuf, 0);
    return result;
}

static JNINativeMethod gMethods[] = {
        {"doFilterGray",   "([BII)[B",  (void *) do_filter_gray},
        {"doFilterMosaic", "([BIII)[B", (void *) do_filter_mosaic},
        {"doFilterRelief", "([BII)[B",  (void *) do_filter_relief},
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
