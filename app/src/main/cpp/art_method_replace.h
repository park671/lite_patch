//
// Created by Park Yu on 2024/3/6.
//

#ifndef LITE_PATCH_ART_METHOD_REPLACE_H
#define LITE_PATCH_ART_METHOD_REPLACE_H

#include <time.h>
#include <stdlib.h>
#include <stddef.h>
#include <assert.h>

#include <stdbool.h>
#include <fcntl.h>
#include <dlfcn.h>

#include <sys/stat.h>
#include <dirent.h>
#include <unistd.h>
#include <ctype.h>
#include <errno.h>
#include <utime.h>
#include <sys/types.h>
#include <sys/wait.h>

#include "art_7_14.h"
#include "common.h"

extern uint32_t __attribute__ ((visibility ("hidden")))
calcMethodSize(JNIEnv *env, jobject method1, jobject method2);

extern void __attribute__ ((visibility ("hidden")))
replace(JNIEnv *env, jobject method1, jobject method2);

extern void __attribute__ ((visibility ("hidden")))
setFieldFlag(JNIEnv *env, jobject field);

extern bool __attribute__ ((visibility ("hidden")))
testAccessFlagAvailable(JNIEnv *env, jobject src);

#endif //LITE_PATCH_ART_METHOD_REPLACE_H
