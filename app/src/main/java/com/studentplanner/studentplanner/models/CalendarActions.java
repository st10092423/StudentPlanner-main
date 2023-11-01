package com.studentplanner.studentplanner.models;

import static com.studentplanner.studentplanner.utils.CalendarUtils.getSelectedDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.studentplanner.studentplanner.addActivities.AddClassesActivity;
import com.studentplanner.studentplanner.addActivities.AddCourseworkActivity;
import com.studentplanner.studentplanner.tables.ClassTable;
import com.studentplanner.studentplanner.tables.CourseworkTable;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

import java.time.DayOfWeek;

public final class CalendarActions {
    private final ActivityResultLauncher<Intent> startForResult;
    private final Context context;

    public CalendarActions(ActivityResultLauncher<Intent> startForResult, Context context) {
        this.startForResult = startForResult;
        this.context = context;
    }

    public void addCourseworkAction() {
        if (Validation.isPastDate(getSelectedDate().toString())) {
            openActivity(AddCourseworkActivity.class);
            return;
        }

        // set deadline to selected calendar date
        startForResult.launch(courseworkIntent());
    }

    public void addClassAction() {
        DayOfWeek dow = getSelectedDate().getDayOfWeek();

        if (Helper.weekends().contains(dow)) {
            openActivity(AddClassesActivity.class);
            return;
        }
        // set class to selected class day
        startForResult.launch(classIntent());
    }

    private void openActivity(Class<? extends Activity> activityPageToOpen) {
        startForResult.launch(new Intent(context, activityPageToOpen));
    }


    private Intent classIntent() {
        Intent intent = new Intent(context, AddClassesActivity.class);
        int dow = getSelectedDate().getDayOfWeek().getValue();
        intent.putExtra(ClassTable.COLUMN_DOW, dow);
        return intent;
    }

    private Intent courseworkIntent() {
        Intent intent = new Intent(context, AddCourseworkActivity.class);
        intent.putExtra(CourseworkTable.COLUMN_DEADLINE, Helper.formatDate(CalendarUtils.getSelectedDate().toString()));
        return intent;
    }

}
