cmake_minimum_required(VERSION 3.4.1)

if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3")
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    include_directories(${ANDROID_SYSROOT}/usr/include/aarch64-linux-android)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_64BIT -ffast-math -O3")
elseif (${ANDROID_ABI} STREQUAL "x86")
    include_directories(${ANDROID_SYSROOT}/usr/include/i686-linux-android)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_INTEL -ffast-math -O3")
endif()


add_library(mad
            SHARED
            src/main/jni/mad/version.c
            src/main/jni/mad/fixed.c
            src/main/jni/mad/bit.c
            src/main/jni/mad/timer.c
            src/main/jni/mad/stream.c
            src/main/jni/mad/frame.c
            src/main/jni/mad/synth.c
            src/main/jni/mad/decoder.c
            src/main/jni/mad/layer12.c
            src/main/jni/mad/layer3.c
            src/main/jni/mad/huffman.c

            src/main/jni/mad/jni_mp3_wrap.cpp
             )


include_directories(src/main/jni/mad/)

find_library(log-lib
             log)

target_link_libraries(mad
                      log)