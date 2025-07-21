package com.hoamz.hoamz.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.databinding.FragmentBinBinding;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class FragmentBin extends BaseFragment {
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    LiveData<List<Note>> listNOteDeleted;
    private PhotoViewModel photoViewModel;
    private boolean isShow = false;
    private boolean isMultiSelect = false;
    private boolean isSelectAllNote = false;
    private final AtomicReference<Set<Note>> listNoteSelectMulti = new AtomicReference<>(new HashSet<>());//luu lai danh sach cac note da chon
    private boolean isGrid = true;
    private FragmentBinBinding binding;
    private String sortCondition = Constants.sortNewToOld;

    public FragmentBin() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBinBinding.inflate(inflater,container,false);
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
        if(listNOteDeleted != null){
            listNOteDeleted.removeObservers(this);
        }

        listNOteDeleted = Transformations.distinctUntilChanged(viewModel.getNotesDeleted(sortCondition));
        listNOteDeleted.observe(getViewLifecycleOwner(), notesDeleted -> {
            show(notesDeleted);
            if(notesDeleted != null){
                adapter.setNoteList(notesDeleted);
            }
        });

        if(binding.rcViewBin.getAdapter() == null){
            binding.rcViewBin.setAdapter(adapter);
        }
    }

    private void onClick(){
        binding.icExitBin.setOnClickListener(v ->{
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        adapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                DialogUtils.ActionClickLongItem(getContext(),Constants.DELETE,action ->{
                    if(action.equals("delete")){
                        //xoa
                        DialogUtils.ActionOnLongClickNote(getContext(), Constants.DELETE, isAccept ->{
                            if(isAccept){
                                note.setDeleted(false);
                                viewModel.updateNote(note,state ->{});
                                viewModel.deleteNote(note,state ->{
                                    Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
                                    onLoadData();
                                });
                                //xoa het tat ca cac anh
                                LiveData<List<Photo>> listPhoto = Transformations.distinctUntilChanged(photoViewModel.getAllPhotosByIdNote(note.getId()));
                                listPhoto.observe(getViewLifecycleOwner(),photos ->{
                                    if(photos != null && !photos.isEmpty()){
                                        for(Photo photo : photos){
                                            photoViewModel.deletePhoto(photo);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else {
                        //restore here
                        note.setDeleted(false);
                        viewModel.updateNote(note,state ->{});
                    }
                });
            }
            @Override
            public void onItemLongClick(Note note) {}
        });

        binding.icMoreInBin.setOnClickListener(v ->{
            binding.constraintIncludeInTrash.setVisibility(View.VISIBLE);//hien thi
            isShow = true;
        });

        //an ra ngoai thi an di
        binding.constraintIncludeInTrash.setOnClickListener(v ->{
            if(isShow){
                isShow = false;
                binding.constraintIncludeInTrash.setVisibility(View.INVISIBLE);
            }
        });
        adapter.setOnMultiSelectItem(listMultiSelected -> {
            String title = listMultiSelected.size() + " Selected";
            binding.toolbarSelMulInTrash.titleMulSel.setText(title);
            listNoteSelectMulti.set(listMultiSelected);
        });

        binding.layoutActionMulSelInTrash.tvDeleteMul.setOnClickListener(v ->{
            onClickActionDelArc(Constants.DELETE_S);
        });

        binding.layoutActionMulSelInTrash.tvRestore.setOnClickListener(v ->{
            onClickActionDelArc(Constants.RESTORE);
        });

    }
    private void initView() {
        adapter = new NoteAdapter();
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        isGrid = SharePre.getInstance(requireContext()).getTypeShowTrash();
        displayType();
        sortCondition = SharePre.getInstance(requireActivity()).getSortCondition("sort_trash");
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
        binding.tvEmptyNoteInTrash.setVisibility(View.VISIBLE);
        binding.rcViewBin.setVisibility(View.INVISIBLE);
        binding.ivEmptyBin.setVisibility(View.VISIBLE);
    }

    private void showWhenNotEmpty(){
        binding.tvEmptyNoteInTrash.setVisibility(View.INVISIBLE);
        binding.rcViewBin.setVisibility(View.VISIBLE);
        binding.ivEmptyBin.setVisibility(View.INVISIBLE);
    }

    private void onClickMenu(){
        //bat su kien
        //sort
        binding.layoutMoreSetupInTrash.tvSort.setOnClickListener(click ->{
            //hien thi dialog de sort
            //cancelOnTouchOutSide
            binding.constraintIncludeInTrash.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //chon che do hien thi
        binding.layoutMoreSetupInTrash.tvTypeShow.setOnClickListener(click ->{
            if(isGrid){
                //neu dang la dang luoi
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort,null);
                binding.layoutMoreSetupInTrash.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInTrash.tvTypeShow.setText("List View");
            }
            else{
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps,null);
                binding.layoutMoreSetupInTrash.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.layoutMoreSetupInTrash.tvTypeShow.setText("Grid View");
            }
            isGrid = !isGrid;
            SharePre.getInstance(requireContext()).saveTypeShowTrash(isGrid);
            displayType();
            binding.constraintIncludeInTrash.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //o che do chon nhieu
        binding.layoutMoreSetupInTrash.tvChoose.setOnClickListener(v ->{
            adapter.setMultiSelect(true);
            isMultiSelect = true;
            binding.constraintIncludeInTrash.setVisibility(View.INVISIBLE);
            isShow = false;
            onMultiSelect();
        });

        binding.layoutMoreSetupInTrash.tvSort.setOnClickListener(v ->{
            DialogUtils.showDialogSort(requireContext(),sortCondition,sort ->{
                sortCondition = sort;
                SharePre.getInstance(requireContext()).saveSortCondition("sort_trash",sortCondition);
                onLoadData();
            });
            binding.constraintIncludeInTrash.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //thoat che do chon nhieu
        binding.toolbarSelMulInTrash.icExitMulSel.setOnClickListener(v ->{
            adapter.setCancelMultiSelect();
            isMultiSelect = false;
            onNormalSelect();
        });

        //chon tat ca
        binding.toolbarSelMulInTrash.acSelectAll.setOnClickListener(v ->{
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
                            note.setDeleted(false);
                            viewModel.updateNote(note,state ->{});
                            viewModel.deleteNote(note,state ->{
                                Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
                                onLoadData();
                            });
                            //xoa het tat ca cac anh
                            LiveData<List<Photo>> listPhoto = Transformations.distinctUntilChanged(photoViewModel.getAllPhotosByIdNote(note.getId()));
                            listPhoto.observe(getViewLifecycleOwner(),photos ->{
                                if(photos != null && !photos.isEmpty()){
                                    for(Photo photo : photos){
                                        photoViewModel.deletePhoto(photo);
                                    }
                                }
                            });
                        }
                    }
                    else{
                        for(Note note : noteList){
                            //restore here
                            note.setDeleted(false);
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
        binding.icExitBin.setVisibility(View.INVISIBLE);
        binding.title.setVisibility(View.INVISIBLE);
        binding.icMoreInBin.setVisibility(View.INVISIBLE);
        //hien thi toolbar select multi
        binding.toolbarSMInTrash.setVisibility(View.VISIBLE);
        binding.constrainActionMulSelInTrash.setVisibility(View.VISIBLE);
    }

    private void onNormalSelect(){
        //hien cac icon tren toolbar cu
        binding.icMoreInBin.setVisibility(View.VISIBLE);
        binding.title.setVisibility(View.VISIBLE);
        binding.icExitBin.setVisibility(View.VISIBLE);
        //an toolbar select multi
        binding.toolbarSMInTrash.setVisibility(View.INVISIBLE);
        binding.toolbarSelMulInTrash.titleMulSel.setText("0 Selected");
        binding.constrainActionMulSelInTrash.setVisibility(View.INVISIBLE);
    }

    private void displayType() {
        if(!isGrid){
            binding.rcViewBin.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            binding.rcViewBin.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTypeShowIcon() {
        if (isGrid) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps, null);
            binding.layoutMoreSetupInTrash.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInTrash.tvTypeShow.setText("Grid View");
        } else {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort, null);
            binding.layoutMoreSetupInTrash.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetupInTrash.tvTypeShow.setText("List View");
        }
    }

    //onBackPressed
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