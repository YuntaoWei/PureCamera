//
// Created by wyt on 2019/5/16.
//

#include "imageproc.h"

void mosaic(Mat bgr, int square, int center) {
    int channels = bgr.channels();
    if (channels == 1) {
        //single channel
    } else if (channels == 3) {
        //bgr
        MatIterator_<Vec3b> it, end;
        int x = 0, y = 0;
        int col = bgr.cols;
        int row = bgr.rows;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int realX = i / square * square;
                int realY = j / square * square;
                bgr.at<Vec3b>(i, j) = bgr.at<Vec3b>(realX, realY);
            }
        }
    } else if (channels == 4) {
        //bgra
    }
}

Mat relief(Mat bgr) {
    Mat gray;
    cvtColor(bgr, gray, CV_BGR2GRAY);

    int width = gray.cols;
    int height = gray.rows;
    int channel = gray.channels();
    width = width * channel;
    for(int j = 0; j < height; j++) {
        for(int i = 0; i < width - 1; i++) {
            uchar* currentColor = gray.ptr(j, i);
            uchar* nextColor = gray.ptr(j, i + 1);
            *currentColor = (*currentColor - *nextColor) + 128;
        }
    }

    return gray;
}