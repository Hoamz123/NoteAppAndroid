package com.hoamz.hoamz.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.databinding.FragmentContactBinding;


public class FragmentContact extends Fragment {

    private FragmentContactBinding binding;

    public FragmentContact() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater, container, false);
        onClick();
        return binding.getRoot();
    }

    private void onClick() {
        //intent khong tuong minh chuyen den facebook cua hoamz : https://www.facebook.com/hoamz123?locale=vi_VN
        binding.lnFacebook.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.facebook.com/hoamz123?locale=vi_VN"));
            startActivity(intent);
        });
        //intent khong tuong minh chuyen den email cua hoamz
        binding.lnEmail.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:hoamzdev@gmail.com"));
            startActivity(intent);
        });

        binding.icExitContact.setOnClickListener(v ->{
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}