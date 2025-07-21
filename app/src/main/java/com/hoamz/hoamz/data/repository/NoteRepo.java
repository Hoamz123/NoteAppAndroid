package com.hoamz.hoamz.data.repository;


import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hoamz.hoamz.data.DAO.NoteDao;
import com.hoamz.hoamz.data.DAO.NoteDatabase;
import com.hoamz.hoamz.data.model.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.zip.CheckedInputStream;

import com.hoamz.hoamz.utils.Constants;

public class NoteRepo {
    private final NoteDao noteDao;
    private final ExecutorService executorService;
    private LiveData<List<Note>> listLiveData = new MutableLiveData<>();
    private LiveData<List<Note>> listLiveDataBySearch = new MutableLiveData<>();

    public NoteRepo(Context context){
        noteDao = NoteDatabase.getInstance(context.getApplicationContext()).noteDao();
        executorService = Executors.newSingleThreadExecutor();//bat dong bo
    }

    //lay tat ca du lieu tren bang
    public LiveData<List<Note>> getListNotes(){
        listLiveData = noteDao.getAllNotes(false,false);
        return listLiveData;
    }

    //them note
    public void insertNewNote(Note note, Consumer<Long> callBack){
        executorService.execute(() -> {
            long id = noteDao.insertNewNote(note);
            callBack.accept(id);
        });
    }

    //xoa 1 note
    public void deleteNote(Note note){
        executorService.execute(() -> noteDao.deleteNote(note));
    }

    //xoa 1 list notes
    public void deleteNotes(List<Note> noteList){
        executorService.execute(() -> noteDao.deleteNotes(noteList));
    }

    //update
    public void updateNote(Note note){
        executorService.execute(() -> noteDao.updateNote(note));
    }

    //lay all theo nhan(label)
    public LiveData<List<Note>> getListNotesByLabel(String label){
        listLiveDataBySearch = noteDao.getListNotesByLabel(label,false,false);
        return listLiveDataBySearch;
    }

    //lay danh sach tim kiem
    public LiveData<List<Note>> getListNotesByQuerySearch(String query){
        listLiveDataBySearch = noteDao.getListNotesSearch(query,false,false);
        return listLiveDataBySearch;
    }

    //lay list loc theo time
    public LiveData<List<Note>> getListNotesByTime(String condition,String label){
        if(label.equals(Constants.labelAll)){
            if(condition.equals(Constants.sortOldToNew)){
                listLiveData = noteDao.getListNotesByDateASC(false,false);
            }
            else if(condition.equals(Constants.sortNewToOld)){
                listLiveData = noteDao.getListNotesByDateDESC(false,false);
            }
        }
        else{
            if(condition.equals(Constants.sortOldToNew)){
                listLiveData = noteDao.getListNotesByDateASC(false,false,label);
            }
            else if(condition.equals(Constants.sortNewToOld)){
                listLiveData = noteDao.getListNotesByDateDESC(false,false,label);
            }
        }
        return listLiveData;
    }
    public LiveData<List<Note>> getListNotesByAlphabet(String condition,String label){
        if(label.equals(Constants.labelAll)){
            if(condition.equals(Constants.sortAToZ)){
                listLiveData = noteDao.getListNotesOderByAz(false,false);
            }
            else if(condition.equals(Constants.sortZToA)){
                listLiveData = noteDao.getListNotesOderByZa(false,false);
            }
        }
        else{
            if(condition.equals(Constants.sortAToZ)){
                listLiveData = noteDao.getListNotesOderByAz(false,false,label);
            }
            else if(condition.equals(Constants.sortZToA)){
                listLiveData = noteDao.getListNotesOderByZa(false,false,label);
            }
        }
        return listLiveData;
    }

    public LiveData<List<Note>> getNotesByTime(long startOfDay,long endOfDay){
        return noteDao.getListNotesByTime(startOfDay,endOfDay,false);
    }

    public LiveData<List<Long>> getAllDate(){
        return noteDao.getAllDate(false);
    }

    public LiveData<Integer> getCountNotesByLabel(String label){
       if(Constants.labelAll.equals(label)){
           return noteDao.getCountAllNotes(false,false);
       }
       else{
           return noteDao.getCountNoteByLabel(label,false,false);
       }
    }
    public LiveData<List<Note>> getListNotesAlarm(){
        return noteDao.getListNotesAlarm(true,false);
    }

    public void deleteNotesByLabel(String label){
        executorService.execute(() ->{
            noteDao.deleteNotesByLabel(label);
        });
    }

    public LiveData<List<Note>> getListNoteFavorite(boolean isFavorite,String condition){
        switch (condition) {
            case Constants.sortOldToNew:
                return noteDao.getListNoteFavoriteSortByDateASC(isFavorite, false, false);
            case Constants.sortNewToOld:
                return noteDao.getListNoteFavoriteSortByDateDESC(isFavorite, false, false);
            case Constants.sortAToZ:
                return noteDao.getListNoteFavoriteSortByAZ(isFavorite, false, false);
        }
        return noteDao.getListNoteFavoriteSortByZA(isFavorite,false,false);
    }

    //get note by id
    public LiveData<Note> getNoteById(int id){
        return noteDao.getNoteById(id,false);
    }

    public LiveData<List<Note>> getNotesDeleted(String condition){
        switch (condition) {
            case Constants.sortOldToNew:
                return noteDao.getListNoteDeletedSortByDateASC(true);
            case Constants.sortNewToOld:
                return noteDao.getListNoteDeletedSortByDateDESC( true);
            case Constants.sortAToZ:
                return noteDao.getListNoteDeletedSortByAZ(  true);
        }
        return noteDao.getListNoteDeletedSortByZA(true);
    }

    public LiveData<List<Note>> getNotesArchived(String condition){
        switch (condition) {
            case Constants.sortOldToNew:
                return noteDao.getListNoteArchiveSortByDateASC(false, true);
            case Constants.sortNewToOld:
                return noteDao.getListNoteArchiveSortByDateDESC(false, true);
            case Constants.sortAToZ:
                return noteDao.getListNoteArchiveSortByAZ( false, true);
        }
        return noteDao.getListNoteArchiveSortByZA(false,true);
    }
    public void deleteNotesAfter30Days(){
        executorService.execute(() ->{
            noteDao.deleteNotesAfter30Days(true,System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L);
        });
    }
}

