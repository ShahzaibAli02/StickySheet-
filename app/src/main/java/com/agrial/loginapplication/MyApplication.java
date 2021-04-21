package com.agrial.loginapplication;

import androidx.multidex.MultiDexApplication;

/**
 * Created by Junaid Ali on 14,March,2020
 */
public class MyApplication extends MultiDexApplication {

    public static boolean isFirstTime = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
