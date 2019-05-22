//
// Created by wyt on 2019/5/16.
//

#ifndef PURECAMERA_IMAGEPROC_H
#define PURECAMERA_IMAGEPROC_H

#include <opencv2/opencv.hpp>

#define DEBUG
#define LOG_TAG "imgproc"
#include "jni_log.h"

using namespace cv;

void mosaic(Mat bgr, int square, int center);

Mat relief(Mat bgr);

void gray(Mat bgr);

void wb(Mat bgr);

void positive(Mat bgr);

void comic(Mat bgr);

Mat comic1(Mat bgr);


#endif //PURECAMERA_IMAGEPROC_H
