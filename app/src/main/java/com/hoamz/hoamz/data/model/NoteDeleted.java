package com.hoamz.hoamz.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteDeleted implements Parcelable {
    /*
    //thong tin can luu cua mot note da bi xoa ->
    title + content + label + color + thoi diem xoa(phuc vu cho work manager) (cac thong tin con lai reset ve thoi diem khoi phuc)
     */
    @PrimaryKey(autoGenerate = true)
    private int idNote;
    private String title;
    private String content;
    private String label;
    private int colorBgID;
    private long timeDeleted;

    public NoteDeleted(String title, String content, String label, int colorBgID, long timeDeleted) {
        this.title = title;
        this.content = content;
        this.label = label;
        this.colorBgID = colorBgID;
        this.timeDeleted = timeDeleted;
    }

    protected NoteDeleted(Parcel in) {
        idNote = in.readInt();
        title = in.readString();
        content = in.readString();
        label = in.readString();
        colorBgID = in.readInt();
        timeDeleted = in.readLong();
    }

    public static final Creator<NoteDeleted> CREATOR = new Creator<NoteDeleted>() {
        @Override
        public NoteDeleted createFromParcel(Parcel in) {
            return new NoteDeleted(in);
        }

        @Override
        public NoteDeleted[] newArray(int size) {
            return new NoteDeleted[size];
        }
    };

    public String getTitle() {
        return title;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getColorBgID() {
        return colorBgID;
    }

    public void setColorBgID(int colorBgID) {
        this.colorBgID = colorBgID;
    }

    public int getIdNote() {
        return idNote;
    }

    public long getTimeDeleted() {
        return timeDeleted;
    }

    public void setTimeDeleted(long timeDeleted) {
        this.timeDeleted = timeDeleted;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idNote);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(label);
        dest.writeInt(colorBgID);
        dest.writeLong(timeDeleted);
    }
}
