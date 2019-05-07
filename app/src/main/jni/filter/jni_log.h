#include <android/log.h>

#ifdef DEBUG

    #ifndef LOG_TAG
        #define LOG_TAG "Android_Log"
    #endif
    #define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
    #define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
    #define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
    #define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
    #define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

#else

    #define LOGE(...)
    #define LOGV(...)
    #define LOGD(...)
    #define LOGI(...)
    #define LOGW(...)

#endif