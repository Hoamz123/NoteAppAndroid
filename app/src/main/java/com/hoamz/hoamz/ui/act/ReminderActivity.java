package com.hoamz.hoamz.ui.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView rcViewReminder;
    private ConstraintLayout fadReminder;
    private NoteAdapter noteAdapter;
    private ImageView ivEmptyListReminder;
    private ImageView ivExitReminder;
    private LiveData<List<Note>> listNoteCurrent;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        loadDataToRecyclerView();
        onClickItems();
    }

    private void onClickItems() {
        noteAdapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(ReminderActivity.this,NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                ///none
            }
        });

        fadReminder.setOnClickListener(v ->{
            Intent intent = new Intent(ReminderActivity.this, CreateNote.class);
            startActivity(intent);
        });

        ivExitReminder.setOnClickListener(v ->{
            finish();
        });
    }

    private void loadDataToRecyclerView() {
        if(listNoteCurrent != null){
            listNoteCurrent.removeObservers(this);
        }

        listNoteCurrent = Transformations.distinctUntilChanged(noteViewModel.getAllHaveReminder());//lay tat ca nhung ghi chu  co nhac nho
        listNoteCurrent.observe(this, list -> {
            List<Note> noteList = new ArrayList<>(list);
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

    private void initView() {
        rcViewReminder = findViewById(R.id.rcViewReminder);
        fadReminder = findViewById(R.id.fab_addReminder);
        ivEmptyListReminder = findViewById(R.id.iv_emptyReminder);
        noteAdapter = new NoteAdapter();
        rcViewReminder.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        ivExitReminder = findViewById(R.id.icExitReminder);
    }
}