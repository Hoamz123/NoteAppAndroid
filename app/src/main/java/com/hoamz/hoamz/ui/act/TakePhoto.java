package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.databinding.ActivityTakePhotoBinding;
import com.hoamz.hoamz.utils.CameraUtils;
import com.hoamz.hoamz.utils.MyAnimation;

import java.io.File;

public class TakePhoto extends AppCompatActivity {

    private ActivityTakePhotoBinding binding;
    private PreviewView previewView;
    private Bitmap bitmapSaveTemp;
    private final int REQUEST_CODE_PERMISSIONS_CAMERA = 1001;
    private boolean isTookPhoto = false;
    private boolean isBackCamera = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        previewView = findViewById(R.id.preViewView);
        requestCameraPermission();
        if(checkCameraPermission()){
            CameraUtils.getInstance().startCamera(this,this,previewView,isBackCamera);//mo camera
        }
        else{
            requestCameraPermission();
        }

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.state_camera);

        binding.acRotateCamera.setOnClickListener(v ->{
            binding.acRotateCamera.startAnimation(animation);
            isBackCamera = !isBackCamera;
            CameraUtils.getInstance().startCamera(this,this,previewView,isBackCamera);
        });

        //bat su kien vao hai cai nut (save) va (cancel)
        binding.acTakePicture.setOnClickListener(v ->{
            if(isTookPhoto) return;//khong cho chup lien tiep

            //sau khi chup xong -< an previewView -> hien thi ivPreview de hien thi anh vua chup
            CameraUtils.getInstance().takePhoto(this, bitmap ->{
                bitmapSaveTemp = bitmap;
                runOnUiThread(() ->{
                    binding.preViewView.setVisibility(View.INVISIBLE);//an previewView
                    binding.ivPreviewView.setVisibility(View.VISIBLE);//hien thi ivPreView
                    Glide.with(binding.ivPreviewView.getContext())
                            .load(bitmap)
                            .into(binding.ivPreviewView);
                    isTookPhoto = true;
                    updateAfterTakePhoto();
                });
            });
        });

        binding.icExitTakePhoto.setOnClickListener(v ->{
            finish();
        });

        CameraUtils.getInstance().error.observe(this,error ->{
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

    }

    private void updateAfterTakePhoto(){
        //neu da chup roi -> khong cho chup nua
        binding.acTakePicture.setVisibility(View.INVISIBLE);
        binding.acRotateCamera.setVisibility(View.INVISIBLE);
        //hien thi cancel va save
        binding.acCancelSaveImage.setVisibility(View.VISIBLE);
        binding.acSavePicture.setVisibility(View.VISIBLE);
        binding.acCancelSaveImage.setOnClickListener(v -> {
            binding.preViewView.setVisibility(View.VISIBLE);//hien thi chup lai
            binding.ivPreviewView.setVisibility(View.INVISIBLE);//an hinh anh trc do di
            binding.acTakePicture.setVisibility(View.VISIBLE);//hien thi nut chup anh
            binding.acRotateCamera.setVisibility(View.VISIBLE);//hien thi thay doi trang thai
            binding.acCancelSaveImage.setVisibility(View.INVISIBLE);//an nut cancel
            binding.acSavePicture.setVisibility(View.INVISIBLE);//an nut save
            isTookPhoto = false;
        });
        binding.acSavePicture.setOnClickListener(v -> {
            if (bitmapSaveTemp != null) {
                File file = CameraUtils.getInstance().saveBitmapToFile(bitmapSaveTemp, TakePhoto.this);
                if (file != null) {
                    Intent intent = new Intent();
                    intent.putExtra("PhotoUri", Uri.fromFile(file).toString());
                    setResult(111, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //request permission camera
    private void requestCameraPermission(){
        if(!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS_CAMERA);
        }
    }

    //check permission camera
    private boolean checkCameraPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSIONS_CAMERA){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //da duoc quyen truy cap
                CameraUtils.getInstance().startCamera(getApplicationContext(),this,previewView,isBackCamera);
            }
        }
    }
}