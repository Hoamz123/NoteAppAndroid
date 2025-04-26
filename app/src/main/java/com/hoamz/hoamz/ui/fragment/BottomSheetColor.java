package com.hoamz.hoamz.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.databinding.SheetColorBinding;

import java.util.Objects;


public class BottomSheetColor extends BottomSheetDialogFragment {
    private SheetColorBinding binding;
    private onSelectedColor onSelectedColor;

    public void setOnSelectedColor(BottomSheetColor.onSelectedColor onSelectedColor) {
        this.onSelectedColor = onSelectedColor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sheet_color,container,false);//anh xa view
        binding = SheetColorBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickCard();

    }

    private void onClickCard() {
        binding.colorCyan.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color1);
            onSelectedColor.onSelected(color);
        });
        binding.colorVivid.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color2);
            onSelectedColor.onSelected(color);
        });
        binding.colorPure.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color3);
            onSelectedColor.onSelected(color);
        });
        binding.colorNeon.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color4);
            onSelectedColor.onSelected(color);
        });
        binding.colorVibrant.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color5);
            onSelectedColor.onSelected(color);
        });
        binding.colorElectricPurple.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color6);
            onSelectedColor.onSelected(color);
        });
        binding.colorSkyBlue.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color7);
            onSelectedColor.onSelected(color);
        });
        binding.colorElectricPink.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color8);
            onSelectedColor.onSelected(color);
        });
        binding.colorLively.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color9);
            onSelectedColor.onSelected(color);
        });
        binding.colorLime.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color10);
            onSelectedColor.onSelected(color);
        });
        binding.colorSoft.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color11);
            onSelectedColor.onSelected(color);
        });
        binding.colorDeepBlueGrey.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color12);
            onSelectedColor.onSelected(color);
        });
        binding.colorDarkIndigo.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color13);
            onSelectedColor.onSelected(color);
        });
        binding.colorSlate.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color14);
            onSelectedColor.onSelected(color);
        });
        binding.colorCarbon.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color15);
            onSelectedColor.onSelected(color);
        });
        binding.colorDeepPurpleNight.setOnClickListener(v -> {
            int color = ContextCompat.getColor(requireContext(), R.color.color16);
            onSelectedColor.onSelected(color);
        });

    }

    public interface onSelectedColor{
        void onSelected(int color);
    }
}
