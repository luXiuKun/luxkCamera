package com.luxk.luxkcamera;

import android.app.Application;

/**
 * Created by ASUS on 2019/2/24.
 */

public class CommApplication extends Application {
    public static CommApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CommApplication getInstance() {
        return instance;
    }
}
