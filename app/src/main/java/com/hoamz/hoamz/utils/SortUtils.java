package com.hoamz.hoamz.utils;

import android.hardware.lights.LightsManager;

import androidx.lifecycle.LiveData;

import com.hoamz.hoamz.data.model.Note;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    private static List<Note> sortByNameA_Z(List<Note> listNote) {
        List<Note> list = new ArrayList<>(listNote);
        list.sort(Comparator.comparing(Note::getTitle));
        return list;
    }
    private static List<Note> sortByNameZ_A(List<Note> listNote) {
        List<Note> list = new ArrayList<>(listNote);
        list.sort((o1, o2) -> o2.getTitle().compareTo(o1.getTitle()));
        return list;
    }
    private static List<Note> sortByTimeCreateOldestFirst(List<Note> noteList){
        List<Note> list = new ArrayList<>(noteList);
        list.sort(Comparator.comparing(Note::getDate));
        return list;
    }
    private static List<Note> sortByTimeCreateNewestFirst(List<Note> noteList){
        List<Note> list = new ArrayList<>(noteList);
        list.sort((o1, o2) -> Long.compare(o2.getDate(), o1.getDate()));
        return list;
    }
    public static List<Note> sort(String sortBy, List<Note> listNote){
        switch (sortBy){
            case "A_Z":
                return  sortByNameA_Z(listNote);
            case "Z_A":
                return sortByNameZ_A(listNote);
            case "Oldest":
                return sortByTimeCreateOldestFirst(listNote);
            case "Newest":
                return sortByTimeCreateNewestFirst(listNote);
        }
        return listNote;
    }
}
