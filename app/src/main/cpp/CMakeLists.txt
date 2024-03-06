cmake_minimum_required(VERSION 3.22.1)

project("lite_patch")

add_library(${CMAKE_PROJECT_NAME} SHARED
        art_method_replace.cpp
        lite_patch.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)
