package com.hoamz.hoamz.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.hoamz.hoamz.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDayCustom implements DayViewDecorator {
    private final CalendarDay calendarDay;
    private final Drawable drawable;

    public SelectedDayCustom(Context context,CalendarDay calendarDay){
        this.calendarDay = calendarDay;
        drawable = ContextCompat.getDrawable(context,R.drawable.decor_selected);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day != null && day.equals(this.calendarDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
