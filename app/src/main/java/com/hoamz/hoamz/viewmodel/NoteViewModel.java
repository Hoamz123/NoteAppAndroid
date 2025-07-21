package com.hoamz.hoamz.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.NoteDeleted;
import com.hoamz.hoamz.data.repository.NoteRepo;
import com.hoamz.hoamz.utils.Constants;

import java.util.List;
import java.util.function.Consumer;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepo noteRepo;
    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepo = new NoteRepo(application);
    }

    //lay tat ca du lieu tren bang
    public LiveData<List<Note>> getListNotes(){
        return noteRepo.getListNotes();
    }

    //them note
    public void insertNewNote(Note note, Consumer<Long> callBack){
        noteRepo.insertNewNote(note,callBack);
    }

    //xoa 1 note
    public void deleteNote(Note note){
        noteRepo.deleteNote(note);
    }

    //xoa 1 list note
    public void deleteNotes(List<Note> listNote){
        noteRepo.deleteNotes(listNote);
    }

    //update
    public void updateNote(Note note){
        noteRepo.updateNote(note);
    }

    //lay tat ca theo nhan
    public LiveData<List<Note>> getListNotesByLabel(String label){
        if(label.equals(Constants.labelAll)){
            return getListNotes();
        }
        return noteRepo.getListNotesByLabel(label);
    }

    //lay danh scah theo tim kiem
    public LiveData<List<Note>> getListNotesByQuerySearch(String query){
        return noteRepo.getListNotesByQuerySearch(query);
    }

    //lay theo time
    public LiveData<List<Note>> getListNotesByTime(String condition){
        return noteRepo.getListNotesByTime(condition);
    }

    public LiveData<List<Note>> getListNotesByAlphabet(String condition){
        return noteRepo.getListNotesByAlphabet(condition);
    }

    public LiveData<List<Note>> getNotesByTime(long startOfDay,long endOfDay){
        return noteRepo.getNotesByTime(startOfDay, endOfDay);
    }

    public LiveData<List<Long>> getAllDate(){
        return noteRepo.getAllDate();
    }

    public LiveData<Integer> getCountNotes(String label){
        return noteRepo.getCountNotesByLabel(label);
    }

    public LiveData<List<Note>> getAllHaveReminder(){
        return noteRepo.getListNotesAlarm();
    }

    public void deleteNotesByLabel(String label){
        noteRepo.deleteNotesByLabel(label);
    }

    public LiveData<List<Note>> getListNoteFavorite(){
        return noteRepo.getListNoteFavorite(true);
    }

    //note deleted
    public LiveData<List<NoteDeleted>> getAllNoteDeleted(){
        return noteRepo.getAllNoteDeleted();
    }

    public void insertNoteDeleted(NoteDeleted noteDeleted){
        noteRepo.insertNoteDeleted(noteDeleted);
    }

    public void deletedNoteAfter30Day(NoteDeleted noteDeleted){
        noteRepo.deletedNoteAfter30Day(noteDeleted);
    }
}
