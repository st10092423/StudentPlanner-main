package com.studentplanner.studentplanner.models;

import android.content.ContentValues;
import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.interfaces.Searchable;
import com.studentplanner.studentplanner.tables.TeacherTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Teacher extends User implements Searchable {
    public Teacher(String firstname, String lastname, String email) {
        super(firstname, lastname, email);
    }

    public Teacher(TextInputLayout txtFirstName, TextInputLayout txLastName, TextInputLayout txtEmail) {
        super(txtFirstName, txLastName, txtEmail);
    }

    public Teacher(int userID, String firstname, String lastname, String email) {
        super(userID, firstname, lastname, email);
    }

    private static List<Teacher> defaultTeachers() {
        List<Teacher> teacherList = new ArrayList<>();

        return teacherList;
    }

    public static void addDefaultTeachers(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        defaultTeachers().forEach(t -> db.addTeacher(new Teacher(t.getFirstname(), t.getLastname(), t.getEmail())));
    }

    @Override
    public String searchText() {
        return getName();
    }

    public static ContentValues contentValues(Teacher teacher) {
        ContentValues cv = new ContentValues();
        cv.put(TeacherTable.COLUMN_FIRSTNAME, teacher.getFirstname());
        cv.put(TeacherTable.COLUMN_LASTNAME, teacher.getLastname());
        cv.put(TeacherTable.COLUMN_EMAIL, teacher.getEmail());
        return cv;
    }
    public static List<Teacher> sortList(List<Teacher> list) {
        if (!list.isEmpty()) {
            list.sort(Comparator.comparing(User::getLastname));
        }
        return list;

    }
}
