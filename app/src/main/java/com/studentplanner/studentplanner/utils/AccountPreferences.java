package com.studentplanner.studentplanner.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public final class AccountPreferences {

    private static final String LOGIN_SHARED_PREF = "LoginDetails";

    private static final String STUDENT_ID = "StudentID";


    private AccountPreferences() {

    }

    public static void setLoginShredPref(Context context, int studentID) {
        SharedPreferences loginSharedPref = context.getSharedPreferences(LOGIN_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginSharedPref.edit();
        editor.putInt(STUDENT_ID, studentID);
        editor.apply();
    }

    public static int getStudentID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AccountPreferences.LOGIN_SHARED_PREF, MODE_PRIVATE);
        return preferences.getInt(STUDENT_ID, 0);
    }

    public static void logout(Context context) {
        context.deleteSharedPreferences(LOGIN_SHARED_PREF);
    }


}
