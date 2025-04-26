package com.hoamz.hoamz.utils;

import android.view.animation.Animation;

public abstract class MyAnimation implements Animation.AnimationListener {

    @Override
    public void onAnimationStart(android.view.animation.Animation animation) {
        //bat dau animation
    }

    @Override
    public void onAnimationEnd(android.view.animation.Animation animation) {
        //ket thuc animation
    }

    @Override
    public void onAnimationRepeat(android.view.animation.Animation animation) {

    }
}
