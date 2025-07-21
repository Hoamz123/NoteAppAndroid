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
    private static final String TYPE_SHOW = "type_show";
    private static final String TYPE_SHOW_ARCHIVE = "type_show_archive";
    private static final String TYPE_SHOW_FAVORITE = "type_show_archive";
    private static final String TYPE_SHOW_TRASH = "type_show_archive";

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

    public void saveTypeShow(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW,type).apply();
    }

    public boolean getTypeShow(){
        return sharedPreferences.getBoolean(TYPE_SHOW,false);
    }


    public void saveTypeShowArchiver(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW_ARCHIVE,type).apply();
    }

    public boolean getTypeShowArchive(){
        return sharedPreferences.getBoolean(TYPE_SHOW_ARCHIVE,false);
    }
    public void saveTypeShowFavorite(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW_FAVORITE,type).apply();
    }
    public boolean getTypeShowFavorite(){
        return sharedPreferences.getBoolean(TYPE_SHOW_FAVORITE,false);
    }
    public void saveTypeShowTrash(boolean type){
        sharedPreferences.edit().putBoolean(TYPE_SHOW_TRASH,type).apply();
    }
    public boolean getTypeShowTrash(){
        return sharedPreferences.getBoolean(TYPE_SHOW_TRASH,false);
    }

    public void saveSortCondition(String key,String condition){
        sharedPreferences.edit().putString(key,condition).apply();
    }

    public String getSortCondition(String key){
        return sharedPreferences.getString(key, Constants.sortNewToOld);
    }

}
