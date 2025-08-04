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
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;

public class MyBroadCastReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String content = intent.getStringExtra(Constants.KEY_CONTENT);
            String title = intent.getStringExtra(Constants.KEY_TITLE);
            int id = intent.getIntExtra(Constants.RQ_CODE_ALARM, -1);
            long timeRepeat = intent.getLongExtra(Constants.REPEAT, 0);
            int idNote = intent.getIntExtra(Constants.KEY_NOTE,-1);
//            Toast.makeText(context, "" + timeRepeat, Toast.LENGTH_SHORT).show();
            if (content != null && title != null && id != -1 && idNote != -1) {
                showNotify(context, content, title, idNote);
                if (timeRepeat != 0) {
                    //nhac lai
                    long timeNext = System.currentTimeMillis() + timeRepeat;
                    Intent intent1 = new Intent(context, MyBroadCastReminder.class);
                    intent1.putExtra(Constants.KEY_CONTENT, content);
                    intent1.putExtra(Constants.KEY_TITLE, title);
                    intent1.putExtra(Constants.RQ_CODE_ALARM, id);
                    intent1.putExtra(Constants.REPEAT, timeRepeat);
                    intent1.putExtra(Constants.KEY_NOTE,idNote);
                    AlarmUtils.getInstance().setAlarmNotify(context, intent1, id, timeNext);
                } else {
                    //huy
//                    Toast.makeText(context, "" + timeRepeat, Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(context, MyBroadCastReminder.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent1,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmUtils.getInstance().setCancelAlarm(context, pendingIntent);
                }
            }
        }
    }

        private void showNotify (Context context, String content, String title,int id){
            //dau tien khoi tan Notification Channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(Constants.ChannelID,
                        Constants.ChannelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = context.getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            Intent intent = new Intent(context, NoteDetail.class);
            intent.putExtra(Constants.KEY_NOTE,id);
            intent.setAction("notify");

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            //tao noi dung notify
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.ChannelID);
            builder.setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.logo_2);

            //hien thi notify
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
            }
        }
}
