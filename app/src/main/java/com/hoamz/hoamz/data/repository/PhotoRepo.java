package com.hoamz.hoamz.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hoamz.hoamz.data.DAO.NoteDatabase;
import com.hoamz.hoamz.data.DAO.PhotoDao;
import com.hoamz.hoamz.data.model.Photo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PhotoRepo {
    private final PhotoDao photoDao;
    private final ExecutorService executorService;
    public PhotoRepo(Application application){
        photoDao = NoteDatabase.getInstance(application).photoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    //insert photo
    public void insertPhoto(Photo photo, Consumer<Long> consumer){
        executorService.execute(() -> {
            long id = photoDao.insertPhoto(photo);
            consumer.accept(id);
        });
    }

    public void insertPhotos(List<Photo> photoList){
        executorService.execute(() -> {
            photoDao.insertPhotos(photoList);
        });
    }

    //deletePhoto
    public void deletePhoto(Photo photo){
        executorService.execute(() -> photoDao.deletePhoto(photo));
    }

    public void updatePhoto(Photo photo){
        executorService.execute(() -> photoDao.updatePhoto(photo));
    }
    //get All Photos
    public LiveData<List<Photo>> getAllPhotosByIdNote(int idNote){
        return photoDao.getAllPhotosByIdNote(idNote);
    }
    //deletePhoto
    public void deletePhotoById(int id){
        executorService.execute(() -> photoDao.deletePhotoById(id));
    }
}
