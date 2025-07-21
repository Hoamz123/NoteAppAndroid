package com.hoamz.hoamz.ui.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.MyAnimation;
import com.hoamz.hoamz.utils.TextViewUtils;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    private boolean isBack = false;
    //them animation cho start app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intentReceive = getIntent();
        if(intentReceive != null){
            if(Objects.equals(intentReceive.getAction(), Constants.ActionClickNotify)){
                int id = intentReceive.getIntExtra(Constants.ID_NOTE_CLICK,-1);
                if(id != -1){
                    Intent intent = new Intent(this, NoteDetail.class);
                    intent.putExtra(Constants.KEY_NOTE,id);
                    intent.setAction(Constants.ActionClickNotify);
                    startActivity(intent);
                }
            }
        }

        //hien nhu danh may
        TextViewUtils textViewUtils = findViewById(R.id.tv_welcome);
        textViewUtils.setCharacterDelay(100);
        textViewUtils.animateText("welcome");

//       lam 1 cai gi do trong 3 s chang han
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.show_logo);
        animation.setAnimationListener(new MyAnimation() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                next();
            }
        });
        findViewById(R.id.nameArthur).startAnimation(animation);
    }

    private void next(){
        if(isBack || isDestroyed() || isFinishing()) return;
        //check lan dau cua user o day
        boolean isFirst = SharePre.getInstance(this).checkFirstRunApp();
        Intent intent;
        if(!isFirst){
            //neu lan dau -> sang act gioi thieu
            intent = new Intent(this, FirstRunAct.class);
        }
        else{
            //vao main chinh
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBack = true;
        finish();
    }
}