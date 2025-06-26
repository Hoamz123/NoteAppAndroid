package com.hoamz.hoamz.ui.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.ui.fragment.FragmentWidget;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.MyAnimation;

public class Splash extends AppCompatActivity {
    //them animation cho start app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //lam 1 cai gi do trong 3 s chang han
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.show_logo);
        animation.setAnimationListener(new MyAnimation() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                next();
            }
        });
        findViewById(R.id.iv_logo_main).startAnimation(animation);
    }

    private void next(){

        Intent intentReceiver = getIntent();
        if(intentReceiver != null){
            if(Constants.EDIT.equals(intentReceiver.getAction())){
                //gui id nhan duoc qua fgEdit
                int idWidget = intentReceiver.getIntExtra(Constants.ID_WIDGET_CLICK,0);
                if(idWidget != 0){
                    //gui du lieu idWidget qua fgEdit
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fagContainerSplash,FragmentWidget.getInstance(idWidget,"SPLASH"),FragmentWidget.class.getName())
                            .commit();
                }
                return;
            }
        }

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
}