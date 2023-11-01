package com.studentplanner.studentplanner.models;

public final class CustomTimePicker {
    private int selectedHour;
    private int selectedMinute;

    public int getSelectedHour() {
        return selectedHour;
    }

    public void setSelectedHour(int selectedHour) {
        this.selectedHour = selectedHour;
    }

    public int getSelectedMinute() {
        return selectedMinute;
    }

    public void setSelectedMinute(int selectedMinute) {
        this.selectedMinute = selectedMinute;
    }

    public CustomTimePicker(int selectedHour, int selectedMinute) {
        this.selectedHour = selectedHour;
        this.selectedMinute = selectedMinute;
    }
}