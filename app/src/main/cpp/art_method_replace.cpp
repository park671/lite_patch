#include "art_method_replace.h"

static constexpr uint32_t kAccPublic = 0x0001;  // class, field, method, ic
static constexpr uint32_t kAccPrivate = 0x0002;  // field, method, ic
static constexpr uint32_t kAccProtected = 0x0004;  // field, method, ic

static constexpr uint32_t kAccSuper = 0x0020;  // class (not used in dex)
static constexpr uint32_t kAccVolatile = 0x0040;  // field
static constexpr uint32_t kAccTransient = 0x0080;  // field
static constexpr uint32_t kAccInterface = 0x0200;  // class, ic
static constexpr uint32_t kAccAnnotation = 0x2000;  // class, ic (1.5)
static constexpr uint32_t kAccEnum = 0x4000;  // class, field, ic (1.5)

static uint32_t art_method_size = 0;
static uint8_t access_flag_available = 0;

static void *GetArtMethod(JNIEnv *env, jobject method) {
    jclass methodClass = env->GetObjectClass(method);
    jfieldID artMethodFieldID = env->GetFieldID(methodClass, "artMethod", "J");
    if (artMethodFieldID == NULL) {
        return NULL;
    }
    return reinterpret_cast<void *>(env->GetLongField(method, artMethodFieldID));
}

bool realTestAccessFlagAvailable(JNIEnv *env, jobject src) {
    art::mirror::ArtMethod *artMethod =
            (art::mirror::ArtMethod *) GetArtMethod(env, src);
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[+] artMethod ptr=%p\n", artMethod);

    if (
            (artMethod->access_flags_ & kAccSuper)
            || (artMethod->access_flags_ & kAccVolatile)
            || (artMethod->access_flags_ & kAccTransient)
            || (artMethod->access_flags_ & kAccInterface)
            || (artMethod->access_flags_ & kAccAnnotation)
            || (artMethod->access_flags_ & kAccEnum)
            ) {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[-] error flag.\n");
        return false;
    }

    bool hasFlag = false;
    if (artMethod->access_flags_ & kAccPublic) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "public\n");
        hasFlag = true;
    }
    if (artMethod->access_flags_ & kAccPrivate) {
        if (hasFlag) {
            __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[-] error flag.\n");
            return false;
        }
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "private\n");
        hasFlag = true;
    }
    if (artMethod->access_flags_ & kAccProtected) {
        if (hasFlag) {
            __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[-] error flag.\n");
            return false;
        }
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "protect\n");
        hasFlag = true;
    }
    return hasFlag;
}

/**
 * 粗略校验access_flags_
 * @param env
 * @param src
 * @return
 */
bool testAccessFlagAvailable(JNIEnv *env, jobject src) {
    access_flag_available = realTestAccessFlagAvailable(env, src);
    return access_flag_available != 0;
}

/**
 * 计算连续Method内存offset
 * @param env
 * @param method1
 * @param method2
 */
uint32_t calcMethodSize(JNIEnv *env, jobject method1, jobject method2) {
    void *artMethod1 =
            GetArtMethod(env, method1);
    void *artMethod2 =
            GetArtMethod(env, method2);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "artMethod1 ptr=%p\n", artMethod1);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "artMethod2 ptr=%p\n", artMethod2);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "inbuilt ArtMethod size=%u\n",
                        sizeof(art::mirror::ArtMethod));
    art_method_size = ((uint64_t) artMethod2 - (uint64_t) artMethod1);
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[+] runtime ArtMethod offset=%d\n",
                        sizeof(art::mirror::ArtMethod));
    if (art_method_size == sizeof(art::mirror::ArtMethod)) {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[+] ArtMethod is verified.\n");
    }
    return art_method_size;
}

void replace(JNIEnv *env, jobject method1, jobject method2) {
    if (art_method_size <= 0 || art_method_size >= 64) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "[-] invalid size, will not replace.\n");
        return;
    }
    void *artMethod1Ptr =
            GetArtMethod(env, method1);
    void *artMethod2Ptr =
            GetArtMethod(env, method2);
    if (access_flag_available) {
        art::mirror::ArtMethod *artMethod1 =
                (art::mirror::ArtMethod *) artMethod1Ptr;
        art::mirror::ArtMethod *artMethod2 =
                (art::mirror::ArtMethod *) artMethod2Ptr;
        artMethod1->access_flags_ =
                artMethod1->access_flags_ & (~kAccPrivate) & (~kAccProtected) | kAccPublic;
        artMethod2->access_flags_ =
                artMethod2->access_flags_ & (~kAccPrivate) & (~kAccProtected) | kAccPublic;
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[+] fix access_flags_ finish\n");
    }
    memcpy(artMethod1Ptr, artMethod2Ptr, art_method_size);
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[+] replace art method finish\n");
}

void setFieldFlag(JNIEnv *env, jobject field) {
    art::mirror::ArtField *artField =
            (art::mirror::ArtField *) env->FromReflectedField(field);
    artField->access_flags_ =
            artField->access_flags_ & (~kAccPrivate) & (~kAccProtected) | kAccPublic;
    LOGD("[+] setFieldFlag: %d ", artField->access_flags_);
}
