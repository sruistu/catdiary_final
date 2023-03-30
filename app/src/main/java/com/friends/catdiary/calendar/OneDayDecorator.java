package com.friends.catdiary.calendar;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.friends.catdiary.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

//즐겨찾기한 날짜 표시하는 Decorator
public class OneDayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final int color;

    public OneDayDecorator(CalendarDay date, int color) {
        this.date = date;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1f));
        view.addSpan(new ForegroundColorSpan(color));
    }
}
