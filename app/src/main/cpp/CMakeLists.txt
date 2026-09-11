cmake_minimum_required(VERSION 3.22.1)

project("skyverse_jni")

add_library(
        skyverse_jni
        SHARED
        llama_jni.cpp
        whisper_jni.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        skyverse_jni
        ${log-lib})
