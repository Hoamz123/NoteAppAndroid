package com.hoamz.hoamz.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
public class AlarmUtils {
    private volatile static AlarmUtils instance;

    public static AlarmUtils getInstance(){
        if(instance == null){
            instance = new AlarmUtils();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void setAlarmNotify(Context context, Intent intent, int requestCode, long trigger){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmManager != null){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger, pendingIntent);
        }
    }

    public void setCancelAlarm(Context context, PendingIntent pendingIntent){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
