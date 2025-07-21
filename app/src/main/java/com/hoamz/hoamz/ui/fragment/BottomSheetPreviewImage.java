package com.hoamz.hoamz.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.databinding.BtSheetPreviewImageBinding;

public class BottomSheetPreviewImage extends BottomSheetDialogFragment {
    private BtSheetPreviewImageBinding binding;
    public BottomSheetPreviewImage() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bt_sheet_preview_image,container,false);
        binding = BtSheetPreviewImageBinding.bind(view);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClick();
    }

    private void onClick(){
        binding.acBack.setOnClickListener(v ->{
            onClickItemView.onClick("back");
            dismiss();//an
        });

        binding.acDeleteImage.setOnClickListener(v ->{
            onClickItemView.onClick("delete");
            dismiss();
        });

        binding.acDownloadImage.setOnClickListener(v ->{
            onClickItemView.onClick("download");
            dismiss();
        });
    }

    public interface onClickItemView{
        void onClick(String action);
    }

    private onClickItemView onClickItemView;

    public void setOnClickItemView(onClickItemView onClickItemView) {
        this.onClickItemView = onClickItemView;
    }
}
