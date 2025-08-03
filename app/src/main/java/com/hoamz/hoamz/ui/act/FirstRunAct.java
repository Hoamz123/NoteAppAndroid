package com.hoamz.hoamz.ui.act;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.NoCopySpan;
import android.util.Half;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

public class FirstRunAct extends AppCompatActivity {

    private Button btnStart;
    private LabelViewModel model;
    private NoteViewModel noteViewModel;
    private LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_run);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        btnStart = findViewById(R.id.btnStart);

        //blablala -> main
        model = new ViewModelProvider(this).get(LabelViewModel.class);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        lottieAnimationView = findViewById(R.id.lottie);


        LottieCompositionFactory.fromAsset(this,"splash")
                .addListener(composition -> {
            lottieAnimationView.setComposition(composition);
            lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
            lottieAnimationView.playAnimation();
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->{
            btnStart.setAlpha(0f);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.animate().alpha(1f).setDuration(500).start();
        },2500);

        //tao cac gia tri mac dinh ban dau khi bat dau dung app
        btnStart.setOnClickListener(v ->{
            //neu nhu chay vao dau -> mat lan dau
            //chen mac dinh cho 3 cai nhan
            model.insertLabel(new Label("All"),state ->{});
            model.insertLabel(new Label("Home"),state ->{});
            model.insertLabel(new Label("Work"),state ->{});
            //chen cho cai note mac dinh
            @SuppressLint("UseCompatLoadingForDrawables") Note note = new Note(Constants.feature,Constants.content_welcome,System.currentTimeMillis(),
                    false,1,true,false,false,"All",
                    R.drawable.img_12,-1L);
            noteViewModel.insertNewNote(note,state ->{});
            SharePre.getInstance(this).saveSecondRunApp();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}