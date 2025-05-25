package com.hoamz.hoamz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TypeModel extends ViewModel {
    private final MutableLiveData<String> type = new MutableLiveData<>();

    private final MutableLiveData<Integer> index = new MutableLiveData<>();

    public void setIndex(int index) {
        this.index.setValue(index);
    }

    public MutableLiveData<Integer> getIndex() {
        return index;
    }

    public MutableLiveData<String> getType() {
        return type;
    }
    public void setType(String type) {
        this.type.setValue(type);
    }
}
