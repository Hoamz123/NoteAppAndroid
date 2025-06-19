package com.hoamz.hoamz.ui.fragment;

import static androidx.core.content.res.ResourcesCompat.getColor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;


public class FragmentBin extends Fragment {
    private Context context;
    private ImageView icExitBin;
    private RecyclerView rcViewBin;
    private ImageView ivEmptyListOfBin;
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    LiveData<List<Note>> listNOteDeleted;

    public FragmentBin() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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
        onLoadData();
        onClick();
    }

    private void onLoadData() {
        if(listNOteDeleted != null){
            listNOteDeleted.removeObservers(this);
        }



        if(rcViewBin.getAdapter() == null){
            rcViewBin.setAdapter(adapter);
        }
    }

    private void onClick(){
        icExitBin.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        //su li su kien click item
        adapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                //xoa han or khoi phuc lai
                /*
                hien thi log -> 2 case :
                1:Xoa
                2:Khoi phuc lai
                 */
            }
            @Override
            public void onItemLongClick(Note note) {

            }
        });
    }
    private void initView(View view) {
        icExitBin = view.findViewById(R.id.icExitBin);
        rcViewBin = view.findViewById(R.id.rcViewBin);
        ivEmptyListOfBin = view.findViewById(R.id.iv_emptyBin);
        adapter = new NoteAdapter();
        rcViewBin.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    private void showOrHideEmptyImage(List<Note> list){
        boolean isEmpty = (list == null || list.isEmpty());
        ivEmptyListOfBin.setVisibility((isEmpty) ? View.VISIBLE : View.INVISIBLE);
        if(isEmpty){
            ivEmptyListOfBin.setImageResource(R.drawable.empty_bg);
        }
    }

}