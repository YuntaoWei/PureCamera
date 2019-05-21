//
// Created by ubt on 2019/5/21.
//

#ifndef PURECAMERA_FILTER_H
#define PURECAMERA_FILTER_H

#endif //PURECAMERA_FILTER_H
#include <jni.h>

extern "C"

#define JAVA_CLASS "com/pure/camera/filter/engine/NativeFilter"

jboolean do_filter_gray(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file);

jboolean do_filter_mosaic(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int square, int orientation, jstring file);

jboolean do_filter_relief(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file);

jboolean do_filter_wb(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file);

jboolean do_yuv2rgb(JNIEnv *env, jclass obj, jbyteArray buf, int w, int h, int orientation, jstring file);
