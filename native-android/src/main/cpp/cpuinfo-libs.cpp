#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <android/log.h>
#include "cpuinfo.h"
#include <string>

#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, "cpuinfo-libs::", __VA_ARGS__))

extern "C"
JNIEXPORT void JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_initLibrary(JNIEnv *env,
                                                                         jobject thiz) {
    if (!cpuinfo_initialize()) {
        LOGI("Error during initialization");
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getCpuName(JNIEnv *env, jobject thiz) {
    if (!cpuinfo_initialize()) {
        return env->NewStringUTF("");
    }
    return env->NewStringUTF(cpuinfo_get_package(0)->name);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_hasArmNeon(JNIEnv *env, jobject thiz) {
    if (!cpuinfo_initialize()) {
        return false;
    }
    return cpuinfo_has_arm_neon();
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getL1dCaches(JNIEnv *env,
                                                                          jobject thiz) {
    if (!cpuinfo_initialize() || cpuinfo_get_l1d_caches_count() == 0) {
        return nullptr;
    }

    uint32_t cacheCount = cpuinfo_get_l1d_caches_count();
    jintArray result = env->NewIntArray(cacheCount);
    jint internalArray[cacheCount];
    auto l1dCaches = cpuinfo_get_l1d_caches();
    for (uint32_t i = 0; i < cacheCount; i++) {
        internalArray[i] = l1dCaches[i].size;
    }
    env->SetIntArrayRegion(result, 0, cacheCount, internalArray);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getL1iCaches(JNIEnv *env,
                                                                          jobject thiz) {
    if (!cpuinfo_initialize() || cpuinfo_get_l1i_caches_count() == 0) {
        return nullptr;
    }

    uint32_t cacheCount = cpuinfo_get_l1i_caches_count();
    jintArray result = env->NewIntArray(cacheCount);
    jint internalArray[cacheCount];
    auto l1iCaches = cpuinfo_get_l1i_caches();
    for (uint32_t i = 0; i < cacheCount; i++) {
        internalArray[i] = l1iCaches[i].size;
    }
    env->SetIntArrayRegion(result, 0, cacheCount, internalArray);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getL2Caches(JNIEnv *env,
                                                                         jobject thiz) {
    if (!cpuinfo_initialize() || cpuinfo_get_l2_caches_count() == 0) {
        return nullptr;
    }

    uint32_t cacheCount = cpuinfo_get_l2_caches_count();
    jintArray result = env->NewIntArray(cacheCount);
    jint internalArray[cacheCount];
    auto l2Caches = cpuinfo_get_l2_caches();
    for (uint32_t i = 0; i < cacheCount; i++) {
        internalArray[i] = l2Caches[i].size;
    }
    env->SetIntArrayRegion(result, 0, cacheCount, internalArray);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getL3Caches(JNIEnv *env,
                                                                         jobject thiz) {
    if (!cpuinfo_initialize() || cpuinfo_get_l3_caches_count() == 0) {
        return nullptr;
    }

    uint32_t cacheCount = cpuinfo_get_l3_caches_count();
    jintArray result = env->NewIntArray(cacheCount);
    jint internalArray[cacheCount];
    auto l3Caches = cpuinfo_get_l3_caches();
    for (uint32_t i = 0; i < cacheCount; i++) {
        internalArray[i] = l3Caches[i].size;
    }
    env->SetIntArrayRegion(result, 0, cacheCount, internalArray);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getL4Caches(JNIEnv *env,
                                                                         jobject thiz) {
    if (!cpuinfo_initialize() || cpuinfo_get_l4_caches_count() == 0) {
        return nullptr;
    }

    uint32_t cacheCount = cpuinfo_get_l4_caches_count();
    jintArray result = env->NewIntArray(cacheCount);
    jint internalArray[cacheCount];
    auto l4Caches = cpuinfo_get_l4_caches();
    for (uint32_t i = 0; i < cacheCount; i++) {
        internalArray[i] = l4Caches[i].size;
    }
    env->SetIntArrayRegion(result, 0, cacheCount, internalArray);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_kgurgul_cpuinfo_data_provider_CpuDataNativeProvider_getNumberOfCores(JNIEnv *env,
                                                                              jobject thiz) {
    if (!cpuinfo_initialize()) {
        return 1;
    }
    return cpuinfo_get_cores_count();
}
