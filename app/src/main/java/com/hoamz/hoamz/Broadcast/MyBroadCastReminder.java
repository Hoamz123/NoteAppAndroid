package com.hoamz.hoamz.Broadcast;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;

public class MyBroadCastReminder extends BroadcastReceiver {
    private final String ChannelID = "notifyChannel";
    private final String ChannelName = "Notify";

    @Override
    public void onReceive(Context context, Intent intent) {
        //xy ly neu nhu can nhac lai(tam thoi thi chua dong den )
        //bayh minh chi can thong bao la duoc
        //lay ra noi dung nhac nho tu intent
        if(intent != null){
            Note note = intent.getParcelableExtra(Constants.KEY_NOTE);
            if(note != null){
                showNotify(context,note);
            }
            //neu ko repeat thi huy notify
            if(note != null){
                Intent intentRepeat = new Intent(context, MyBroadCastReminder.class);
                intentRepeat.putExtra(Constants.KEY_CONTENT, note.getContent());
                intentRepeat.putExtra(Constants.KEY_TITLE, note.getTitle());
                intentRepeat.putExtra(Constants.RQ_CODE_ALARM, note.getId());
                intentRepeat.putExtra(Constants.KEY_NOTE,note);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        note.getId(),
                        intentRepeat,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );
                if(note.getTimeRepeat() > 0){
                    //co nhac lai
                    long newTrigger = note.getTrigger() + note.getTimeRepeat();
                    AlarmUtils.getInstance().setAlarmNotify(context,intentRepeat,note.getId(),newTrigger);
                }
                else{
                    //khong nhac lai -> huy
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
