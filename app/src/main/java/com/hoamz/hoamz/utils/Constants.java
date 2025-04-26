package com.hoamz.hoamz.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.hoamz.hoamz.R;

import java.util.HashSet;
import java.util.Set;

public class Constants {
    //luu cac hang ko doi
    public static final String sortAToZ = "a_z";
    public static final String sortZToA = "z_a";
    public static final String sortOldToNew = "asc";
    public static final String sortNewToOld = "desc";
    public static final String labelAll = "All";
    public static final String READING_MODE = "ReadingMode";
    public static final String EDIT_MODE = "EditMode";
    public static final String PIN = "Pin";
    public static final String UNPIN = "Unpin";
    public static final String UN_FAVORITE = "UnFavorite";
    public static final String FAVORITE = "Favorite";
    public static final String KEY_NOTE = "note";
    public static final String DATE_SELECTED = "dateSelected";
     public static final String LABEL_CURRENT = "labelCurrent";

    public static Set<Integer> colorLightPicker = new HashSet<>();
    public static Set<Integer> colorDarkPicker = new HashSet<>();

    //set mau sang -> chu den
    public static void init(Context context){
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color1));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color2));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color3));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color4));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color5));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color7));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color9));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color10));
        colorLightPicker.add(ContextCompat.getColor(context, R.color.color11));

// dark -> chu trang
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color12));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color6));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color15));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color13));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color16));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color14));
        colorDarkPicker.add(ContextCompat.getColor(context, R.color.color8));
    }
}
