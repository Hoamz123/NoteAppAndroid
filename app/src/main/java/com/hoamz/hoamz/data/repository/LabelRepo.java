package com.hoamz.hoamz.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.hoamz.hoamz.data.DAO.LabelDAO;
import com.hoamz.hoamz.data.DAO.NoteDatabase;
import com.hoamz.hoamz.data.model.Label;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabelRepo {
    private final LabelDAO labelDAO;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LabelRepo(Application application){
        labelDAO = NoteDatabase.getInstance(application).labelDAO();
    }

    //lauy tat ca label
    public LiveData<List<Label>> getListLabels(){
        return labelDAO.getListLabels();
    }

    //insert
    public void insertLabel(Label label){
        executorService.execute(() -> labelDAO.insertNewLabel(label));
    }

    //delete
    public void deleteLabel(Label label){
        executorService.execute(() -> labelDAO.deleteLabel(label));
    }

    public void updateLabel(Label label){
        executorService.execute(() -> labelDAO.updateLabel(label));
    }

    public LiveData<Label> getLabelByName(String nameLabel){
        return labelDAO.getLabelByName(nameLabel);
    }

}
