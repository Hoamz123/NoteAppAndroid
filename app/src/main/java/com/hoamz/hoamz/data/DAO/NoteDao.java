package com.hoamz.hoamz.data.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hoamz.hoamz.data.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    //viet cac truy van
    //lay tat ca ban ghi
    @Query("select * from note order by isPin desc")
    LiveData<List<Note>> getAllNotes();
    //chen them 1 ban ghi
    @Insert
    void insertNewNote(Note newNote);
    //xoa mot ban ghi
    @Delete
    void deleteNote(Note note);
    //chinh sua ban ghi
    @Update
    void updateNote(Note note);
    //xoa mot list ban ghi
    @Delete
    void deleteNotes(List<Note> list);
    //truy van theo nhan
    @Query("select *  from note where label = :label order by isPin desc")
    LiveData<List<Note>> getListNotesByLabel(String label);
    //truy van tim kiem dua tren text tren thanh nhap
    @Query("select * from note where title like '%' || :query || '%' COLLATE NOCASE or content like '%' || :query || '%' COLLATE NOCASE")
    LiveData<List<Note>> getListNotesSearch(String query);
    //loc theo thoi gian
    @Query("select * from note order by date desc,isPin desc")
    LiveData<List<Note>> getListNotesByDateDESC();

    @Query("select * from note order by date asc,isPin desc")
    LiveData<List<Note>> getListNotesByDateASC();

    @Query("select * from note order by title COLLATE NOCASE asc")
    LiveData<List<Note>> getListNotesOderByAz();

    @Query("select * from note order by title COLLATE NOCASE desc")
    LiveData<List<Note>> getListNotesOderByZa();

    @Query("select * from note where date <= :endOfDay and date >= :startOfDay order by isPin desc")
    LiveData<List<Note>> getListNotesByTime(long startOfDay,long endOfDay);

    //lay tat ca cac ngay tao ghi chu
    @Query("select date from note")
    LiveData<List<Long>> getAllDate();//truy van tat ca cac thoi gian da tao ghi cu

    @Query("select count(*) from note where label = :label")
    LiveData<Integer> getCountNoteByLabel(String label);

    @Query("select count(*) from note")
    LiveData<Integer> getCountAllNotes();

    //lay cac note co nhac nho
    @Query("select * from note where `trigger` > 0 order by isPin desc")
    LiveData<List<Note>> getListNotesAlarm();

    @Query("delete from note where label = :label")
    void deleteNotesByLabel(String label);

    @Query("select * from note where isFavorite =:isFavorite order by isPin desc")
    LiveData<List<Note>> getListNoteFavorite(boolean isFavorite);
}
