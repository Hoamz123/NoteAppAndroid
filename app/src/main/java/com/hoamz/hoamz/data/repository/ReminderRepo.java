package com.hoamz.hoamz.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hoamz.hoamz.data.DAO.ReminderDao;
import com.hoamz.hoamz.data.DAO.ReminderDatabase;
import com.hoamz.hoamz.data.model.Reminder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ReminderRepo {
    private final ReminderDao reminderDao;
    private final ExecutorService executorService;
    public ReminderRepo(Application application){
        reminderDao = ReminderDatabase.getInstance(application).reminderDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    //insert new reminder
    public void insertReminder(Reminder reminder, Consumer<Long> consumer){
        executorService.execute(() -> {
            long id = reminderDao.insertReminder(reminder);
            consumer.accept(id);
        });
    }
    //delete
    public void deleteReminder(Reminder reminder){
        executorService.execute(() ->{
            reminderDao.deleteReminder(reminder);
        });
    }

    //update
    public void updateReminder(Reminder reminder){
        executorService.execute(() ->{
            reminderDao.updateReminder(reminder);
        });
    }

    //get
    public LiveData<List<Reminder>> getAllRemindersByIdNote(int id){
        return reminderDao.getAllReminderByIdNote(id);
    }

    public LiveData<Integer> getCountReminder(int idNote){
        return reminderDao.getCountReminder(idNote);
    }

    public LiveData<List<Reminder>> getAllReminderUpcoming(long timeNow){
        return reminderDao.getAllReminderUpcoming(timeNow);
    }

    public LiveData<List<Reminder>> getAllReminderExpired(long timeNow){
        return reminderDao.getAllReminderExpired(timeNow);
    }
}
