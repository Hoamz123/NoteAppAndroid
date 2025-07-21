package com.hoamz.hoamz.data.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Photo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int idPhoto;
    private String uri;
    private int idNote;

    public Photo(String uri,int idNote){
        this.uri = uri;
        this.idNote = idNote;
    }

    protected Photo(Parcel in) {
        idPhoto = in.readInt();
        uri = in.readString();
        idNote = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };


    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public int getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(int idPhoto) {
        this.idPhoto = idPhoto;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idPhoto);
        dest.writeString(uri);
        dest.writeInt(idNote);
    }
}
