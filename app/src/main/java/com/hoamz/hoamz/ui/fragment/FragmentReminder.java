package com.hoamz.hoamz.ui.fragment;

import static androidx.core.content.res.ResourcesCompat.getColor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.hoamz.hoamz.ui.act.CreateNote;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import java.util.ArrayList;
import java.util.List;

public class FragmentReminder extends Fragment {

    private RecyclerView rcViewReminder;
    private ConstraintLayout fadReminder;
    private NoteAdapter noteAdapter;
    private ImageView ivEmptyListReminder;
    private ImageView ivExitReminder;
    private LiveData<List<Note>> listNoteCurrent;
    private NoteViewModel noteViewModel;

    public FragmentReminder() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_reminder, container, false);
        initView(view);
        loadDataToRecyclerView();
        onClickItems();
        return view;
    }

    private void onClickItems() {
        noteAdapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(),NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                ///none
            }
        });

        fadReminder.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), CreateNote.class);
            startActivity(intent);
        });

        ivExitReminder.setOnClickListener(v ->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void loadDataToRecyclerView() {
        if(listNoteCurrent != null){
            listNoteCurrent.removeObservers(this);
        }

        listNoteCurrent = Transformations.distinctUntilChanged(noteViewModel.getAllHaveReminder());//lay tat ca nhung ghi chu  co nhac nho
        listNoteCurrent.observe(getViewLifecycleOwner(), list -> {

            List<Note> noteList = new ArrayList<>();
            for(Note note : list){
                //sau 30 ngay ko hien thi nua
                if(System.currentTimeMillis() - note.getTrigger() <= Constants.time30Days || note.getTrigger() >= System.currentTimeMillis()){
                    noteList.add(note);
                }
            }
            showOrHideEmptyImage(noteList);
            noteAdapter.setNoteList(noteList);
        });
        //chi setAdapter khi null
        if(rcViewReminder.getAdapter() == null){
            rcViewReminder.setAdapter(noteAdapter);
        }
    }

    private void showOrHideEmptyImage(List<Note> list){
        boolean isEmpty = (list == null || list.isEmpty());
        ivEmptyListReminder.setVisibility((isEmpty) ? View.VISIBLE : View.INVISIBLE);
        if(isEmpty){
            ivEmptyListReminder.setImageResource(R.drawable.empty_bg);
        }
    }

    private void initView(View view) {
        rcViewReminder = view.findViewById(R.id.rcViewReminder);
        fadReminder = view.findViewById(R.id.fab_addReminder);
        ivEmptyListReminder = view.findViewById(R.id.iv_emptyReminder);
        noteAdapter = new NoteAdapter();
        rcViewReminder.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        ivExitReminder = view.findViewById(R.id.icExitReminder);
    }

}