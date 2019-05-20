//
// Created by wyt on 2019/5/16.
//

#ifndef PURECAMERA_IMAGEPROC_H
#define PURECAMERA_IMAGEPROC_H

#include <opencv2/opencv.hpp>

using namespace cv;

void mosaic(Mat bgr, int square, int center);

Mat relief(Mat bgr);

void gray(Mat bgr);


#endif //PURECAMERA_IMAGEPROC_H
