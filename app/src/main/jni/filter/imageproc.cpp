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
    for (int j = 0; j < height; j++) {
        for (int i = 0; i < width - 1; i++) {
            uchar *currentColor = gray.ptr(j, i);
            uchar *nextColor = gray.ptr(j, i + 1);
            *currentColor = (*currentColor - *nextColor) + 128;
        }
    }

    return gray;
}

void gray(Mat bgr) {
    int channel = bgr.channels();
    if (channel < 3)
        return;

    switch (channel) {
        case 3: {
            //bgr
            MatIterator_<Vec3b> it, end;
            for (it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
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
            for (it = bgr.begin<Vec4b>(), end = bgr.end<Vec4b>(); it != end; ++it) {
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
    if (channel < 3)
        return;

    if (channel == 3) {
        MatIterator_<Vec3b> it, end;
        for (it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
            int average = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;
            if (average > 100)
                average = 255;
            else
                average = 0;
            (*it)[0] = average;
            (*it)[1] = average;
            (*it)[2] = average;
        }
    } else if (channel == 4) {
        MatIterator_<Vec4b> it, end;
        for (it = bgr.begin<Vec4b>(), end = bgr.end<Vec4b>(); it != end; ++it) {
            int average = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;
            if (average > 100)
                average = 255;
            else
                average = 0;
            (*it)[0] = average;
            (*it)[1] = average;
            (*it)[2] = average;
        }
    }
}

void positive(Mat bgr) {
    int channle = bgr.channels();
    int width = bgr.cols;
    int height = bgr.rows;
    switch (channle) {
        case 1: {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    uchar *a = bgr.at<uchar *>(i, j);
                    *a = 255 - *a;
                }
            }
            break;
        }

        case 3: {
            MatIterator_<Vec3b> it, end;
            for (it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
                (*it)[0] = 255 - (*it)[0];
                (*it)[1] = 255 - (*it)[1];
                (*it)[2] = 255 - (*it)[2];
            }
            break;
        }

        case 4: {
            MatIterator_<Vec4b> it1, end1;
            for (it1 = bgr.begin<Vec4b>(), end1 = bgr.end<Vec4b>(); it1 != end1; ++it1) {
                (*it1)[0] = 255 - (*it1)[0];
                (*it1)[1] = 255 - (*it1)[1];
                (*it1)[2] = 255 - (*it1)[2];
            }
            break;
        }
    }
}

void comic(Mat bgr) {
    int channle = bgr.channels();
    LOGV("comic filter, original picture channles : %d.", channle);
    int r, g, b;
    int newr, newg, newb;
    switch (channle) {
        case 3: {
            MatIterator_<Vec3b> it, end;
            for (it = bgr.begin<Vec3b>(), end = bgr.end<Vec3b>(); it != end; ++it) {
                //b g r
                r = (*it)[2];
                g = (*it)[1];
                b = (*it)[0];
                /*newr = abs(g - b + g + r) * r / 256;//r
                newg = abs(b - g + b + r) * r / 256;//g
                newb = abs(b - g + b + r) * g / 256;//b
                //LOGV("(%d, %d, %d), (%d, %d, %d)", r, g ,b, newr, newg, newb);
                (*it)[2] = newr;
                (*it)[1] = newg;
                (*it)[0] = newb;*/
                (*it)[2] = abs(g - b + g + r) * r / 256;//r
                (*it)[1] = abs(b - g + b + r) * r / 256;//g
                (*it)[0] = abs(b - g + b + r) * g / 256;//b
            }
            break;
        }

        case 4: {
            MatIterator_<Vec4b> it1, end1;
            for (it1 = bgr.begin<Vec4b>(), end1 = bgr.end<Vec4b>(); it1 != end1; ++it1) {
                b = (*it1)[0];
                g = (*it1)[1];
                r = (*it1)[2];
                (*it1)[0] = abs(b - g + b + r) * g / 256;
                (*it1)[1] = abs(b - g + b + r) * r / 256;
                (*it1)[2] = abs(g - b + g + r) * r / 256;
            }
            break;
        }
    }
}

Mat comic1(Mat bgr) {
    if(bgr.channels() > 1) {
        cvtColor(bgr, bgr, CV_BGR2GRAY);
    }

    medianBlur(bgr, bgr, 7);
    Mat tmp;
    //Canny(bgr, tmp, 50, 50 * 3, 3);
    Laplacian(bgr, tmp, CV_8U, 5);
    Mat result;
    threshold(tmp, result, 80, 255, THRESH_BINARY_INV);

    bgr.release();
    tmp.release();
    return result;
}