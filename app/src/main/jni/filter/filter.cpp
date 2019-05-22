//
// Created by wyt on 2019/5/21.
//
#include <stdlib.h>
#include <stdio.h>
#include <opencv2/opencv.hpp>
#include "convert.h"
#include "imageproc.h"
#include "filter.h"

#define DEBUG
#define LOG_TAG "filter"

#include "jni_log.h"

using namespace cv;

extern "C"

jboolean do_filter_gray(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_gray!");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat bgr = yuv420_to_bgr_mat((unsigned char *)cbuf, w, h, CV_YUV2BGR_I420);

    gray(bgr);

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

    Mat bgr = yuv420_to_bgr_mat((unsigned char *)cbuf, w, h, CV_YUV2BGR_I420);
    LOGI("do_filter_mosaic  1");

    mosaic(bgr, square, 0);

    Mat result;
    result = rotate_mat(bgr, orientation);
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    result.release();
    bgr.release();

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

    Mat imgData = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV2BGR_I420);

    Mat tmp;
    tmp = rotate_mat(imgData, orientation);
    Mat result = relief(tmp);

    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    tmp.release();
    imgData.release();
    result.release();
    return success;
}

jboolean do_filter_wb(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_wb");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat bgr = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV2BGR_I420);
    Mat tmp;
    tmp = rotate_mat(bgr, orientation);
    wb(tmp);

    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, tmp);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    tmp.release();
    bgr.release();
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

jboolean do_filter_positive(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_positive");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV2BGR_I420);
    Mat result;
    result = rotate_mat(imgData, orientation);
    positive(result);
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    result.release();
    imgData.release();
    return success;
}

jboolean do_filter_comic(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file) {
    LOGI("do_filter_comic");
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData = yuv420_to_bgr_mat((unsigned char *) cbuf, w, h, CV_YUV2BGR_I420);
    Mat tmp, result;
    tmp = rotate_mat(imgData, orientation);
    result = comic1(tmp);
    char* file_Path =(char*) env->GetStringUTFChars(file, JNI_FALSE);
    bool success = imwrite(file_Path, result);

    env->ReleaseByteArrayElements(buf, cbuf, 0);
    env->ReleaseStringUTFChars(file, file_Path);
    tmp.release();
    result.release();
    imgData.release();
    return success;
}

