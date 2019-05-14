//
// Created by wyt on 2019/5/8.
//

#define DEBUG
#include "convert.h"


Mat yuv420_to_bgr_mat(uchar* yuv, int w, int h, int type) {
    LOGI("yuv420_to_bgr");
    Mat yuvMat(h + h / 2, w, CV_8UC1, yuv);
    Mat bgr;
    cvtColor(yuvMat, bgr, type);
    yuvMat.release();
    return bgr;
}

Mat rotate_mat(Mat src, int orientation) {
    LOGI("rotate_mat angle : %d", orientation);
    RotateFlags  f;
    if(orientation == 90) {
        f = ROTATE_90_CLOCKWISE;
    } else if(orientation == 180) {
        f = ROTATE_180;
    } else if(orientation == 270){
        f = ROTATE_90_COUNTERCLOCKWISE;
    } else {
        return src;
    }

    Mat result;
    rotate(src, result, f);
    return result;
}


