package com.hoamz.hoamz.data.DAO;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.Note;

@Database(entities = {Note.class, Label.class},version = 3)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;
    private static final String name_database = "notes_database";

    //singleton
    public static NoteDatabase getInstance(Context context) {
        if(instance == null){
            synchronized (RoomDatabase.class){
                instance = Room.databaseBuilder(context, NoteDatabase.class,name_database)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }
    public abstract NoteDao noteDao();
    public abstract LabelDAO labelDAO();
}
