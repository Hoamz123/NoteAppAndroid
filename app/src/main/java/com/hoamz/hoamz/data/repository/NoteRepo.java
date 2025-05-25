package com.hoamz.hoamz.data.repository;


import android.app.Application;
import android.security.identity.EphemeralPublicKeyNotFoundException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hoamz.hoamz.data.DAO.NoteDao;
import com.hoamz.hoamz.data.DAO.NoteDatabase;
import com.hoamz.hoamz.data.model.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.hoamz.hoamz.utils.Constants;

public class NoteRepo {
    private final NoteDao noteDao;
    private final ExecutorService executorService;
    private LiveData<List<Note>> listLiveData = new MutableLiveData<>();
    private LiveData<List<Note>> listLiveDataBySearch = new MutableLiveData<>();

    public NoteRepo(Application application){
        noteDao = NoteDatabase.getInstance(application).noteDao();
        executorService = Executors.newSingleThreadExecutor();//bat dong bo
    }

    //lay tat ca du lieu tren bang
    public LiveData<List<Note>> getListNotes(){
        listLiveData = noteDao.getAllNotes();
        return listLiveData;
    }

    //them note
    public void insertNewNote(Note note){
        executorService.execute(() -> noteDao.insertNewNote(note));
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
        listLiveDataBySearch = noteDao.getListNotesByLabel(label);
        return listLiveDataBySearch;
    }

    //lay danh sach tim kiem
    public LiveData<List<Note>> getListNotesByQuerySearch(String query){
        listLiveDataBySearch = noteDao.getListNotesSearch(query);
        return listLiveDataBySearch;
    }

    //lay list loc theo time
    public LiveData<List<Note>> getListNotesByTime(String condition){
        if(condition.equals(Constants.sortOldToNew)){
            listLiveData = noteDao.getListNotesByDateASC();
        }
        else if(condition.equals(Constants.sortNewToOld)){
            listLiveData = noteDao.getListNotesByDateDESC();
        }
        return listLiveData;
    }
    public LiveData<List<Note>> getListNotesByAlphabet(String condition){
        if(condition.equals(Constants.sortAToZ)){
            listLiveData = noteDao.getListNotesOderByAz();
        }
        else if(condition.equals(Constants.sortZToA)){
            listLiveData = noteDao.getListNotesOderByZa();
        }
        return listLiveData;
    }

    public LiveData<List<Note>> getNotesByTime(long startOfDay,long endOfDay){
        return noteDao.getListNotesByTime(startOfDay,endOfDay);
    }

    public LiveData<List<Long>> getAllDate(){
        return noteDao.getAllDate();
    }

    public LiveData<Integer> getCountNotesByLabel(String label){
       if(Constants.labelAll.equals(label)){
           return noteDao.getCountAllNotes();
       }
       else{
           return noteDao.getCountNoteByLabel(label);
       }
    }
    public LiveData<List<Note>> getListNotesAlarm(){
        return noteDao.getListNotesAlarm();
    }

    public void deleteNotesByLabel(String label){
        executorService.execute(() ->{
            noteDao.deleteNotesByLabel(label);
        });
    }

    public LiveData<List<Note>> listNodeDeleted(boolean deleted){
        return noteDao.getListNoteDeleted(deleted);
    }
}
