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
    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc")
    LiveData<List<Note>> getAllNotes(boolean isDeleted,boolean isArchived);
    //chen them 1 ban ghi
    @Insert
    long insertNewNote(Note newNote);
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
    @Query("select *  from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc")
    LiveData<List<Note>> getListNotesByLabel(String label,boolean isDeleted,boolean isArchived);
    //truy van tim kiem dua tren text tren thanh nhap
    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived and title like '%' || :query || '%' COLLATE NOCASE or content like '%' || :query || '%' COLLATE NOCASE")
    LiveData<List<Note>> getListNotesSearch(String query,boolean isDeleted,boolean isArchived);
    //loc theo thoi gian
    @Query("select * from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived order by date desc,isPin desc")
    LiveData<List<Note>> getListNotesByDateDESC(boolean isDeleted,boolean isArchived,String label);

    @Query("select * from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived order by date asc,isPin desc")
    LiveData<List<Note>> getListNotesByDateASC(boolean isDeleted,boolean isArchived,String label);

    @Query("select * from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived order by title COLLATE NOCASE asc,content COLLATE NOCASE asc")
    LiveData<List<Note>> getListNotesOderByAz(boolean isDeleted,boolean isArchived,String label);

    @Query("select * from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived order by title COLLATE NOCASE desc,content COLLATE NOCASE desc")
    LiveData<List<Note>> getListNotesOderByZa(boolean isDeleted,boolean isArchived,String label);

    @Query("select * from note where isDeleted = :isDeleted and date <= :endOfDay and date >= :startOfDay order by isPin desc")
    LiveData<List<Note>> getListNotesByTime(long startOfDay,long endOfDay,boolean isDeleted);

    //lay tat ca cac ngay tao ghi chu
    @Query("select date from note where isDeleted = :isDeleted group by date order by date desc")
    LiveData<List<Long>> getAllDate(boolean isDeleted);//truy van tat ca cac thoi gian da tao ghi cu

    @Query("select count(*) from note where label = :label and isDeleted = :isDeleted and isArchived = :isArchived")
    LiveData<Integer> getCountNoteByLabel(String label,boolean isDeleted,boolean isArchived);

    @Query("select count(*) from note where isDeleted = :isDeleted and isArchived = :isArchived")
    LiveData<Integer> getCountAllNotes(boolean isDeleted,boolean isArchived);

    //lay cac note co nhac nho
    @Query("select * from note where `haveReminder` = :haveReminder and isDeleted =:isDeleted order by isPin desc")
    LiveData<List<Note>> getListNotesAlarm(boolean haveReminder,boolean isDeleted);
    @Query("delete from note where label = :label")
    void deleteNotesByLabel(String label);

    @Query("select * from note where id = :id and isDeleted = :idDeleted limit 1")
    LiveData<Note> getNoteById(int id,boolean idDeleted);

    @Query("select * from note where isDeleted = :isDeleted order by isPin desc")
    LiveData<List<Note>> getNotesDeleted(boolean isDeleted);

    //xoa sau 30 ngay
    @Query("delete from note where isDeleted = :isDeleted and timeDeleteNote < :time")
    void deleteNotesAfter30Days(boolean isDeleted,long time);

    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by date desc,isPin desc")
    LiveData<List<Note>> getListNotesByDateDESC(boolean isDeleted,boolean isArchived);

    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by date asc,isPin desc")
    LiveData<List<Note>> getListNotesByDateASC(boolean isDeleted,boolean isArchived);

    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by title COLLATE NOCASE asc,content COLLATE NOCASE asc")
    LiveData<List<Note>> getListNotesOderByAz(boolean isDeleted,boolean isArchived);

    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by title COLLATE NOCASE desc,content COLLATE NOCASE desc")
    LiveData<List<Note>> getListNotesOderByZa(boolean isDeleted,boolean isArchived);

    @Query("select * from note where isFavorite =:isFavorite and isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,title COLLATE NOCASE asc,content COLLATE NOCASE asc")
    LiveData<List<Note>> getListNoteFavoriteSortByAZ(boolean isFavorite,boolean isDeleted,boolean isArchived);
    @Query("select * from note where isFavorite =:isFavorite and isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,title COLLATE NOCASE desc,content COLLATE NOCASE desc")
    LiveData<List<Note>> getListNoteFavoriteSortByZA(boolean isFavorite,boolean isDeleted,boolean isArchived);
    @Query("select * from note where isFavorite =:isFavorite and isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,date asc")
    LiveData<List<Note>> getListNoteFavoriteSortByDateASC(boolean isFavorite,boolean isDeleted,boolean isArchived);
    @Query("select * from note where isFavorite =:isFavorite and isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,date desc")
    LiveData<List<Note>> getListNoteFavoriteSortByDateDESC(boolean isFavorite,boolean isDeleted,boolean isArchived);

    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,title COLLATE NOCASE asc,content COLLATE NOCASE asc")
    LiveData<List<Note>> getListNoteArchiveSortByAZ(boolean isDeleted,boolean isArchived);
    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,title COLLATE NOCASE desc,content COLLATE NOCASE desc")
    LiveData<List<Note>> getListNoteArchiveSortByZA(boolean isDeleted,boolean isArchived);
    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,date asc")
    LiveData<List<Note>> getListNoteArchiveSortByDateASC(boolean isDeleted,boolean isArchived);
    @Query("select * from note where isDeleted = :isDeleted and isArchived = :isArchived order by isPin desc,date desc")
    LiveData<List<Note>> getListNoteArchiveSortByDateDESC(boolean isDeleted,boolean isArchived);

    @Query("select * from note where isDeleted = :isDeleted order by isPin desc,title COLLATE NOCASE asc,content COLLATE NOCASE asc")
    LiveData<List<Note>> getListNoteDeletedSortByAZ(boolean isDeleted);
    @Query("select * from note where isDeleted = :isDeleted order by isPin desc,title COLLATE NOCASE desc,content COLLATE NOCASE desc")
    LiveData<List<Note>> getListNoteDeletedSortByZA(boolean isDeleted);
    @Query("select * from note where isDeleted = :isDeleted order by isPin desc,date asc")
    LiveData<List<Note>> getListNoteDeletedSortByDateASC(boolean isDeleted);
    @Query("select * from note where isDeleted = :isDeleted order by isPin desc,date desc")
    LiveData<List<Note>> getListNoteDeletedSortByDateDESC(boolean isDeleted);
}
