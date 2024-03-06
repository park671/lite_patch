#include <jni.h>
#include "art_method_replace.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_park_lite_1patch_Fixer_getArtMethodSize(JNIEnv *env, jclass clazz, jobject a, jobject b) {
    return calcMethodSize(env, a, b);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_park_lite_1patch_Fixer_testInBuiltArtMethod(JNIEnv *env, jclass clazz, jobject a) {
    if (testAccessFlagAvailable(env, a)) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_park_lite_1patch_Fixer_replace(JNIEnv *env, jclass clazz, jobject src, jobject dest) {
    replace(env, src, dest);
}