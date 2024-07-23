cmake_minimum_required(VERSION 3.4.1)

# configure import libs
set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/external)

add_library(lib_cpuinfo STATIC IMPORTED)
set_target_properties(lib_cpuinfo PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/cpuinfo/lib/${ANDROID_ABI}/libcpuinfo.a)

# build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library(cpuinfo-libs SHARED
        cpuinfo-libs.cpp)

target_include_directories(cpuinfo-libs PRIVATE
        ${distribution_DIR}/cpuinfo/include)

target_link_libraries(cpuinfo-libs
        android
        lib_cpuinfo
        log)