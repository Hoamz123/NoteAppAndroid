package com.hoamz.hoamz.ui.fragment;

import android.app.PendingIntent;
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.FragmentReminderBinding;
import com.hoamz.hoamz.ui.act.CreateNote;
import com.hoamz.hoamz.ui.act.MainActivity;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FragmentReminder extends Fragment {
    private NoteAdapter noteAdapterUpcoming;
    private NoteAdapter noteAdapterExpired;
    private LiveData<List<Note>> listNoteCurrent;
    private FragmentReminderBinding binding;
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
//            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReminderBinding.inflate(inflater,container,false);
        initView();
        loadDataToRecyclerView();
        onClickItems();
        return binding.getRoot();
    }

    private void onClickItems() {
        noteAdapterUpcoming.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(),NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                ///none
                DialogUtils.ActionOnLongClickNote(getActivity(),Constants.DELETE, isAccept ->{
                    if(isAccept){
                        note.setDeleted(true);
                        note.setTimeAlarm(false);
                        noteViewModel.updateNote(note,state ->{});
                        deleteAllReminder(note);
                    }
                });
            }
        });

        noteAdapterExpired.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(),NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                ///none
                DialogUtils.ActionOnLongClickNote(getActivity(),Constants.DELETE, isAccept ->{
                    if(isAccept){
                        note.setDeleted(true);
                        note.setTimeAlarm(false);
                        noteViewModel.updateNote(note,state ->{});
                        deleteAllReminder(note);
                    }
                });
            }
        });

        binding.fabAddReminder.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), CreateNote.class);
            startActivity(intent);
        });

        binding.icExitReminder.setOnClickListener(v ->{
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadDataToRecyclerView() {
        if (listNoteCurrent != null) {
            listNoteCurrent.removeObservers(this);
        }

        listNoteCurrent = Transformations.distinctUntilChanged(noteViewModel.getAllHaveReminder());

        listNoteCurrent.observe(getViewLifecycleOwner(), noteList -> {
            show(noteList);
            if (noteList == null || noteList.isEmpty()) return;

            List<Note> listReminderUpcoming = new ArrayList<>();
            List<Note> listReminderExpired = new ArrayList<>();
            AtomicInteger processedCount = new AtomicInteger(0);

            for (Note note : noteList) {
                LiveData<List<Reminder>> reminderLiveData = getReminderViewModel().getAllRemindersByIdNote(note.getId());
                reminderLiveData.observe(getViewLifecycleOwner(), reminderList -> {
                    boolean isUpcoming = false;
                    if (reminderList != null && !reminderList.isEmpty()) {
                        for (Reminder reminder : reminderList) {
                            if (reminder.getTrigger() > System.currentTimeMillis() || reminder.getTimeRepeat() != 0) {
                                isUpcoming = true;
                                break;
                            }
                        }
                    }
                    if (isUpcoming) {
                        listReminderUpcoming.add(note);
                    } else {
                        listReminderExpired.add(note);
                    }

                    reminderLiveData.removeObservers(getViewLifecycleOwner());

                    if (processedCount.incrementAndGet() == noteList.size()) {
                        noteAdapterUpcoming.setNoteList(listReminderUpcoming);
                        noteAdapterExpired.setNoteList(listReminderExpired);

                        binding.tvUpcoming.setVisibility(listReminderUpcoming.isEmpty() ? View.GONE : View.VISIBLE);
                        binding.tvExpired.setVisibility(listReminderExpired.isEmpty() ? View.GONE : View.VISIBLE);

                        binding.rcViewReminderUpComing.setVisibility(listReminderUpcoming.isEmpty() ? View.GONE : View.VISIBLE);
                        binding.rcViewReminderExpired.setVisibility(listReminderExpired.isEmpty() ? View.GONE : View.VISIBLE);

                        if (binding.rcViewReminderUpComing.getAdapter() == null) {
                            binding.rcViewReminderUpComing.setAdapter(noteAdapterUpcoming);
                        }
                        if (binding.rcViewReminderExpired.getAdapter() == null) {
                            binding.rcViewReminderExpired.setAdapter(noteAdapterExpired);
                        }
                    }
                });
            }
        });
    }

    private void show(List<Note> list){
        if(list == null || list.isEmpty()){
            showWhenEmpty();
        }else{
            showWhenNotEmpty();
        }
    }

    private void initView() {
        noteAdapterUpcoming = new NoteAdapter();
        noteAdapterExpired = new NoteAdapter();
        binding.rcViewReminderUpComing.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rcViewReminderExpired.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    private void showWhenEmpty(){
        binding.tvEmptyReminder.setVisibility(View.VISIBLE);
        binding.ivEmptyReminder.setVisibility(View.VISIBLE);
        binding.tvEmptyReminder.setHint(Constants.EMPTY_REMINDER);
        binding.tvEmptyReminder.setGravity(Gravity.CENTER);
    }

    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private void showWhenNotEmpty(){
        binding.tvEmptyReminder.setVisibility(View.INVISIBLE);
        binding.ivEmptyReminder.setVisibility(View.INVISIBLE);
    }

    private void deleteAllReminder(Note note){
        LiveData<List<Reminder>> reminderLiveData = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(note.getId()));
        reminderLiveData.observe(getViewLifecycleOwner(), reminderList -> {
            if(reminderList != null){
                for(Reminder reminder : reminderList){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            requireContext(),
                            reminder.getIdReminder(),
                            new Intent(requireActivity(), MyBroadCastReminder.class),
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    getReminderViewModel().deleteReminder(reminder);
                    AlarmUtils.getInstance().setCancelAlarm(requireContext(), pendingIntent);
                }
                reminderLiveData.removeObservers(getViewLifecycleOwner());
            }
        });
    }

}