package com.hoamz.hoamz.app;

import android.app.Application;
import android.util.Log;

import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.utils.Constants;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init(getApplicationContext());
    }

}
