#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <android/log.h>
#include <cpuinfo.h>
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