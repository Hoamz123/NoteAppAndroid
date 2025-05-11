package com.hoamz.hoamz.data.DAO;

import android.content.pm.LabeledIntent;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hoamz.hoamz.data.model.Label;

import java.util.List;

@Dao
public interface LabelDAO {
    //lay tat ca nhan
    @Query("select * from label")
    LiveData<List<Label>> getListLabels();
    //them nhan
    @Insert
    void insertNewLabel(Label label);
    @Delete
    void deleteLabel(Label label);
    @Update
    void updateLabel(Label label);

    @Query("select * from label where label = :nameLabel")
    LiveData<Label> getLabelByName(String nameLabel);
}
