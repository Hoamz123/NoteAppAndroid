package com.hoamz.hoamz.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.data.repository.ReminderRepo;

import java.util.List;
import java.util.function.Consumer;

public class ReminderViewModel extends AndroidViewModel {
    private final ReminderRepo reminderRepo;
    public ReminderViewModel(@NonNull Application application) {
        super(application);
        reminderRepo = new ReminderRepo(application);
    }

    //insert
    public void insertReminder(Reminder reminder, Consumer<Long> consumer){
        reminderRepo.insertReminder(reminder,consumer);
    }
    //delete
    public void deleteReminder(Reminder reminder){
        reminderRepo.deleteReminder(reminder);
    }
    public void updateReminder(Reminder reminder){
        reminderRepo.updateReminder(reminder);
    }

    //get
    public LiveData<List<Reminder>> getAllRemindersByIdNote(int idNote){
        return reminderRepo.getAllRemindersByIdNote(idNote);
    }

    public LiveData<Integer> getCountReminder(int idNote){
        return reminderRepo.getCountReminder(idNote);
    }
}
