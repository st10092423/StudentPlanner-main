package com.studentplanner.studentplanner.models;

import static com.studentplanner.studentplanner.utils.CompletionStatus.COMPLETED;
import static com.studentplanner.studentplanner.utils.CompletionStatus.NOT_COMPLETED;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.EventRowBinding;
import com.studentplanner.studentplanner.utils.Helper;

import org.apache.commons.text.WordUtils;

public final class CourseworkRow {
    private final TextView title;
    private final TextView lblModule;
    private final TextView priority;
    private final TextView time;
    private final TextView completionStatus;
    private final Context context;
    private final DatabaseHelper db;

    public CourseworkRow(EventRowBinding binding, Context context){
        title = binding.tvCwTitle;
        lblModule = binding.tvCwModule;
        priority = binding.tvCwPriority;
        time = binding.tvCwTime;
        completionStatus = binding.tvCwCompleted;
        this.context = context;
        db = DatabaseHelper.getInstance(context);
    }
    public void setDetails(Coursework coursework){
        String priorityLevel = coursework.getPriority();
        Module module = db.getSelectedModule(coursework.getModuleID());
        title.setText(Helper.getSnippet(WordUtils.capitalizeFully(coursework.getTitle()), 20));
        lblModule.setText(Helper.getSnippet(module.getModuleDetails(), 30));
        priority.setText(priorityLevel);
        priority.setTextColor(Helper.getPriorityColour(priorityLevel, context));
        time.setText(Helper.formatTimeShort(coursework.getDeadlineTime().toString()));

        completionStatus.setText(coursework.isCompleted() ? COMPLETED : NOT_COMPLETED);
        completionStatus.setTextColor(coursework.isCompleted() ? context.getColor(R.color.dark_green) : Color.RED);

    }
}