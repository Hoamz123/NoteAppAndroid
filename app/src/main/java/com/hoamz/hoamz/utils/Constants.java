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
import com.hoamz.hoamz.data.model.Label;
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
    public static final String sortAToZ = "Name A - Z";
    public static final String TitleCreateNewLabel = "Add New Category";
    public static final String TitleEditLabel = "Rename Category";
    public static final String sortZToA = "Name Z - A";
    public static final String sortOldToNew = "Created Time (Oldest first)";
    public static final String sortNewToOld = "Created Time (Newest first)";
    public static final String labelAll = "All";
    public static final String READING_MODE = "Reading Mode";
    public static final String TAKE_PHOTO = "take_photo";
    public static final String CHOOSE_IMAGE = "choose_image";

    public static final List<Label> listLabelDefault = List.of(
            new Label("All"),
            new Label("Home"),
            new Label("Work")
    );
    public static final String EDIT_MODE = "Editing Mode";
    public static final String PIN = "Pin";
    public static final String UNPIN = "Unpin";
    public static final String UN_FAVORITE = "Remove from Favorites";
    public static final String FAVORITE = "Favorite";
    public static final String RESTORE = "Restore";
    public static final String KEY_NOTE = "note";
    public static final String DATE_SELECTED = "dateSelected";
    public static final String LABEL_CURRENT = "labelCurrent";
    public static final String KEY_TITLE = "title_notify";
    public static final String KEY_CONTENT = "content_notify";
    public static final String RQ_CODE_ALARM = "Alarm";
    public static final String ID_NOTE_CLICK = "id_note_click";
    public static final String REPEAT = "repeat";
    public static final String ChannelID = "notifyChannel";
    public static final String ChannelName = "Notify";
    public static final String EMPTY_NOTIFY = "Tap the + icon to create a note";
    public static final String EMPTY_REMINDER = "You haven't created any Reminder not yet!";

    public static final String DELETE = "Delete";
    public static final String DELETE_S = "Deletes";
    public static final String ARCHIVE = "Archive";
    public static final String UN_ARCHIVE = "Unarchive";
    public static final String UN_ARCHIVE_s = "Unarchives";
    public static final String ARCHIVE_S = "Archives";
    public static Set<Integer> backGroundDark = new HashSet<>();
    public static Set<Integer> backGroundLight = new HashSet<>();

    public static String feature = "Welcome";
    public static final String content_welcome = "\uD83D\uDE80 Key Features \n\n" +
            "\uD83D\uDCDD Create, edit, and organize your notes effortlessly\n" +
            "\n" +
            "\uD83D\uDCCC Pin important notes to access them faster\n" +
            "\n" +
            "\uD83D\uDD0D Quickly search notes by title or content\n" +
            "\n" +
            "\uD83D\uDCC2 Archive notes you want to keep but hide from the main list\n" +
            "\n" +
            "⏰ Set reminders so you never forget important tasks\n" +
            "\n" +
            "\uD83D\uDDD3\uFE0F View and manage notes by date with built-in calendar\n" +
            "\n" +
            "\uD83D\uDD14 Receive helpful notifications for your scheduled notes\n" +
            "\n" +
            "\uD83D\uDDC3\uFE0F Categorize your notes into folders";

    public static final String ActionClickNotify = "ActionTouchNotify";


    public static Long daily = 24 * 60 * 60 * 1000L;
    public static Long weekly = 7L * 24 * 60 * 60 * 1000L;
    public static Long monthly = 30L * 24 * 60 * 60 * 1000L;
    public static Long yearly = 12 * 30L * 24 * 60 * 60 * 1000L;
    public static final String NONE = "None";
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String YEARLY = "Yearly";


    //set mau sang -> chu den
    public static void init(){
        //dark
        backGroundDark.add(R.drawable.img_20);
        backGroundDark.add(R.drawable.img_21);
        backGroundDark.add(R.drawable.img_22);
        backGroundDark.add(R.drawable.img_24);
        backGroundDark.add(R.drawable.img_25);
        backGroundDark.add(R.drawable.img_29);
        backGroundDark.add(R.drawable.bg_wg10);
        backGroundDark.add(R.drawable.bg_wg7);
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
        backGroundLight.add(R.drawable.img_10);
        backGroundLight.add(R.drawable.img_5);
        backGroundLight.add(R.drawable.img_6);
    }

    public static void setUpAlarm(Activity act,Note note,int requestCodePending,long trigger,long timeRepeat){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
        intent.putExtra(Constants.KEY_CONTENT, note.getContent());
        intent.putExtra(Constants.KEY_TITLE, note.getTitle());
        intent.putExtra(Constants.RQ_CODE_ALARM,requestCodePending);
        intent.putExtra(Constants.REPEAT,timeRepeat);
        intent.putExtra(Constants.KEY_NOTE,note.getId());
        AlarmUtils.getInstance().setAlarmNotify(act,intent,requestCodePending,trigger);
    }

    public static void setCancelAlarm(Activity act,int rqCode){
        Intent intent = new Intent(act, MyBroadCastReminder.class);
//        intent.putExtra(Constants.KEY_NOTE,note);
        PendingIntent p = PendingIntent.getBroadcast(act,rqCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmUtils.getInstance().setCancelAlarm(act,p);
    }
}
