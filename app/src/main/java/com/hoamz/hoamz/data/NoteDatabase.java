package com.hoamz.hoamz.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hoamz.hoamz.data.DAO.LabelDAO;
import com.hoamz.hoamz.data.DAO.NoteDao;
import com.hoamz.hoamz.data.DAO.PhotoDao;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Photo;

import kotlin.jvm.Volatile;

@Database(entities = {Note.class, Label.class, Photo.class},version = 17)
public abstract class NoteDatabase extends RoomDatabase {
    @Volatile
    private static NoteDatabase instance;
    private static final String name_database = "notes_database";

    //singleton
    public static NoteDatabase getInstance(Context context) {
        if(instance == null){
            synchronized (RoomDatabase.class){
                instance = Room.databaseBuilder(context, NoteDatabase.class,name_database)
                        .build();
            }
        }
        return instance;
    }
    public abstract NoteDao noteDao();
    public abstract LabelDAO labelDAO();
    public abstract PhotoDao photoDao();
}
