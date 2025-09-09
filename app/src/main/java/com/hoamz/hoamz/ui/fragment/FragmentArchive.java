package com.hoamz.hoamz.ui.fragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.TextView;
import android.widget.Toast;

import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.FragmentArchiveBinding;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.utils.SortUtils;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class FragmentArchive extends BaseFragment {
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    LiveData<List<Note>> listNoteArchive;
    private FragmentArchiveBinding binding;
    private boolean isMultiSelect = false;
    private boolean isSelectAllNote = false;
    private String sortCondition = Constants.sortNewToOld;
    private final AtomicReference<Set<Note>> listNoteSelectMulti = new AtomicReference<>(new HashSet<>());//luu lai danh sach cac note da chon
    private boolean isShow = false;
    private boolean isGrid = true;
    public FragmentArchive() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentArchiveBinding.inflate(inflater,container,false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onLoadData();
        onClick();
        onClickMenu();
    }

    private void onLoadData() {
        if(listNoteArchive != null){
            listNoteArchive.removeObservers(this);
        }
        listNoteArchive = Transformations.distinctUntilChanged(viewModel.getNotesArchived(sortCondition));
        listNoteArchive.observe(getViewLifecycleOwner(), notesArchived -> {
            show(notesArchived);
            if(notesArchived != null){
                adapter.setNoteList(notesArchived);
            }
        });

        if(binding.rcViewArchive.getAdapter() == null){
            binding.rcViewArchive.setAdapter(adapter);
        }
    }

    private void onClick(){
        binding.icExitArchive.setOnClickListener(v ->{
            if(isAdded()){
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        adapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getContext(), NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                DialogUtils.ActionClickLongItem(getContext(),Constants.ARCHIVE,action ->{
                    if(action.equals("delete")){
                        //xoa
                        note.setDeleted(true);
                        note.setArchived(false);
                        viewModel.updateNote(note,state ->{});
                        deleteAllReminder(note);
                    }
                    else {
                        //restore here
                        note.setArchived(false);
                        viewModel.updateNote(note,state ->{});
                    }
                });
            }
        });
        binding.icMoreInArchive.setOnClickListener(v ->{
            binding.constraintIncludeInArchive.setVisibility(View.VISIBLE);//hien thi
            isShow = true;
        });

        //an ra ngoai thi an di
        binding.constraintIncludeInArchive.setOnClickListener(v ->{
            if(isShow){
                isShow = false;
                binding.constraintIncludeInArchive.setVisibility(View.INVISIBLE);
            }
        });
        adapter.setOnMultiSelectItem(listMultiSelected -> {
            String title = listMultiSelected.size() + " Selected";
            binding.toolbarSelMulInArchive.titleMulSel.setText(title);
            listNoteSelectMulti.set(listMultiSelected);
        });

        binding.layoutActionMulSelInArchive.tvDeleteMul.setOnClickListener(v ->{
            onClickActionDelArc(Constants.DELETE_S);
        });

        binding.layoutActionMulSelInArchive.tvUnarchiveMul.setOnClickListener(v ->{
            onClickActionDelArc(Constants.UN_ARCHIVE_s);
        });
    }

    private void deleteAllReminder(Note note){
        LiveData<List<Reminder>> reminderLiveData = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(note.getId()));
        reminderLiveData.observe(this, reminderList -> {
            if(reminderList != null){
                for(Reminder reminder : reminderList){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            requireActivity(),
                            reminder.getIdReminder(),
                            new Intent(requireActivity(), MyBroadCastReminder.class),
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    getReminderViewModel().deleteReminder(reminder);
                    AlarmUtils.getInstance().setCancelAlarm(requireActivity(), pendingIntent);
                }
                reminderLiveData.removeObservers(this);
            }
            else{
                reminderLiveData.removeObservers(this);
            }
        });
    }

    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
    }
    private void initView() {
        adapter = new NoteAdapter();
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        isGrid = SharePre.getInstance(requireContext()).getTypeShowArchive();
        sortCondition = SharePre.getInstance(requireActivity()).getSortCondition("sort_archive");
        displayType();
        updateTypeShowIcon();
    }

    private void show(List<Note> list){
        if(list == null || list.isEmpty()){
            showWhenEmpty();
        }else{
            showWhenNotEmpty();
        }
    }

    private void showWhenEmpty(){
        binding.tvEmptyNoteInArchive.setVisibility(View.VISIBLE);
        binding.rcViewArchive.setVisibility(View.INVISIBLE);
        binding.ivEmptyArchive.setVisibility(View.VISIBLE);
    }

    private void showWhenNotEmpty(){
        binding.tvEmptyNoteInArchive.setVisibility(View.INVISIBLE);
        binding.rcViewArchive.setVisibility(View.VISIBLE);
        binding.ivEmptyArchive.setVisibility(View.INVISIBLE);
    }


    private void onClickMenu(){
        //bat su kien
        //sort
        binding.layoutMoreSetupInArchive.tvSort.setOnClickListener(click ->{
            //hien thi dialog de sort
            //cancelOnTouchOutSide
            binding.constraintIncludeInArchive.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //chon che do hien thi
        binding.layoutMoreSetupInArchive.tvTypeShow.setOnClickListener(click ->{
            if(isGrid){
                //neu dang la dang luoi
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort,null);
                binding.layoutMoreSetupInArchive.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInArchive.tvTypeShow.setText("List View");
            }
            else{
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps,null);
                binding.layoutMoreSetupInArchive.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInArchive.tvTypeShow.setText("Grid View");
            }
            isGrid = !isGrid;
            SharePre.getInstance(requireContext()).saveTypeShowArchiver(isGrid);
            displayType();
            binding.constraintIncludeInArchive.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //o che do chon nhieu
        binding.layoutMoreSetupInArchive.tvChoose.setOnClickListener(v ->{
            adapter.setMultiSelect(true);
            isMultiSelect = true;
            binding.constraintIncludeInArchive.setVisibility(View.INVISIBLE);
            isShow = false;
            onMultiSelect();
        });

        binding.layoutMoreSetupInArchive.tvSort.setOnClickListener(v ->{
            DialogUtils.showDialogSort(requireContext(),sortCondition,sort ->{
                sortCondition = sort;
                SharePre.getInstance(requireContext()).saveSortCondition("sort_archive",sortCondition);
                onLoadData();
            });
            binding.constraintIncludeInArchive.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //thoat che do chon nhieu
        binding.toolbarSelMulInArchive.icExitMulSel.setOnClickListener(v ->{
            onNormalSelect();
        });

        //chon tat ca
        binding.toolbarSelMulInArchive.acSelectAll.setOnClickListener(v ->{
            if(!isSelectAllNote){
                adapter.setAllClick();
                isSelectAllNote = true;
            }
            else{
                adapter.setClearAllClick();
                isSelectAllNote = false;
            }
        });
    }

    private void onClickActionDelArc(String action){
        if(!listNoteSelectMulti.get().isEmpty()){
            List<Note> noteList = new ArrayList<>(listNoteSelectMulti.get());
            DialogUtils.ActionOnLongClickNote(requireContext(),action, isAccept -> {
                if(isAccept){
                    if(action.equals(Constants.DELETE_S)){
                        for(Note note : noteList){
                            note.setDeleted(true);
                            note.setArchived(false);
                            note.setTimeDeleteNote(System.currentTimeMillis());
                            viewModel.updateNote(note,state ->{});
                            deleteAllReminder(note);
                        }
                    }
                    else{
                        for(Note note : noteList){
                            note.setArchived(false);
                            viewModel.updateNote(note,state ->{});
                        }
                    }
                    onNormalSelect();
                }
            });
        }
        else{
            //empty -> toast
            Toast.makeText(requireContext(), "No notes selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void onMultiSelect(){
        //an cac icon tren toolbar cu
        binding.icExitArchive.setVisibility(View.INVISIBLE);
        binding.title.setVisibility(View.INVISIBLE);
        binding.icMoreInArchive.setVisibility(View.INVISIBLE);
        //hien thi toolbar select multi
        binding.toolbarSMInArchive.setVisibility(View.VISIBLE);
        binding.constrainActionMulSelInArchive.setVisibility(View.VISIBLE);
    }

    private void onNormalSelect(){
        //hien cac icon tren toolbar cu
        binding.icExitArchive.setVisibility(View.VISIBLE);
        binding.title.setVisibility(View.VISIBLE);
        binding.icMoreInArchive.setVisibility(View.VISIBLE);
        adapter.setCancelMultiSelect();
        isMultiSelect = false;
        //an toolbar select multi
        binding.toolbarSMInArchive.setVisibility(View.INVISIBLE);
        binding.toolbarSelMulInArchive.titleMulSel.setText("0 Selected");
        binding.constrainActionMulSelInArchive.setVisibility(View.INVISIBLE);
    }

    private void displayType() {
        if(!isGrid){
            binding.rcViewArchive.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            binding.rcViewArchive.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTypeShowIcon() {
        if (isGrid) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps, null);
            binding.layoutMoreSetupInArchive.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInArchive.tvTypeShow.setText("Grid View");
        } else {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort, null);
            binding.layoutMoreSetupInArchive.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInArchive.tvTypeShow.setText("List View");
        }
    }
    @Override
    public boolean onBackPressed() {
        if(isMultiSelect){
            onNormalSelect();
            adapter.setClearAllClick();
            adapter.setCancelMultiSelect();
            isMultiSelect = false;
            return true;//xu ly nut back o day
        }
        return false;//khong xu li de mainAct xu ly
    }
}