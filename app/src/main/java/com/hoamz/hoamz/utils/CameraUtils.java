package com.hoamz.hoamz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class CameraUtils {
    private volatile static CameraUtils instance;
    private ImageCapture imageCapture;
    public MutableLiveData<String> error = new MutableLiveData<>();//quan sat loi

    private CameraUtils() {

    }

    public static CameraUtils getInstance() {
        if (instance == null) {
            synchronized (CameraUtils.class) {
                if (instance == null) {
                    instance = new CameraUtils();
                }
            }
        }
        return instance;
    }

    //start cameraX
    public void startCamera(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView, boolean isBackCamera) {
        ListenableFuture<ProcessCameraProvider> processCameraProviderListenableFuture = ProcessCameraProvider.getInstance(context);
        processCameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider processCameraProvider = processCameraProviderListenableFuture.get();
                //khoi tao preview
                Preview preview = new Preview.Builder().build();
                //khoi tao selector
                CameraSelector cameraSelector = (isBackCamera) ? CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setJpegQuality(95)
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());//lien ket preview voi previewView
                processCameraProvider.unbindAll();//huy cac lien ket truoc do
                processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview);
            } catch (ExecutionException | InterruptedException e) {
                error.postValue("Error" + e.getMessage());
            }
        }, getMainThreadExecutor(context));
    }

    //take photo
    public void takePhoto(Context context, Consumer<Bitmap> consumer) {
        if (imageCapture == null) return;
        imageCapture.takePicture(getMainThreadExecutor(context), new ImageCapture.OnImageCapturedCallback() {
            @OptIn(markerClass = ExperimentalGetImage.class)
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                //convert tu imageProxy sang bitmap
                Bitmap bitmap = image.toBitmap();
                //quay lai luong chinh de cap nhat UI
                consumer.accept(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCaptureException e) {
                super.onError(e);
                //Xử lý lỗi
                error.postValue("Error" + e.getMessage());
            }
        });
    }

    private Executor getMainThreadExecutor(Context context) {
        return ContextCompat.getMainExecutor(context);
    }


    //convert bitmap sang byte[]
    public byte[] convertBitmapToByte(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //convert byte[] -> bitmap

    public Bitmap convertByteToBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public File saveBitmapToFile(Bitmap bitmap, Context context) {
        File file = new File(context.getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}