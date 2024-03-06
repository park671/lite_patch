package com.park.demo;

import android.app.Application;
import android.content.Context;

import com.park.lite_patch.Fixer;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Fixer.setup();
    }
}
