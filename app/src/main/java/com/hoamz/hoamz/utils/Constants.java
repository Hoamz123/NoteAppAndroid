package com.hoamz.hoamz.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Constants {
    //luu cac hang ko doi
    public static final String sortAToZ = "Tên A đến Z";
    public static final String TitleCreateNewLabel = "Tạo danh mục mới";
    public static final String TitleEditLabel = "Sửa tên danh mục";
    public static final String sortZToA = "Tên Z đến A";
    public static final String sortOldToNew = "Theo thời gian tạo (Cũ nhất trước)";
    public static final String sortNewToOld = "Theo thời gian tạo (Mới nhất trước)";
    public static final String labelAll = "All";
    public static final String READING_MODE = "Reading Mode";
    public static final String EDIT_MODE = "Edit Mode";
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
    public static final String EMPTY_NOTIFY = "Chưa có ghi chú hôm nay\nNhấn + để thêm";
    public static final String SOURCE = "source";
    public static Set<Integer> backGroundDark = new HashSet<>();
    public static Set<Integer> backGroundLight = new HashSet<>();
    public static long time30Days = 30L * 24 * 60 * 60 * 1000L;
    public static List<Integer> listTimeRepeat = new ArrayList<>();
    public static Long fifteenMin = 60 * 1000L;
    public static Long thirtyMin = 30L * 60 * 1000L;
    public static Long oneHour = 60L * 60 * 1000L;
    public static Long oneDay = 24L * 60 * 60 * 1000L;
    public static Long oneWeek = 7L * 24 * 60 * 60 * 1000L;
    public static Long oneMonth = 30L * 24 * 60 * 60 * 1000L;

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
        backGroundLight.add(R.drawable.img_10);
        backGroundLight.add(R.drawable.img_3);
        backGroundLight.add(R.drawable.img_4);
        backGroundLight.add(R.drawable.img_5);
        backGroundLight.add(R.drawable.img_6);
        backGroundLight.add(R.drawable.img_7);
        backGroundLight.add(R.drawable.img_8);
        backGroundLight.add(R.drawable.img_9);
        backGroundLight.add(R.drawable.img_31);
        backGroundLight.add(R.drawable.img_26);


        //listTimeRepeat
        listTimeRepeat.add(R.id.tv15min);
        listTimeRepeat.add(R.id.tv30min);
        listTimeRepeat.add(R.id.tv1hour);
        listTimeRepeat.add(R.id.tv1day);
        listTimeRepeat.add(R.id.tv1week);
        listTimeRepeat.add(R.id.tv1month);

    }

    public static void setUpAlarm(Activity act,Note note,long trigger){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
        intent.putExtra(Constants.KEY_CONTENT, note.getContent());
        intent.putExtra(Constants.KEY_TITLE, note.getTitle());
        intent.putExtra(Constants.RQ_CODE_ALARM, note.getId());
        intent.putExtra(Constants.KEY_NOTE,note);
        AlarmUtils.getInstance().setAlarmNotify(act,intent,note.getId(),trigger);
    }

    public static void setCancelAlarm(Activity act,Note note){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
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
    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    @SuppressLint("NonConstantResourceId")
    public static void onBackgroundSelectTimeRepeat(View group, TextView tView, onSelectTimeRepeat onSelectTimeRepeat){
        int id = tView.getId();
        for(int i : listTimeRepeat){
            if(i != id){
                TextView tV = group.findViewById(i);
                tV.setBackgroundResource(R.drawable.custom_bg_time_repeat);
                tV.setTextColor(Color.GRAY);
            }
            else{
                tView.setBackgroundResource(R.drawable.custom_bg_repeat_click);
                tView.setTextColor(Color.WHITE);
            }
        }
        //call back time to act
        if(id == R.id.tv15min){
            onSelectTimeRepeat.onSelectTime(fifteenMin);
        }
        else if(id == R.id.tv30min){
            onSelectTimeRepeat.onSelectTime(thirtyMin);
        }
        else if(id == R.id.tv1hour){
            onSelectTimeRepeat.onSelectTime(oneHour);
        }
        else if(id == R.id.tv1day){
            onSelectTimeRepeat.onSelectTime(oneDay);
        }
        else if(id == R.id.tv1week){
            onSelectTimeRepeat.onSelectTime(oneWeek);
        }
        else if(id == R.id.tv1month){
            onSelectTimeRepeat.onSelectTime(oneMonth);
        }
    }

    public interface onSelectTimeRepeat{
        void onSelectTime(Long timeRepeat);
    }

}
