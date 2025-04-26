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
import android.widget.Toast;

import com.hoamz.hoamz.R;

import java.util.Objects;


public class FragmentArchiver extends Fragment {
    private Context context;
    private ImageView icExitArchiver;
    private RecyclerView rcViewArchiver;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FragmentArchiver() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_archiver, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
    }

    private void onClick() {
        icExitArchiver.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initView(View view) {
        icExitArchiver = view.findViewById(R.id.icExitArchiver);
        rcViewArchiver = view.findViewById(R.id.rcViewArchiver);
    }
}