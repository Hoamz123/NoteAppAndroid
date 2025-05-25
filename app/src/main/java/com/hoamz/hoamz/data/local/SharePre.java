package com.hoamz.hoamz.data.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public  class SharePre {
    private static SharePre instance;
    private static final String name_save_label = "app_save";
    private final SharedPreferences sharedPreferences;
    private static final String FIRST_RUN = "first_run_app";
    private static final String READING_MODE = "reading_mode";
    private static final String LABEL_CURRENT = "label_current";
    private static final String TYPE_SHOW = "type_show";

    @SuppressLint("CommitPrefEdits")
    private SharePre(Context context){
        sharedPreferences = context.getSharedPreferences(name_save_label,Context.MODE_PRIVATE);
    }
    //singleton
    public static SharePre getInstance(Context context) {
        if(instance == null){
            instance = new SharePre(context);
        }
        return instance;
    }

    @SuppressLint("CommitPrefEdits")
    public void saveSecondRunApp(){
        sharedPreferences.edit().putBoolean(FIRST_RUN,true).apply();
    }

    public boolean checkFirstRunApp(){
        return sharedPreferences.getBoolean(FIRST_RUN,false);
    }

    public boolean checkReadingMode(){
        return sharedPreferences.getBoolean(READING_MODE,false);//ban dau chua co gia tri that thi se tra ve false
    }

    public void saveReadingMode(boolean isReadingMode){
        sharedPreferences.edit().putBoolean(READING_MODE,isReadingMode).apply();
    }

    public void saveTypeShow(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW,type).apply();
    }

    public boolean getTypeShow(){
        return sharedPreferences.getBoolean(TYPE_SHOW,false);
    }

}
