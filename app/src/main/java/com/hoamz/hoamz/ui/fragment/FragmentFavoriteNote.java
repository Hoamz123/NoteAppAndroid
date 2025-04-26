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

public class FragmentFavoriteNote extends Fragment {
    private Context context;
    private ImageView icExitFavorite;
    private RecyclerView rcViewFavorite;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FragmentFavoriteNote() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_note, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
    }

    private void onClick(){
        icExitFavorite.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initView(View view) {
        icExitFavorite = view.findViewById(R.id.icExitFavorite);
        rcViewFavorite = view.findViewById(R.id.rcViewFavorite);
    }
}