package com.hoamz.hoamz.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;
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
    public static final String REPEAT = "repeat";
    public static Set<Integer> colorLightPicker = new HashSet<>();
    public static Set<Integer> colorDarkPicker = new HashSet<>();

    //set mau sang -> chu den
    public static void init(Context context){
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color1));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color2));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color3));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color4));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color5));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color7));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color9));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color10));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color11));

// dark -> chu trang
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color12));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color6));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color15));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color13));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color16));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color14));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color8));
    }

    public static void setUpAlarm(Activity act,Note note,long trigger){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
        intent.putExtra(Constants.KEY_CONTENT, note.getContent());
        intent.putExtra(Constants.KEY_TITLE, note.getTitle());
        intent.putExtra(Constants.RQ_CODE_ALARM, note.getId());
        intent.putExtra(Constants.KEY_NOTE,note);
        AlarmUtils.getInstance().setAlarmNotify(act,intent,note.getId(),trigger);//tru di 50000ms
    }
}
