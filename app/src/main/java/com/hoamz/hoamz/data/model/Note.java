package com.hoamz.hoamz.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private int isPin;//1 la gim : 0 la khong gim -> order by -> gim nen dau
    private boolean isFavorite;
    private long date;
    private long trigger;
    private long timeRepeat;
    private String label;
    private int colorBgID;//mac dinh ban dau la mau trang

    public Note(String title, String content,long date,long trigger,long timeRepeat, int isPin, boolean isFavorite,String label,int colorBgID) {
        this.title = title;
        this.content = content;
        this.isPin = isPin;
        this.isFavorite = isFavorite;
        this.date = date;
        this.label = label;
        this.colorBgID = colorBgID;
        this.trigger = trigger;
        this.timeRepeat = timeRepeat;
    }


    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        isPin = in.readInt();
        isFavorite = in.readByte() != 0;
        date = in.readLong();
        trigger = in.readLong();
        timeRepeat = in.readLong();
        label = in.readString();
        colorBgID = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public void setColorBgID(int colorBgID) {
        this.colorBgID = colorBgID;
    }

    public void setTimeAlarm(long timeAlarm) {
        this.trigger = timeAlarm;
    }

    public long getTimeAlarm() {
        return trigger;
    }

    public void setRepeat(long timeRepeat) {
        this.timeRepeat = timeRepeat;
    }

    public void setTrigger(long trigger) {
        this.trigger = trigger;
    }

    public long getTrigger() {
        return trigger;
    }

    public long getTimeRepeat() {
        return timeRepeat;
    }

    public int getColorBgID() {
        return colorBgID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public int isPin() {
        return isPin;
    }
    public void setPin(int pin) {
        isPin = pin;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(isPin);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeLong(date);
        dest.writeLong(trigger);
        dest.writeLong(timeRepeat);
        dest.writeString(label);
        dest.writeInt(colorBgID);
    }
}
