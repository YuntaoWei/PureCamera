cmake_minimum_required(VERSION 3.4.1)

add_library(
        opencv_test

        SHARED

        filter/filter_load.cpp)

set(OpencvSdk ${PROJECT_SOURCE_DIR}/src/main/jni)

include_directories(${OpencvSdk}/include)


add_library(opencv3 SHARED IMPORTED)

set_target_properties(opencv3
        PROPERTIES
        IMPORTED_LOCATION ${OpencvSdk}/opecv_lib/${ANDROID_ABI}/libopencv_java3.so)

find_library( log-lib
        log)

target_link_libraries(
        opencv_test
        opencv3
        ${log-lib})