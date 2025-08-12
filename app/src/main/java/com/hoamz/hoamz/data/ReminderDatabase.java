package com.hoamz.hoamz.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hoamz.hoamz.data.DAO.ReminderDao;
import com.hoamz.hoamz.data.model.Reminder;

import kotlin.jvm.Volatile;

@Database(entities = {Reminder.class},version = 1)
public abstract class ReminderDatabase extends RoomDatabase {
    @Volatile
    private static volatile ReminderDatabase INSTANCE;
    private static final String nameReminderDatabase = "reminder_database";

    public static ReminderDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (ReminderDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context,ReminderDatabase.class,nameReminderDatabase)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

   public abstract ReminderDao reminderDao();

}
