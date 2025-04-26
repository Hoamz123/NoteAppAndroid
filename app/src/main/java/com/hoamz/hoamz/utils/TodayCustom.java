package com.hoamz.hoamz.utils;

import static android.graphics.Typeface.BOLD;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.core.content.ContextCompat;
import com.hoamz.hoamz.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class TodayCustom implements DayViewDecorator {
    private CalendarDay calendarDay;
    private Context context;

    public TodayCustom(Context context,CalendarDay calendarDay){
        this.calendarDay = calendarDay;
        this.context = context;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day != null && day.equals(calendarDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
        view.addSpan(new StyleSpan(BOLD));
        view.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.decor_today));
    }
}
