package com.studentplanner.studentplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.EventRowBinding;
import com.studentplanner.studentplanner.editActivities.EditClassesActivity;
import com.studentplanner.studentplanner.editActivities.EditCourseworkActivity;
import com.studentplanner.studentplanner.enums.EventType;
import com.studentplanner.studentplanner.models.ClassRow;
import com.studentplanner.studentplanner.models.CourseworkRow;
import com.studentplanner.studentplanner.models.Event;
import com.studentplanner.studentplanner.tables.ClassTable;
import com.studentplanner.studentplanner.tables.CourseworkTable;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public EventAdapter(@NonNull Context context, List<Event> events, ActivityResultLauncher<Intent> startForResult) {
        super(context, 0, events);
        this.context = context;
        this.startForResult = startForResult;
    }

    private void handleClick(Event event) {
        final int ID = event.getId();
        switch (event.getEventType()) {
            case COURSEWORK -> startForResult.launch(getCourseworkIntent(ID));
            case CLASSES -> startForResult.launch(getClassesIntent(ID));
        }

    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Event event = getItem(position);
        EventRowBinding binding = EventRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.mainLayout.setOnClickListener(v -> handleClick(event));
        showEventDetails(event, binding);
        return binding.getRoot();
    }


    private void showEventDetails(final Event event, EventRowBinding binding) {
        final ImageView classesIcon = binding.icClasses;
        final ImageView courseworkIcon = binding.icCoursework;

        final int eventIcon = getEventIcon(event.getEventType());

        classesIcon.setImageResource(eventIcon);
        courseworkIcon.setImageResource(eventIcon);

        final ConstraintLayout classLayout = binding.mainLayoutClasses;
        final ConstraintLayout courseworkLayout = binding.mainLayoutCoursework;

        final CourseworkRow courseworkRow = new CourseworkRow(binding, context);
        final ClassRow classRow = new ClassRow(binding, context);

        switch (event.getEventType()) {
            case COURSEWORK -> {
                classLayout.setVisibility(View.GONE);
                courseworkRow.setDetails(event.getCoursework());
            }

            case CLASSES -> {
                courseworkLayout.setVisibility(View.GONE);
                classRow.setDetails(event.getClasses());
            }
        }

    }


    private Intent getCourseworkIntent(final int id) {
        return new Intent(getContext(), EditCourseworkActivity.class)
                .putExtra(CourseworkTable.COLUMN_ID, id);

    }

    private Intent getClassesIntent(final int id) {
        return new Intent(getContext(), EditClassesActivity.class)
                .putExtra(ClassTable.COLUMN_ID, id);
    }

    private int getEventIcon(final EventType eventType) {
        return switch (eventType) {
            case COURSEWORK -> R.drawable.ic_coursework;
            case CLASSES -> R.drawable.ic_classes;
        };
    }
}


