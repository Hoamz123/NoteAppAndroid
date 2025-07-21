package com.hoamz.hoamz.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hoamz.hoamz.data.model.Note;
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
    public void deleteNote(Note note,Consumer<String> stateFlow){
        try{
            noteRepo.deleteNote(note);
            stateFlow.accept("Deleted success");
        }catch (Exception e){
            stateFlow.accept("Error " + e.getMessage());
        }
    }

    //xoa 1 list note
    public void deleteNotes(List<Note> listNote,Consumer<String> stateFlow){
        try{
            noteRepo.deleteNotes(listNote);
            stateFlow.accept("Delete success");
        }catch (Exception e){
            stateFlow.accept("Error " + e.getMessage());
        }
    }

    //update
    public void updateNote(Note note,Consumer<String> stateFlow){
        try{
            noteRepo.updateNote(note);
            stateFlow.accept("Update success");
        }catch (Exception e){
            stateFlow.accept("Error " + e.getMessage());
        }
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
    public LiveData<List<Note>> getListNotesByTime(String condition,String label){
        return noteRepo.getListNotesByTime(condition,label);
    }

    public LiveData<List<Note>> getListNotesByAlphabet(String condition,String label){
        return noteRepo.getListNotesByAlphabet(condition,label);
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

    public void deleteNotesByLabel(String label,Consumer<String> stateFlow){
        try{
            noteRepo.deleteNotesByLabel(label);
            stateFlow.accept("Delete success");
        }catch (Exception e){
            stateFlow.accept("Error " + e.getMessage());
        }
    }

    public LiveData<List<Note>> getListNoteFavorite(String condition){
        return noteRepo.getListNoteFavorite(true,condition);
    }

    //get note by id
    public LiveData<Note> getNoteById(int id){
        return noteRepo.getNoteById(id);
    }

    public LiveData<List<Note>> getNotesDeleted(String condition){
        return noteRepo.getNotesDeleted(condition);
    }

    public LiveData<List<Note>> getNotesArchived(String condition){
        return noteRepo.getNotesArchived(condition);
    }

    public void deleteNotesAfter30Days(){
        noteRepo.deleteNotesAfter30Days();
    }
}
