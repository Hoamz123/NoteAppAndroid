package com.hoamz.hoamz.ui.fragment;

import androidx.fragment.app.Fragment;

//quan li back tren device cua user khi fragment dang hien thi
public class BaseFragment extends Fragment {
    public boolean onBackPressed(){
        return false;
    }
}
