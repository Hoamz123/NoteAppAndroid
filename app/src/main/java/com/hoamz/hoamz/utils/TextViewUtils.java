package com.hoamz.hoamz.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TextViewUtils extends AppCompatTextView {
    private CharSequence text;//noi dung can hien thi
    private int index;//vi tri dang hien thi
    private long delay;//thoi gian delay

    private final Handler handler = new Handler(Looper.getMainLooper());

    public TextViewUtils(@NonNull Context context) {
        super(context);
    }

    public TextViewUtils(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewUtils(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void animateText(CharSequence txt) {
        text = txt;
        index = 0;
        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);//goi lai sau 150ms
    }

    public void setCharacterDelay(long millis) {
        delay = millis;
    }

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(this, delay);
            }
        }
    };




}
