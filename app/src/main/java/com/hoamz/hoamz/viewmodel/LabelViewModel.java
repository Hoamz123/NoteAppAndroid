package com.hoamz.hoamz.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.repository.LabelRepo;

import java.util.List;
import java.util.function.Consumer;

public class LabelViewModel extends AndroidViewModel {
    private final LabelRepo labelRepo;
    public LabelViewModel(@NonNull Application application) {
        super(application);
        labelRepo = new LabelRepo(application);
    }

    //lay tat ca label
    public LiveData<List<Label>> getListLabels(){
        return labelRepo.getListLabels();
    }

    //insert label
    public void insertLabel(Label label, Consumer<String> consumer){
        try{
            labelRepo.insertLabel(label);
        }catch (Exception e){
            consumer.accept("Error " + e.getMessage());
        }
    }

    //delete
    public void deleteLabel(Label label,Consumer<String> consumer){
        try{
            labelRepo.deleteLabel(label);
            consumer.accept("Delete success");
        }catch (Exception e){
            consumer.accept("Error " + e.getMessage());
        }
    }

    //update
    public void updateLabel(Label label,Consumer<String> consumer){
        try{
            labelRepo.updateLabel(label);
            consumer.accept("Update success");
        }catch (Exception e){
            consumer.accept("Error " + e.getMessage());
        }
    }

    public LiveData<Label> getLabelByName(String nameLabel){
        return labelRepo.getLabelByName(nameLabel);
    }

}
