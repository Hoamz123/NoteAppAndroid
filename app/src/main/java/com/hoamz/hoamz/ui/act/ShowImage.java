package com.hoamz.hoamz.ui.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.hoamz.hoamz.adapter.PhotoAdapter;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.databinding.ActivityShowImageBinding;
import com.hoamz.hoamz.ui.fragment.BottomSheetPreviewImage;
import com.hoamz.hoamz.utils.CameraUtils;
import com.hoamz.hoamz.utils.FileUtils;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ShowImage extends AppCompatActivity {

    private ActivityShowImageBinding binding;
    private BottomSheetPreviewImage bottomSheetPreviewImage;
    private Photo photoSaved = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EdgeToEdge.enable(this);
        binding = ActivityShowImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //khoa dung man hinh

//        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        bottomSheetPreviewImage = new BottomSheetPreviewImage();

        Intent intent = getIntent();
        if(intent != null){
            photoSaved = intent.getParcelableExtra("photo___");
            if(photoSaved != null){
                Glide.with(binding.ivPreviewImage.getContext())
                        .load(photoSaved.getUri())
                        .into(binding.ivPreviewImage);
            }
            else{
                Toast.makeText(this, "photo null", Toast.LENGTH_SHORT).show();
            }
        }

        binding.main.setOnClickListener(v ->{
            if(bottomSheetPreviewImage.isAdded()){
                bottomSheetPreviewImage.dismiss();//an di
            }
            else{
                bottomSheetPreviewImage.show(getSupportFragmentManager(),bottomSheetPreviewImage.getTag());
            }
        });

        binding.ivPreviewImage.setOnClickListener(v ->{
            if(bottomSheetPreviewImage.isAdded()){
                bottomSheetPreviewImage.dismiss();//an di
            }
            else{
                bottomSheetPreviewImage.show(getSupportFragmentManager(),bottomSheetPreviewImage.getTag());
            }
        });

        bottomSheetPreviewImage.setOnClickItemView(action ->{
            switch (action) {
                case "back":
                    finish();
                    break;
                case "delete":
                    deleteImage(photoSaved);
                    finish();
                    break;
                case "download":
                    FileUtils.saveImage(getApplicationContext(),photoSaved);
                    break;
            }
        });
    }

    private void deleteImage(Photo photoSaved){
        PhotoViewModel photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoViewModel.deletePhoto(photoSaved);
    }
}