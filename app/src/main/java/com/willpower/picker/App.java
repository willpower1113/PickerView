package com.willpower.picker;

import android.app.Application;

import com.willpower.picker.window.FloatManager;

/**
 * Created by Administrator on 2018/10/26.
 */

public class App extends Application {
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        FloatManager.getManager().init(this);//缓存application
    }
}
