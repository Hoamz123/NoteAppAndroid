package com.hoamz.hoamz.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.databinding.SheetChooseBackgroundBinding;

import java.util.Objects;


public class BottomSheetColor extends BottomSheetDialogFragment {
    private SheetChooseBackgroundBinding binding;
    private onSelectedColor onSelectedColor;

    public void setOnSelectedColor(BottomSheetColor.onSelectedColor onSelectedColor) {
        this.onSelectedColor = onSelectedColor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sheet_choose_background,container,false);//anh xa view
        binding = SheetChooseBackgroundBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickCard();
    }

    private void onClickCard() {
        onClickListener(binding.ivBackground1, R.drawable.img_11);
        onClickListener(binding.ivBackground2, R.drawable.img_12);
        onClickListener(binding.ivBackground3, R.drawable.img_13);
        onClickListener(binding.ivBackground4, R.drawable.img_14);
        onClickListener(binding.ivBackground5, R.drawable.img_15);
        onClickListener(binding.ivBackground6, R.drawable.img_16);
        onClickListener(binding.ivBackground7, R.drawable.img_17);
        onClickListener(binding.ivBackground12, R.drawable.img_5);
        onClickListener(binding.ivBackground13, R.drawable.img_6);

        onClickListener(binding.ivBackground19, R.drawable.img_20);
        onClickListener(binding.ivBackground20, R.drawable.img_21);
        onClickListener(binding.ivBackground21, R.drawable.img_22);
        onClickListener(binding.ivBackground23, R.drawable.img_24);
        onClickListener(binding.ivBackground24, R.drawable.img_25);
        onClickListener(binding.ivBackground25, R.drawable.bg_wg10);
        onClickListener(binding.ivBackground26, R.drawable.bg_wg7);
        onClickListener(binding.ivBackground27, R.drawable.bg_wg8);
        onClickListener(binding.ivBackground28, R.drawable.img_18);

    }

    private void onClickListener(View view,int idBackground){
        view.setOnClickListener(v->{
            onSelectedColor.onSelected(idBackground);
        });
    }
    public interface onSelectedColor{
        void onSelected(int idBackground);
    }
}
