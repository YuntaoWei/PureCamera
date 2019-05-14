//
// Created by wyt on 2019/5/8.
//
#define LOG_TAG "convert"
#include "jni_log.h"
#include <opencv2/opencv.hpp>
#ifndef PURECAMERA_CONVERT_H
#define PURECAMERA_CONVERT_H

#endif //PURECAMERA_CONVERT_H

using namespace cv;

Mat yuv420_to_bgr_mat(uchar* yuv, int w, int h, int type);

Mat rotate_mat(Mat src, int orientation);