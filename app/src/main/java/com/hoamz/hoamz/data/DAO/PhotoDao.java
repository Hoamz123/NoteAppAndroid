package com.hoamz.hoamz.data.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hoamz.hoamz.data.model.Photo;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert
    long insertPhoto(Photo photo);
    @Insert
    void insertPhotos(List<Photo> photoList);
    @Delete
    void deletePhoto(Photo photo);

    @Update
    void updatePhoto(Photo photo);

    @Query("delete from photo where idPhoto =:id")
    void deletePhotoById(int id);
    @Query("select * from photo where idNote =:idNote")
    LiveData<List<Photo>> getAllPhotosByIdNote(int idNote);
}
