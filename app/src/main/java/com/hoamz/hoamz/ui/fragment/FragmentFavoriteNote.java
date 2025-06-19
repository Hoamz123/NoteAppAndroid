package com.hoamz.hoamz.ui.fragment;

import static androidx.core.content.res.ResourcesCompat.getColor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavoriteNote extends Fragment {
    private ImageView icExitFavorite;
    private RecyclerView rcViewFavorite;
    private LiveData<List<Note>> listNoteFavorite;
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    private ImageView ivEmptyListFavorite;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public FragmentFavoriteNote() {
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
        View view = inflater.inflate(R.layout.fragment_favorite_note, container, false);
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
        if(listNoteFavorite != null) listNoteFavorite.removeObservers(getViewLifecycleOwner());
        listNoteFavorite = Transformations.distinctUntilChanged(viewModel.getListNoteFavorite());
        listNoteFavorite.observe(getViewLifecycleOwner(), list -> {
            List<Note> listNote = new ArrayList<>(list);
            showOrHideEmptyImage(listNote);
            adapter.setNoteList(listNote);
        });
        if(rcViewFavorite.getAdapter() == null){
            rcViewFavorite.setAdapter(adapter);
        }
    }

    private void showOrHideEmptyImage(List<Note> list){
        boolean isEmpty = (list == null || list.isEmpty());
        ivEmptyListFavorite.setVisibility((isEmpty) ? View.VISIBLE : View.INVISIBLE);
        if(isEmpty){
            ivEmptyListFavorite.setImageResource(R.drawable.empty_bg);
        }
    }

    private void onClick(){
        icExitFavorite.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();//back ve main
        });

        adapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(), NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                //long times
            }
        });
    }

    private void initView(View view) {
        icExitFavorite = view.findViewById(R.id.icExitFavorite);
        rcViewFavorite = view.findViewById(R.id.rcViewFavorite);
        adapter = new NoteAdapter();
        rcViewFavorite.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        ivEmptyListFavorite = view.findViewById(R.id.iv_emptyFavorite);
    }
}