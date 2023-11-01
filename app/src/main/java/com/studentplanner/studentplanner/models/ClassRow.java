package com.studentplanner.studentplanner.models;

import android.content.Context;
import android.widget.TextView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.EventRowBinding;
import com.studentplanner.studentplanner.utils.Helper;

import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.Objects;

public final class ClassRow {
    private final Context context;
    private final DatabaseHelper db;

    private final TextView lblTeachers;
    private final TextView lblClassTitle;
    private final TextView lblRoom;
    private final TextView lblStartTime;
    private final TextView lblEndTime;
    private final TextView lblType;

    public ClassRow(EventRowBinding binding, Context context) {
        this.context = context;
        db = DatabaseHelper.getInstance(context);
        lblTeachers = binding.tvClassTeachers;
        lblClassTitle =  binding.tvClassTitle;
        lblRoom = binding.tvRoom;
        lblStartTime = binding.tvStartTime;
        lblEndTime = binding.tvEndTime;
        lblType = binding.tvClassType;
    }
    public void setDetails(final Classes classes){
        final int moduleID = classes.getModuleID();
        List<ModuleTeacher> moduleTeacherList = db.getModuleTeachers();
        final String teachers = Helper.moduleIDExistsInModuleTeacher(moduleTeacherList, classes.getModuleID())
                ? Helper.getSnippet(WordUtils.capitalizeFully(Helper.getTeachersForSelectedModule(context, moduleID)), 35)
                : context.getString(R.string.no_teacher_assigned);

        lblType.setText(classes.getClassType());
        lblTeachers.setText(teachers);
        lblClassTitle.setText(Objects.requireNonNull(db.getSelectedModule(moduleID)).getModuleName());
        final String room = classes.getRoom().isEmpty() ? context.getString(R.string.no_room_assigned) : classes.getRoom();
        lblRoom.setText(room);
        lblStartTime.setText(Helper.formatTimeShort(classes.getStartTime().toString()));
        lblEndTime.setText(Helper.formatTimeShort(classes.getEndTime().toString()));
    }
}
