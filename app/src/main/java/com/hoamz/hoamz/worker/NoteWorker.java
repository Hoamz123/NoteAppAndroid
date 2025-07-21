package com.hoamz.hoamz.worker;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.hoamz.hoamz.data.repository.NoteRepo;

public class NoteWorker extends Worker {
    public NoteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NoteRepo noteRepo = new NoteRepo (getApplicationContext());
        noteRepo.deleteNotesAfter30Days();
        return Result.success();
    }
}
