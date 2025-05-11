package com.hoamz.hoamz.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.repository.LabelRepo;

import java.util.List;
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
    public void insertLabel(Label label){
        labelRepo.insertLabel(label);
    }

    //delete
    public void deleteLabel(Label label){
        labelRepo.deleteLabel(label);
    }

    //update
    public void updateLabel(Label label){
        labelRepo.updateLabel(label);
    }

    public LiveData<Label> getLabelByName(String nameLabel){
        return labelRepo.getLabelByName(nameLabel);
    }

}
