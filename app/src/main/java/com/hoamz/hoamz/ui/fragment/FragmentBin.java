package com.hoamz.hoamz.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hoamz.hoamz.R;


public class FragmentBin extends Fragment {
    private Context context;
    private ImageView icExitBin;
    private RecyclerView rcViewBin;


    public FragmentBin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bin, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
    }

    private void onClick(){
        icExitBin.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
    private void initView(View view) {
        icExitBin = view.findViewById(R.id.icExitBin);
        rcViewBin = view.findViewById(R.id.rcViewBin);
    }
}