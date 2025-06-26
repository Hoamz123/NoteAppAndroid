package com.hoamz.hoamz.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    //luu cac hang ko doi
    public static final String sortAToZ = "a_z";
    public static final String TitleCreateNewLabel = "Tạo danh mục mới";
    public static final String TitleEditLabel = "Sửa tên danh mục";
    public static final String sortZToA = "z_a";
    public static final String sortOldToNew = "asc";
    public static final String sortNewToOld = "desc";
    public static final String labelAll = "All";
    public static final String READING_MODE = "ReadingMode";
    public static final String EDIT_MODE = "EditMode";
    public static final String PIN = "Pin";
    public static final String UNPIN = "Unpin";
    public static final String UN_FAVORITE = "UnFavorite";
    public static final String FAVORITE = "Favorite";
    public static final String KEY_NOTE = "note";
    public static final String DATE_SELECTED = "dateSelected";
    public static final String LABEL_CURRENT = "labelCurrent";
    public static final String KEY_TITLE = "title_notify";
    public static final String KEY_CONTENT = "content_notify";
    public static final String RQ_CODE_ALARM = "Alarm";
    public static final String ID_WIDGET_CLICK = "idWidgetClick";
    public static final String REPEAT = "repeat";
    public static final String EDIT = "edit_widget";
    public static final String BACK = "back";
    public static final String HINT = "Không tìm thấy tiện ích cần quản lý \nVui lòng tạo tiện ích!";
    public static final String SOURCE = "source";
    public static Set<Integer> backGroundDark = new HashSet<>();
    public static Set<Integer> backGroundLight = new HashSet<>();
    public static long time30Days = 2592000000L;

    //set mau sang -> chu den
    public static void init(){
        //dark
        backGroundDark.add(R.drawable.img_20);
        backGroundDark.add(R.drawable.img_21);
        backGroundDark.add(R.drawable.img_22);
        backGroundDark.add(R.drawable.img_23);
        backGroundDark.add(R.drawable.img_24);
        backGroundDark.add(R.drawable.img_25);
        backGroundDark.add(R.drawable.img_29);
        backGroundDark.add(R.drawable.bg_wg10);
        backGroundDark.add(R.drawable.bg_wg7);
        backGroundDark.add(R.drawable.bg_wg2);
        backGroundDark.add(R.drawable.bg_wg8);
        backGroundDark.add(R.drawable.img_18);

        //light
        backGroundLight.add(R.drawable.img_11);
        backGroundLight.add(R.drawable.img_12);
        backGroundLight.add(R.drawable.img_13);
        backGroundLight.add(R.drawable.img_14);
        backGroundLight.add(R.drawable.img_15);
        backGroundLight.add(R.drawable.img_16);
        backGroundLight.add(R.drawable.img_17);
        backGroundLight.add(R.drawable.img_19);
        backGroundLight.add(R.drawable.img_3);
        backGroundLight.add(R.drawable.img_4);
        backGroundLight.add(R.drawable.img_5);
        backGroundLight.add(R.drawable.img_6);
        backGroundLight.add(R.drawable.img_7);
        backGroundLight.add(R.drawable.img_8);
        backGroundLight.add(R.drawable.img_9);
        backGroundLight.add(R.drawable.img_31);
        backGroundLight.add(R.drawable.img_26);
    }

    public static void setUpAlarm(Activity act,Note note,long trigger){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
        intent.putExtra(Constants.KEY_CONTENT, note.getContent());
        intent.putExtra(Constants.KEY_TITLE, note.getTitle());
        intent.putExtra(Constants.RQ_CODE_ALARM, note.getId());
        intent.putExtra(Constants.KEY_NOTE,note);
        AlarmUtils.getInstance().setAlarmNotify(act,intent,note.getId(),trigger);//tru di 50000ms
    }

    public static void setCancelAlarm(Activity act,Note note){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
        intent.putExtra(Constants.KEY_CONTENT, note.getContent());
        intent.putExtra(Constants.KEY_TITLE, note.getTitle());
        intent.putExtra(Constants.RQ_CODE_ALARM, note.getId());
        intent.putExtra(Constants.KEY_NOTE,note);
        PendingIntent p = PendingIntent.getBroadcast(act,note.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmUtils.getInstance().setCancelAlarm(act,p);
    }

    @SuppressLint("DefaultLocale")
    public static String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d",hour,minutes);
    }
    @SuppressLint("DefaultLocale")
    public static String getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return String.format("%02d/%02d/%04d",day,month,year);
    }

}
