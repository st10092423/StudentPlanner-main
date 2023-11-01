package com.studentplanner.studentplanner.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.studentplanner.studentplanner.R;

import java.util.regex.Pattern;


public final class PasswordValidator {
    private final Context context;
    private final ProgressBar progressBar;
    private final TextView strengthView;

    public PasswordValidator(Context context, ProgressBar progressBar, TextView strengthView) {
        this.context = context;
        this.progressBar = progressBar;
        this.strengthView = strengthView;
    }


    public static boolean is8Chars(String password) {
        return password.length() >= 8;
    }

    public static boolean containsSpecialChar(String password) {
        return Pattern.compile("^(?=.*[-+_!@=/#$%^&*., ?{}]).+$").matcher(password).find();
    }

    public static boolean containsNumber(String password) {
        return password.matches(".*[0-9].*");
    }


    public void getProgressBarStatus(int strength) {
        switch (strength) {
            case 1 -> {
                final int COLOUR = context.getColor(R.color.orange);
                setStrength(20, context.getString(R.string.weak), COLOUR);
                setProgressbarColour(COLOUR);
            }
            case 2 -> {

                final int COLOUR = context.getColor(R.color.dark_blue);
                setStrength(40, context.getString(R.string.average), COLOUR);
                setProgressbarColour(COLOUR);
            }
            case 3 -> {

                final int COLOUR = context.getColor(R.color.green);
                setStrength(60, context.getString(R.string.good), COLOUR);
                setProgressbarColour(COLOUR);
            }
            case 4 -> {

                final int COLOUR = context.getColor(R.color.dark_yellow);
                setStrength(80, context.getString(R.string.excellent), COLOUR);

                setProgressbarColour(COLOUR);
            }
            case 5 -> {

                final int COLOUR = context.getColor(R.color.black);
                setStrength(100, context.getString(R.string.strong), COLOUR);
                setProgressbarColour(COLOUR);
            }
            default -> defaultStrength();
        }
    }

    private void setStrength(int progress, String status, int textColour) {
        progressBar.setProgress(progress);
        strengthView.setText(status);
        strengthView.setTextColor(textColour);

    }

    private void defaultStrength() {
        progressBar.setProgress(0);
        strengthView.setText("");

    }

    private void setProgressbarColour(final int selectedColour) {
        progressBar.setProgressTintList(ColorStateList.valueOf(selectedColour));


    }

    public static boolean containsUpperCase(final String password) {
        return !password.equals(password.toLowerCase());
    }

    public static boolean containsLowerCase(final String password) {
        return !password.equals(password.toUpperCase());
    }

}
