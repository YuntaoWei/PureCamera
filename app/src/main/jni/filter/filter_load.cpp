//
// Created by wyt on 2019/5/7.
//
#include <assert.h>
#include "filter.h"

static JNINativeMethod gMethods[] = {
        {"doFilterGray",   "([BIIILjava/lang/String;)Z",  (void *) do_filter_gray},
        {"doFilterMosaic", "([BIIIILjava/lang/String;)Z", (void *) do_filter_mosaic},
        {"doFilterRelief", "([BIIILjava/lang/String;)Z",  (void *) do_filter_relief},
        {"doYuv2RGB", "([BIIILjava/lang/String;)Z",  (void *) do_yuv2rgb},
        {"doFilterWB", "([BIIILjava/lang/String;)Z",  (void *) do_filter_wb},
        {"doFilterPositive", "([BIIILjava/lang/String;)Z",  (void *) do_filter_positive},
        {"doFilterComic", "([BIIILjava/lang/String;)Z",  (void *) do_filter_comic}
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    return registerNativeMethods(env, JAVA_CLASS, gMethods,
                                 sizeof(gMethods) / sizeof(gMethods[0]));
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        return -1;
    }

    return JNI_VERSION_1_4;
}
