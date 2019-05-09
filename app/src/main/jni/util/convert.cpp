//
// Created by ubt on 2019/5/8.
//
#include "convert.h"

#define DEBUG
#define LOG_TAG "convert"
#include "jni_log.h"

uchar* yuv420_to_bgr(uchar* yuv, int w, int h) {
    LOGI("yuv420_to_bgr");
    Mat yuvMat(h + h / 2, w, CV_8UC1, yuv);
    Mat dstMat(h, w, CV_8UC3);
    cvtColor(yuvMat, dstMat, CV_YUV420p2BGR);
    return dstMat.data;
}

