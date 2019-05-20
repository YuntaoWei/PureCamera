cmake_minimum_required(VERSION 3.4.1)

add_library(
        camera_filter

        SHARED

        util/convert.cpp
        filter/imageproc.cpp
        filter/filter_load.cpp)

set(OpencvSdk ${PROJECT_SOURCE_DIR}/src/main/jni)

include_directories(${OpencvSdk}/include
        ${OpencvSdk}/filter
        ${OpencvSdk}/util)


add_library(opencv3 SHARED IMPORTED)

set_target_properties(opencv3
        PROPERTIES
        IMPORTED_LOCATION ${OpencvSdk}/opecvlib/${ANDROID_ABI}/libopencv_java3.so)

find_library( log-lib
        log)

target_link_libraries(
        camera_filter
        opencv3
        ${log-lib})