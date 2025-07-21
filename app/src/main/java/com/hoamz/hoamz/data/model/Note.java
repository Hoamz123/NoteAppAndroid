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
    private boolean haveReminder;
    private String label;
    private boolean isDeleted;
    private boolean isArchived;
    private int colorBgID;//mac dinh ban dau la mau trang
    private long timeDeleteNote;

    public Note(String title, String content,long date,boolean haveReminder, int isPin, boolean isFavorite,boolean isDeleted,boolean isArchived,String label,int colorBgID,long timeDeleteNote) {
        this.title = title;
        this.content = content;
        this.isPin = isPin;
        this.isFavorite = isFavorite;
        this.date = date;
        this.label = label;
        this.colorBgID = colorBgID;
        this.haveReminder = haveReminder;
        this.isDeleted = isDeleted;
        this.isArchived = isArchived;
        this.timeDeleteNote = timeDeleteNote;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        isPin = in.readInt();
        isFavorite = in.readByte() != 0;
        date = in.readLong();
        haveReminder = in.readByte() != 0;
        label = in.readString();
        colorBgID = in.readInt();
        isDeleted = in.readByte() != 0;
        isArchived = in.readByte() != 0;
        timeDeleteNote = in.readLong();
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

    public void setTimeAlarm(boolean haveReminder) {
        this.haveReminder = haveReminder;
    }


    public boolean getTrigger() {
        return haveReminder;
    }

    public long getTimeDeleteNote() {
        return timeDeleteNote;
    }

    public void setTimeDeleteNote(long timeDeleteNote) {
        this.timeDeleteNote = timeDeleteNote;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isArchived() {
        return isArchived;
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
    public boolean isHaveReminder() {
        return haveReminder;
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
        dest.writeByte((byte) (haveReminder ? 1 : 0));
        dest.writeString(label);
        dest.writeInt(colorBgID);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeByte((byte) (isArchived ? 1 : 0));
        dest.writeLong(timeDeleteNote);
    }
}
