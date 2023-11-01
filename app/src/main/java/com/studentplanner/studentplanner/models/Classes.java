package com.studentplanner.studentplanner.models;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.tables.ClassTable;

import java.time.LocalTime;

public final class Classes {
    private int classID;
    private int moduleID;
    private int semesterID;
    private int dow;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String classType;

    private TextInputLayout txtDayError;
    private TextInputLayout txtSemesterError;
    private TextInputLayout txtModuleError;
    private TextInputLayout txtClassTypeError;
    private TextInputLayout txtStartTimeError;
    private TextInputLayout txtEndTimeError;

    public TextInputLayout getTxtStartTimeError() {
        return txtStartTimeError;
    }

    public void setTxtStartTimeError(TextInputLayout txtStartTimeError) {
        this.txtStartTimeError = txtStartTimeError;
    }

    public TextInputLayout getTxtEndTimeError() {
        return txtEndTimeError;
    }

    public void setTxtEndTimeError(TextInputLayout txtEndTimeError) {
        this.txtEndTimeError = txtEndTimeError;
    }

    public TextInputLayout getTxtDayError() {
        return txtDayError;
    }

    public TextInputLayout getTxtSemesterError() {
        return txtSemesterError;
    }

    public TextInputLayout getTxtModuleError() {
        return txtModuleError;
    }

    public TextInputLayout getTxtClassTypeError() {
        return txtClassTypeError;
    }

    public Classes(TextInputLayout txtDayError, TextInputLayout txtSemesterError, TextInputLayout txtModuleError, TextInputLayout txtClassTypeError) {
        this.txtDayError = txtDayError;
        this.txtSemesterError = txtSemesterError;
        this.txtModuleError = txtModuleError;
        this.txtClassTypeError = txtClassTypeError;
    }

    public Classes(int moduleID, int semesterID, int dow, LocalTime startTime, LocalTime endTime, String room, String classType) {
        this.moduleID = moduleID;
        this.semesterID = semesterID;
        this.dow = dow;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.classType = classType;
    }

    public Classes(int classID, int moduleID, int semesterID, int dow, LocalTime startTime, LocalTime endTime, String room, String classType) {
        this.classID = classID;
        this.moduleID = moduleID;
        this.semesterID = semesterID;
        this.dow = dow;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.classType = classType;
    }

    public int getClassID() {
        return classID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public int getSemesterID() {
        return semesterID;
    }

    public int getDow() {
        return dow;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public String getClassType() {
        return classType;
    }

    public static ContentValues contentValues(Classes classes) {
        ContentValues cv = new ContentValues();
        cv.put(ClassTable.COLUMN_MODULE_ID, classes.getModuleID());
        cv.put(ClassTable.COLUMN_SEMESTER_ID, classes.getSemesterID());
        cv.put(ClassTable.COLUMN_DOW, classes.getDow());
        cv.put(ClassTable.COLUMN_START_TIME, classes.getStartTime().toString());
        cv.put(ClassTable.COLUMN_END_TIME, classes.getEndTime().toString());
        cv.put(ClassTable.COLUMN_ROOM, classes.getRoom());
        cv.put(ClassTable.COLUMN_TYPE, classes.getClassType());
        return cv;
    }

    @NonNull
    @Override
    public String toString() {
        return "Classes{" +
                "classID=" + classID +
                ", moduleID=" + moduleID +
                ", semesterID=" + semesterID +
                ", dow=" + dow +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", room='" + room + '\'' +
                ", classType='" + classType + '\'' +
                '}';
    }
}
