package com.hoamz.hoamz.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int idReminder;
    private long trigger;
    private long timeRepeat;
    private int idNote;//mot note co the co toi da 5 reminder (-> dua vao idNote nay -> lay ra list<Reminder>

    public Reminder(int idNote,long trigger, long timeRepeat) {
        this.trigger = trigger;
        this.timeRepeat = timeRepeat;
        this.idNote = idNote;
    }

    public int getIdReminder() {
        return idReminder;
    }

    public void setIdReminder(int idReminder) {
        this.idReminder = idReminder;
    }

    public long getTrigger() {
        return trigger;
    }

    public void setTrigger(long trigger) {
        this.trigger = trigger;
    }

    public long getTimeRepeat() {
        return timeRepeat;
    }

    public void setTimeRepeat(long timeRepeat) {
        this.timeRepeat = timeRepeat;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }
}
