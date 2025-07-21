package com.hoamz.hoamz.Broadcast;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.DAO.NoteDao;
import com.hoamz.hoamz.data.DAO.NoteDatabase;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;

import java.security.Key;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBroadCastReminder extends BroadcastReceiver {
    private final String ChannelID = "notifyChannel";
    private final String ChannelName = "Notify";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            Note note = intent.getParcelableExtra(Constants.KEY_NOTE);
            if(note != null){
                showNotify(context,note);
                long timeRepeat = SharePre.getInstance(context).getTimeRepeat(note.getId());
                Log.e("ID",note.getId() + "");
                Log.e("Time",timeRepeat + "");
                if(timeRepeat > 0){
                    //nhac lai
                    long timeNext = System.currentTimeMillis() + timeRepeat;
                    Intent intent1 = new Intent(context, MyBroadCastReminder.class);
                    intent1.putExtra(Constants.KEY_NOTE,note);
                    AlarmUtils.getInstance().setAlarmNotify(context,intent1,note.getId(),timeNext);
                }
                else{
                    Intent intent1 = new Intent(context, MyBroadCastReminder.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,note.getId(),intent1,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmUtils.getInstance().setCancelAlarm(context,pendingIntent);
                }
            }
        }
    }
    private void showNotify(Context context,Note note){
        //dau tien khoi tan Notification Channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(ChannelID,
                    ChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if(manager != null){
                manager.createNotificationChannel(channel);
            }
        }

        //tao noi dung notify
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,ChannelID);
        builder.setContentTitle(note.getTitle())
                .setContentText(note.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_notifiation);

        //hien thi notify
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
            }
        }
}
