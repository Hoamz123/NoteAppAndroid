package com.hoamz.hoamz.data.model;

public class LabelDetail {
    private String labelName;
    private int numberNote;

    public LabelDetail(String labelName, int numberNote) {
        this.labelName = labelName;
        this.numberNote = numberNote;
    }

    public String getLabelName() {
        return labelName;
    }

    public int getNumberNote() {
        return numberNote;
    }
}
