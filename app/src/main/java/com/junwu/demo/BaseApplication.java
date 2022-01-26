package com.junwu.demo;

import android.app.Application;
import android.content.Context;

import com.junwu.demo.Utils.PaiAppLog;

public class BaseApplication extends Application {

    public static volatile Context sApplicationContext;

    @Override
    public void onCreate() {
        try {
            sApplicationContext = getApplicationContext();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            PaiAppLog.e(throwable.getMessage());
        }
        super.onCreate();
        if (sApplicationContext == null) {
            sApplicationContext = getApplicationContext();
        }
    }
}
