package com.hoamz.hoamz.ui.fragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.FragmentFavoriteNoteBinding;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class FragmentFavoriteNote extends BaseFragment {
    private LiveData<List<Note>> listNoteFavorite;
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    private boolean isShow = false;
    private boolean isMultiSelect = false;
    private String sortCondition = Constants.sortNewToOld;
    private boolean isSelectAllNote = false;
    private final AtomicReference<Set<Note>> listNoteSelectMulti = new AtomicReference<>(new HashSet<>());//luu lai danh sach cac note da chon
    private boolean isGrid = true;
    private FragmentFavoriteNoteBinding binding;

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
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteNoteBinding.inflate(inflater,container,false);
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
        if(listNoteFavorite != null) listNoteFavorite.removeObservers(getViewLifecycleOwner());
        listNoteFavorite = Transformations.distinctUntilChanged(viewModel.getListNoteFavorite(sortCondition));
        listNoteFavorite.observe(getViewLifecycleOwner(), list -> {
            List<Note> listNote = new ArrayList<>(list);
            show(listNote);
            adapter.setNoteList(listNote);
        });
        if(binding.rcViewFavorite.getAdapter() == null){
            binding.rcViewFavorite.setAdapter(adapter);
        }
    }

    private void onClick(){
        binding.icExitFavorite.setOnClickListener(v ->{
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        adapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(), NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                //long click
                DialogUtils.ActionOnLongClickNote(getActivity(),Constants.DELETE, isAccept -> {
                    if(isAccept){
                        note.setDeleted(true);
                        viewModel.updateNote(note,state ->{});
                        deleteAllReminder(note);
                    }
                });
            }
        });
        binding.icMoreInFavorites.setOnClickListener(v ->{
            binding.constraintIncludeInFavorites.setVisibility(View.VISIBLE);//hien thi
            isShow = true;
        });

        //an ra ngoai thi an di
        binding.constraintIncludeInFavorites.setOnClickListener(v ->{
            if(isShow){
                isShow = false;
                binding.constraintIncludeInFavorites.setVisibility(View.INVISIBLE);
            }
        });
        adapter.setOnMultiSelectItem(listMultiSelected -> {
            String title = listMultiSelected.size() + " Selected";
            binding.toolbarSelMulInFavorites.titleMulSel.setText(title);
            listNoteSelectMulti.set(listMultiSelected);
        });

        binding.layoutActionMulSelInFavorites.tvDeleteMul.setOnClickListener(v ->{
            onClickActionDelArc(Constants.DELETE_S);
        });

        binding.layoutActionMulSelInFavorites.tvArchiveMul.setOnClickListener(v ->{
            onClickActionDelArc(Constants.ARCHIVE_S);
        });
        binding.layoutActionMulSelInFavorites.tvRemoveFavorites.setOnClickListener(v ->{
            for(Note note : listNoteSelectMulti.get()){
                note.setFavorite(false);
                viewModel.updateNote(note,status ->{});
            }
        });
    }

    private void initView() {
        adapter = new NoteAdapter();
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        isGrid = SharePre.getInstance(requireContext()).getTypeShowFavorite();
        sortCondition = SharePre.getInstance(requireActivity()).getSortCondition("sort_favorites");
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
        binding.tvEmptyInFavorites.setVisibility(View.VISIBLE);
        binding.rcViewFavorite.setVisibility(View.INVISIBLE);
        binding.ivEmptyFavorite.setVisibility(View.VISIBLE);
    }

    private void showWhenNotEmpty(){
        binding.tvEmptyInFavorites.setVisibility(View.INVISIBLE);
        binding.rcViewFavorite.setVisibility(View.VISIBLE);
        binding.ivEmptyFavorite.setVisibility(View.INVISIBLE);
    }
    private void onClickMenu(){
        //bat su kien
        //sort
        binding.layoutMoreSetupInFavorites.tvSort.setOnClickListener(click ->{
            //hien thi dialog de sort
            //cancelOnTouchOutSide
            binding.constraintIncludeInFavorites.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //chon che do hien thi
        binding.layoutMoreSetupInFavorites.tvTypeShow.setOnClickListener(click ->{
            if(isGrid){
                //neu dang la dang luoi
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort,null);
                binding.layoutMoreSetupInFavorites.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInFavorites.tvTypeShow.setText("List View");
            }
            else{
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps,null);
                binding.layoutMoreSetupInFavorites.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInFavorites.tvTypeShow.setText("Grid View");
            }
            isGrid = !isGrid;
            SharePre.getInstance(requireContext()).saveTypeShowFavorite(isGrid);
            displayType();
            binding.constraintIncludeInFavorites.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //o che do chon nhieu
        binding.layoutMoreSetupInFavorites.tvChoose.setOnClickListener(v ->{
            adapter.setMultiSelect(true);
            isMultiSelect = true;
            binding.constraintIncludeInFavorites.setVisibility(View.INVISIBLE);
            isShow = false;
            onMultiSelect();
        });

        binding.layoutMoreSetupInFavorites.tvSort.setOnClickListener(v ->{
            DialogUtils.showDialogSort(requireContext(),sortCondition,sort ->{
                sortCondition = sort;
                SharePre.getInstance(requireContext()).saveSortCondition("sort_favorites",sortCondition);
                onLoadData();
            });
            binding.constraintIncludeInFavorites.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //thoat che do chon nhieu
        binding.toolbarSelMulInFavorites.icExitMulSel.setOnClickListener(v ->{
            adapter.setCancelMultiSelect();
            isMultiSelect = false;
            onNormalSelect();
        });

        //chon tat ca
        binding.toolbarSelMulInFavorites.acSelectAll.setOnClickListener(v ->{
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
                    adapter.setCancelMultiSelect();
                    isMultiSelect = false;
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
        binding.icExitFavorite.setVisibility(View.INVISIBLE);
        binding.title.setVisibility(View.INVISIBLE);
        binding.icMoreInFavorites.setVisibility(View.INVISIBLE);
        //hien thi toolbar select multi
        binding.toolbarSMInFavorites.setVisibility(View.VISIBLE);
        binding.constrainActionMulSelInFavorite.setVisibility(View.VISIBLE);
    }

    private void onNormalSelect(){
        //hien cac icon tren toolbar cu
        binding.icMoreInFavorites.setVisibility(View.VISIBLE);
        binding.title.setVisibility(View.VISIBLE);
        binding.icExitFavorite.setVisibility(View.VISIBLE);
        //an toolbar select multi
        binding.toolbarSMInFavorites.setVisibility(View.INVISIBLE);
        binding.toolbarSelMulInFavorites.titleMulSel.setText("0 Selected");
        binding.constrainActionMulSelInFavorite.setVisibility(View.INVISIBLE);
    }

    private void displayType() {
        if(!isGrid){
            binding.rcViewFavorite.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            binding.rcViewFavorite.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTypeShowIcon() {
        if (isGrid) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps, null);
            binding.layoutMoreSetupInFavorites.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInFavorites.tvTypeShow.setText("Grid View");
        } else {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort, null);
            binding.layoutMoreSetupInFavorites.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInFavorites.tvTypeShow.setText("List View");
        }
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

    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
    }

    @Override
    public boolean onBackPressed() {
        if(isMultiSelect){
            onNormalSelect();
            isMultiSelect = false;
            return true;//xu ly nut back o day
        }
        return false;//khong xu li de mainAct xu ly
    }

}