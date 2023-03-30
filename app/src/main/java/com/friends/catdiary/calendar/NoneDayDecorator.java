package com.friends.catdiary.calendar;

import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

//일기 있는 날짜 표시해주는 Decorator
public class NoneDayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final int color;

    public NoneDayDecorator(CalendarDay date, int color) {
        this.date = date;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(color));
    }
}
