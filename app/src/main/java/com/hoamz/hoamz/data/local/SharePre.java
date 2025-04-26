package com.hoamz.hoamz.data.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.hoamz.hoamz.utils.Constants;

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
    public static void init(Context context){
        if(instance == null){
            instance = new SharePre(context);
        }
    }

    public static SharePre getInstance() {
        return instance;
    }

    @SuppressLint("CommitPrefEdits")
    public void saveSecondRunApp(){
        sharedPreferences.edit().putBoolean(FIRST_RUN,false).apply();
    }

    public boolean checkFirstRunApp(){
        return sharedPreferences.getBoolean(FIRST_RUN,true);
    }
    public boolean checkReadingMode(){
        return sharedPreferences.getBoolean(READING_MODE,true);//ban dau chua co gia tri that thi se tra ve true
    }

    public void saveReadingMode(boolean isReadingMode){
        sharedPreferences.edit().putBoolean(READING_MODE,isReadingMode).apply();
    }

    public void saveLabelCurrent(String label){
        sharedPreferences.edit().putString(LABEL_CURRENT,label).apply();
    }

    public String getLabelCurrent(){
        return sharedPreferences.getString(LABEL_CURRENT, Constants.labelAll);
    }

    public void saveTypeShow(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW,type).apply();
    }

    public boolean getTypeShow(){
        return sharedPreferences.getBoolean(TYPE_SHOW,false);
    }

}
