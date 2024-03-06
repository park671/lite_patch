package com.park.lite_patch;

import android.util.Log;

import java.lang.reflect.Method;

public class Fixer {

    private static final String TAG = "LitePatch";

    private static native long getArtMethodSize(Method pre, Method next);

    private static native boolean testInBuiltArtMethod(Method a);

    private static native void replace(Method src, Method dest);

    private static volatile boolean fixerAvailable = false;

    public static void setup() {
        try {
            System.loadLibrary("lite_patch");
            Method pre = MethodStub.class.getDeclaredMethod("func1");
            Method next = MethodStub.class.getDeclaredMethod("func2");
            Log.d(TAG, "art method size=" + getArtMethodSize(pre, next));
            testInBuiltArtMethod(pre);
            fixerAvailable = true;
            Log.i(TAG, "[+] fixer is ready!");
        } catch (Throwable tr) {
            Log.e(TAG, "[-] getArtMethodSize fail", tr);
        }
    }

    public static void fix(Method needFixMethod, Method fixedMethod) {
        if (!fixerAvailable) {
            Log.w(TAG, "[-] not available");
            return;
        }
        replace(needFixMethod, fixedMethod);
    }

}
