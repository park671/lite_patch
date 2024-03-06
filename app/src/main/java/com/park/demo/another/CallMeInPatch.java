package com.park.demo.another;

import android.util.Log;

public class CallMeInPatch {

    private static final String TAG = "CallMeInPatch";

    public static int getMyLuckCode() {
        Log.d(TAG, "getMyLuckCode() was called.");
        return 671;
    }

}
