package com.hoamz.hoamz.data.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hoamz.hoamz.data.model.NoteDeleted;

import java.util.List;
@Dao
public interface NoteDeletedDAO {
    @Query("select * from notedeleted")
    LiveData<List<NoteDeleted>> getAllNoteDeleted();


    // TODO: 6/19/2025  
    @Insert
    void insertNoteDeleted(NoteDeleted noteDeleted);

    @Delete
    void deletedNoteAfter30Day(NoteDeleted noteDeleted);
    
}
