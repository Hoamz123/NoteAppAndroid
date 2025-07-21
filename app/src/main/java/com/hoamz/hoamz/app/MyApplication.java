package com.hoamz.hoamz.app;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;
import com.hoamz.hoamz.worker.NoteWorker;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NoteWorker.class,15, TimeUnit.MINUTES
        ).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("noteWork", ExistingPeriodicWorkPolicy.KEEP,request);
    }
}
