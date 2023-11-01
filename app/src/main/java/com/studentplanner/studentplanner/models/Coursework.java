package com.studentplanner.studentplanner.models;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.interfaces.Searchable;
import com.studentplanner.studentplanner.tables.CourseworkTable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public final class Coursework implements Searchable {

    private int courseworkID;
    private int moduleID;
    private String title;
    private String description;
    private String priority;
    private LocalDate deadline;
    private LocalTime deadlineTime;
    private boolean isCompleted;
    private TextInputLayout txtTitle;
    private TextInputLayout txtModuleID;
    private TextInputLayout txtPriority;

    private AutoCompleteTextView txtDeadline;
    private AutoCompleteTextView txtDeadlineTime;
    private TextInputLayout txtDeadlineTimeError;
    private Bitmap image;
    private byte[] byteImage;

    public Bitmap getImage() {
        return image;
    }

    public byte[] getByteImage() {
        return byteImage;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setImage(byte[] image) {
        this.byteImage = image;
    }

    public TextInputLayout getTxtDeadlineError() {
        return txtDeadlineError;
    }

    public void setTxtDeadlineError(TextInputLayout txtDeadlineError) {
        this.txtDeadlineError = txtDeadlineError;
    }

    private TextInputLayout txtDeadlineError;


    public AutoCompleteTextView getTxtDeadline() {
        return txtDeadline;
    }

    public void setTxtDeadline(AutoCompleteTextView txtDeadline) {
        this.txtDeadline = txtDeadline;
    }

    public AutoCompleteTextView getTxtDeadlineTime() {
        return txtDeadlineTime;
    }

    public void setTxtDeadlineTime(AutoCompleteTextView txtDeadlineTime) {
        this.txtDeadlineTime = txtDeadlineTime;
    }

    public TextInputLayout getTxtDeadlineTimeError() {
        return txtDeadlineTimeError;
    }

    public void setTxtDeadlineTimeError(TextInputLayout txtDeadlineTimeError) {
        this.txtDeadlineTimeError = txtDeadlineTimeError;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public static boolean isCompleted(String completionStatus) {
        return "Yes".equalsIgnoreCase(completionStatus);
    }


    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }


    public TextInputLayout getTxtPriority() {
        return txtPriority;
    }


    public TextInputLayout getTxtModuleID() {
        return txtModuleID;
    }

    public TextInputLayout getTxtTitle() {
        return txtTitle;
    }


    public void setCourseworkID(int courseworkID) {
        this.courseworkID = courseworkID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setDeadlineTime(LocalTime deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public void setTxtTitle(TextInputLayout txtTitle) {
        this.txtTitle = txtTitle;
    }

    public void setTxtModuleID(TextInputLayout txtModuleID) {
        this.txtModuleID = txtModuleID;
    }

    public void setTxtPriority(TextInputLayout txtPriority) {
        this.txtPriority = txtPriority;
    }

    public Coursework() {

    }

    public Coursework(TextInputLayout txtTitle, TextInputLayout txtModuleID, TextInputLayout txtPriority) {
        this.txtTitle = txtTitle;
        this.txtModuleID = txtModuleID;
        this.txtPriority = txtPriority;
    }

    public Coursework(int moduleID, String title, String description, String priority, LocalDate deadline, LocalTime deadlineTime) {
        this.moduleID = moduleID;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.deadlineTime = deadlineTime;
    }

    public Coursework(int courseworkID, int moduleID, String title, String description, String priority, LocalDate deadline, LocalTime deadlineTime) {
        this.courseworkID = courseworkID;
        this.moduleID = moduleID;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.deadlineTime = deadlineTime;

    }

    public static Comparator<Coursework> sortDeadlineAsc = Comparator.comparing(Coursework::getDeadline);
    public static Comparator<Coursework> sortDeadlineDesc = (c1, c2) -> c2.getDeadline().compareTo(LocalDate.now());


    public int getCourseworkID() {
        return courseworkID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalTime getDeadlineTime() {
        return deadlineTime;
    }

    @Override
    public String toString() {
        return "Coursework{" +
                "courseworkID=" + courseworkID +
                ", moduleID=" + moduleID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", deadline='" + deadline + '\'' +
                ", deadlineTime='" + deadlineTime + '\'' +
                '}';
    }

    @Override
    public String searchText() {
        return title;
    }

    public static ContentValues contentValues(Coursework coursework) {
        ContentValues cv = new ContentValues();
        cv.put(CourseworkTable.COLUMN_MODULE_ID, coursework.getModuleID());
        cv.put(CourseworkTable.COLUMN_TITLE, coursework.getTitle());
        cv.put(CourseworkTable.COLUMN_DESCRIPTION, coursework.getDescription());
        cv.put(CourseworkTable.COLUMN_PRIORITY, coursework.getPriority());
        cv.put(CourseworkTable.COLUMN_DEADLINE, coursework.getDeadline().toString());
        cv.put(CourseworkTable.COLUMN_DEADLINE_TIME, coursework.getDeadlineTime().toString());
        return cv;
    }
    public static List<Coursework> sortList(List<Coursework> list) {
        return !list.isEmpty() ? list.stream().sorted(sortDeadlineAsc).toList(): list;

    }
    public static List<Coursework> sortList(List<Coursework> list, Comparator<Coursework> comparator) {
        return !list.isEmpty() ? list.stream().sorted(comparator).toList(): list;

    }


}
