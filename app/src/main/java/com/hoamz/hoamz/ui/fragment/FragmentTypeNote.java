package com.hoamz.hoamz.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hoamz.hoamz.R;


public class FragmentTypeNote extends Fragment {

    private Context context;
    private ImageView icExitCategory;
    private RecyclerView rcViewCategory;
    private Button btnAddLabel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FragmentTypeNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_type_note, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
    }

    private void onClick(){
        icExitCategory.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initView(View view) {
        icExitCategory = view.findViewById(R.id.icExitTypeNote);
        rcViewCategory = view.findViewById(R.id.rcViewCategory);
        btnAddLabel = view.findViewById(R.id.btnAddLabel);
    }
}