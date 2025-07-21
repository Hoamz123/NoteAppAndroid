package com.hoamz.hoamz.data.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hoamz.hoamz.data.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    //insert new reminder
    @Insert
    long insertReminder(Reminder reminder);

    //delete reminder
    @Delete
    void deleteReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    //get allReminder by idNote
    @Query("select * from reminder where idNote =:idNote ")
    LiveData<List<Reminder>> getAllReminderByIdNote(int idNote);

    @Query("select count(*) from reminder where idNote =:idNote")
    LiveData<Integer> getCountReminder(int idNote);

    @Query("select * from reminder where `trigger` > :timeNow ")
    LiveData<List<Reminder>> getAllReminderUpcoming(long timeNow);
    @Query("select * from reminder where `trigger` < :timeNow ")
    LiveData<List<Reminder>> getAllReminderExpired(long timeNow);

}
