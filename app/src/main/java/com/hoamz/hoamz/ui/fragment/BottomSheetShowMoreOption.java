package com.hoamz.hoamz.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.databinding.BtSheetDetailTypeNoteBinding;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogShow;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetShowMoreOption extends BottomSheetDialogFragment {
    private BtSheetDetailTypeNoteBinding binding;
    private final List<Label> listLabel;
    private final Context context;
    private final LabelViewModel labelViewModel;
    private final Label editLabel;//cai nay khi click vao trc khi hien sheet se duoc truyen vao thong qua constructor
    private final NoteViewModel noteViewModel;
    String oldLabel;

    public BottomSheetShowMoreOption(List<Label> listLabel, Context context, LabelViewModel labelViewModel,NoteViewModel noteViewModel,Label label){
        this.listLabel = listLabel;
        this.context = context;
        this.labelViewModel = labelViewModel;
        this.editLabel = label;
        this.noteViewModel = noteViewModel;
        oldLabel = label.getLabel();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bt_sheet_detail_type_note,container,false);
        binding = BtSheetDetailTypeNoteBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
        binding.tvNameLabel.setText(editLabel.getLabel());
    }

    private void onClick(){
        //doi ten danh muc
        binding.idRenameLabel.setOnClickListener(v ->{
            //an sheet
            DialogShow.showDialogLabel(context, Constants.TitleEditLabel,oldLabel,listLabel, label -> {
                updateNoteLabel(oldLabel,label);
                editLabel.setLabel(label);
                labelViewModel.updateLabel(editLabel);
                dismiss();
            });
        });

        //xoa danh muc
        binding.idDeleteLabel.setOnClickListener(v ->{
            DialogShow.showDialogNotifyDelete(context, delete -> {
                //delete = true thi xoa
                if(delete){
                    //xoa
                    deleteAllNoteByLabel(editLabel);
                    dismiss();
                }
            });
        });
    }

    private void deleteAllNoteByLabel(Label label){
        //xoa nhan
        labelViewModel.deleteLabel(label);
        //xoa tat ca cac note gan nhan nay
        noteViewModel.deleteNotesByLabel(label.getLabel());
    }

    private void updateNoteLabel(String oldLabel, String newLabel) {
        if (!isAdded()) {
            return;
        }

        LiveData<List<Note>> notesLiveData = noteViewModel.getListNotes();
        if (notesLiveData == null) {
            return;
        }

        notesLiveData.observe(getViewLifecycleOwner(), notes -> {
            if (notes == null) {
                return;
            }
            List<Note> listNotesOldLabel = new ArrayList<>();
            for (Note note : notes) {
                if (Objects.equals(note.getLabel(), oldLabel)) {
                    listNotesOldLabel.add(note);
                }
            }

            for (Note note : listNotesOldLabel) {
                note.setLabel(newLabel);
                noteViewModel.updateNote(note);
            }
            notesLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
