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

void gray(Mat bgr) {
    int channel = bgr.channels();
    if(channel < 3)
        return;

    switch(channel) {
        case 3: {
            //bgr
            MatIterator_<Vec3b> it, end;
            for( it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
                int gray = ((*it)[0] * 15 + (*it)[1] * 75 + (*it)[2] * 38) >> 7;
                (*it)[0] = gray;
                (*it)[1] = gray;
                (*it)[2] = gray;
            }
            break;
        }
        case 4:
            //bgra
            MatIterator_<Vec4b> it, end;
            for( it = bgr.begin<Vec4b>(), end = bgr.end<Vec4b>(); it != end; ++it) {
                int gray = ((*it)[0] * 15 + (*it)[1] * 75 + (*it)[2] * 38) >> 7;
                (*it)[0] = gray;
                (*it)[1] = gray;
                (*it)[2] = gray;
            }
            break;
    }

}

void wb(Mat bgr) {
    int channel = bgr.channels();
    if(channel < 3)
        return;

    if(channel == 3) {
        MatIterator_<Vec3b> it, end;
        for( it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
            int average = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;
            if(average > 100)
                average = 255;
            else
                average = 0;
            (*it)[0] = average;
            (*it)[1] = average;
            (*it)[2] = average;
        }
    } else if(channel == 4) {
        MatIterator_<Vec4b> it, end;
        for( it = bgr.begin<Vec4b>(), end = bgr.end<Vec4b>(); it != end; ++it) {
            int average = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;
            if(average > 100)
                average = 255;
            else
                average = 0;
            (*it)[0] = average;
            (*it)[1] = average;
            (*it)[2] = average;
        }
    }
}