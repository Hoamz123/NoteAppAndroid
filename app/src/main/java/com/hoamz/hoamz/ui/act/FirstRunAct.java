package com.hoamz.hoamz.ui.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.viewmodel.LabelViewModel;

public class FirstRunAct extends AppCompatActivity {

    private Button btnStart;
    private LabelViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_run);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        btnStart = findViewById(R.id.btnStart);

        //blablala -> main
        model = new ViewModelProvider(this).get(LabelViewModel.class);

        //tao cac gia tri mac dinh ban dau khi bat dau dung app

        btnStart.setOnClickListener(v ->{
            //neu nhu chay vao dau -> mat lan dau
            //chen mac dinh cho 3 cai nhan
            model.insertLabel(new Label("All"));
            model.insertLabel(new Label("Home"));
            model.insertLabel(new Label("Work"));
            SharePre.getInstance().saveSecondRunApp();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });


    }
}