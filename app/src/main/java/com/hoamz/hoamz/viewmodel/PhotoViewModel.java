package com.hoamz.hoamz.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.data.repository.NoteRepo;
import com.hoamz.hoamz.data.repository.PhotoRepo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PhotoViewModel extends AndroidViewModel {
    private final PhotoRepo photoRepo;
    private final MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();
    public PhotoViewModel(@NonNull Application application) {
        super(application);
        photoRepo = new PhotoRepo(application);
    }

    //get bitmap
    public MutableLiveData<Bitmap> getBitmapMutableLiveData(){
        return bitmapMutableLiveData;
    }

    //set bitmap
    public void setBitmap(Bitmap bitmap){
        bitmapMutableLiveData.setValue(bitmap);
    }

    //insert photo
    public void insertPhoto(Photo photo, Consumer<Long> consumer){
        photoRepo.insertPhoto(photo,consumer);
    }

    public void insertPhotos(List<Photo> photoList){
        photoRepo.insertPhotos(photoList);
    }

    //delete
    public void deletePhoto(Photo photo){
        photoRepo.deletePhoto(photo);
    }

    public void deletePhotoById(int photo){
        photoRepo.deletePhotoById(photo);
    }

    //get
    public LiveData<List<Photo>> getAllPhotosByIdNote(int idNote){
        return photoRepo.getAllPhotosByIdNote(idNote);
    }

    public void updatePhoto(Photo photo){
        photoRepo.updatePhoto(photo);
    }
}
