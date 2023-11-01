package com.studentplanner.studentplanner.utils;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import androidx.annotation.IntRange;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class BoundTimePickerDialog extends TimePickerDialog {

    private int minHour = -1;
    private int minMinute = -1;

    private int maxHour = 25;
    private int maxMinute = 25;

    private int selectedHour;
    private int selectedMinute;


    private boolean setMinTimeToNow = false;

    public void setMinTimeToNow(boolean status) {
        setMinTimeToNow = status;
    }


    public BoundTimePickerDialog(Context context, Activity activity, int selectedHour, int selectedMinute) {
        super(context, (TimePickerDialog.OnTimeSetListener) activity, selectedHour, selectedMinute, false);
        this.selectedHour = selectedHour;
        this.selectedMinute = selectedMinute;

        try {
            Class<?> superclass = getClass().getSuperclass();
            Field mTimePickerField = superclass.getDeclaredField("mTimePicker");
            mTimePickerField.setAccessible(true);
            TimePicker timePicker = (TimePicker) mTimePickerField.get(this);
            timePicker.setOnTimeChangedListener(this);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }

    }


    public void setMin(@IntRange(from = 0, to = 23) int hour, @IntRange(from = 0, to = 59) int minute) {
        minHour = hour;
        minMinute = minute;
    }

    public void setMax(@IntRange(from = 0, to = 23) int hour, @IntRange(from = 0, to = 59) int minute) {
        maxHour = hour;
        maxMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int selectedHour, int selectedMinute) {


        if (setMinTimeToNow) {
            LocalTime selectedTime = LocalTime.of(selectedHour, selectedMinute);
            List<Integer> pastTimeHoursList = IntStream.rangeClosed(0, LocalTime.now().getHour() - 1).boxed().collect(Collectors.toList());
            boolean isPastTime = pastTimeHoursList.contains(selectedTime.getHour());
            boolean isValidTime = !isPastTime;
            if (isValidTime) {
                this.selectedHour = selectedHour;
                this.selectedMinute = selectedMinute;
            }
            updateTime(this.selectedHour, this.selectedMinute);
            return;
        }

        constrainStartAndEndTimes(selectedHour, selectedMinute);
    }

    private void constrainStartAndEndTimes(int hourOfDay, int minute) {
        boolean isValidTime = true;

        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)) {
            isValidTime = false;
        }

        if (hourOfDay > maxHour || (hourOfDay == maxHour && minute > maxMinute)) {
            isValidTime = false;
        }

        if (isValidTime) {
            selectedHour = hourOfDay;
            selectedMinute = minute;
        }

        updateTime(selectedHour, selectedMinute);

    }


}