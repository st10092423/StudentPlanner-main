package com.studentplanner.studentplanner.activities;

import static com.studentplanner.studentplanner.utils.CalendarUtils.daysInWeekArray;
import static com.studentplanner.studentplanner.utils.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.adapters.CalendarAdapter;
import com.studentplanner.studentplanner.adapters.EventAdapter;
import com.studentplanner.studentplanner.databinding.ActivityWeekViewBinding;
import com.studentplanner.studentplanner.interfaces.OnItemListener;
import com.studentplanner.studentplanner.models.CalendarActions;
import com.studentplanner.studentplanner.models.Event;
import com.studentplanner.studentplanner.models.EventData;
import com.studentplanner.studentplanner.utils.CalendarUtils;

import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class WeekViewActivity extends AppCompatActivity implements OnItemListener {
    private TextView monthYearText;
    private RecyclerView recyclerView;
    private ListView eventListView;
    private ActivityWeekViewBinding binding;
    private EventData eventData;
    private CalendarActions calendarActions;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Event.getEventsList().clear();
            showCalendarEventData();
            setWeekView();
        }

    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(WordUtils.capitalizeFully(getString(R.string.week_view_calendar)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventData = new EventData(DatabaseHelper.getInstance(this));
        calendarActions = new CalendarActions(startForResult, this);

        binding = ActivityWeekViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initWidgets();
        setWeekView();
    }


    private void initWidgets() {

        recyclerView = binding.recyclerView;
        monthYearText = binding.monthYearTV;
        eventListView = binding.eventListView;
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.getSelectedDate()));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.getSelectedDate());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerView.setAdapter(new CalendarAdapter(days, this));
        CalendarUtils.setEventAdapter(eventListView, this, startForResult);
    }


    public void previousWeekAction(View view) {
        CalendarUtils.setSelectedDate(CalendarUtils.getSelectedDate().minusWeeks(1));
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.setSelectedDate(CalendarUtils.getSelectedDate().plusWeeks(1));
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.setSelectedDate(date);
        setWeekView();
    }

    public void getEventsFromDB() {
        eventData.getCourseworkDetails();
        eventData.getClassDetails();
    }


    private void showCalendarEventData() {
        List<Event> dailyEvents = Event.eventsForDate(CalendarUtils.getSelectedDate());
        eventListView.setAdapter(new EventAdapter(this, dailyEvents, startForResult));
        getEventsFromDB();
    }


    public void todayAction(View view) {
        CalendarUtils.setSelectedDate(CalendarUtils.getCurrentDate());
        setWeekView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classes_menu, menu);
        changeCalendarIcon(menu);
        return true;
    }

    private void changeCalendarIcon(Menu menu) {
        MenuItem weekView = menu.findItem(R.id.action_week_view);
        weekView.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_calendar_view));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_coursework_action) calendarActions.addCourseworkAction();
        if (id == R.id.add_class_action) calendarActions.addClassAction();
        if (id == android.R.id.home | id == R.id.action_week_view) goBack();
        return super.onOptionsItemSelected(item);

    }


    private void goBack() {
        CalendarUtils.setEventAdapter(eventListView, this, startForResult);
        finish();
    }

}