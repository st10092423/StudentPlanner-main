package com.studentplanner.studentplanner.fragments;

import static android.app.Activity.RESULT_OK;
import static com.studentplanner.studentplanner.utils.CalendarUtils.daysInMonthArray;
import static com.studentplanner.studentplanner.utils.CalendarUtils.monthYearFromDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.activities.WeekViewActivity;
import com.studentplanner.studentplanner.adapters.CalendarAdapter;
import com.studentplanner.studentplanner.adapters.EventAdapter;
import com.studentplanner.studentplanner.databinding.FragmentCalendarBinding;
import com.studentplanner.studentplanner.interfaces.OnItemListener;
import com.studentplanner.studentplanner.models.CalendarActions;
import com.studentplanner.studentplanner.models.Event;
import com.studentplanner.studentplanner.models.EventData;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements OnItemListener {
    private Activity activity;
    private Context context;
    private TextView monthYearText;
    private RecyclerView recyclerView;
    private ListView eventListView;
    private FragmentCalendarBinding binding;
    private EventData eventData;
    private final LocalDate CURRENT_DATE = LocalDate.now();
    private CalendarActions calendarActions;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Event.getEventsList().clear();
            showCalendarEventData();
            setCalendarDate(CalendarUtils.getSelectedDate());
        }
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    public CalendarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initFragment() {
        activity = getActivity();
        context = getContext();
        activity.setTitle(context.getString(R.string.my_calendar));
        setHasOptionsMenu(true);

    }

    private void clearEventStatus() {
        if (!Event.getEventsList().isEmpty()) Event.getEventsList().clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initFragment();
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        eventData = new EventData(DatabaseHelper.getInstance(context));
        calendarActions = new CalendarActions(startForResult, context);
        initWidgets();
        buttons();
        clearEventStatus();
        showCalendarEventData();
        setCalendarDate(CURRENT_DATE);
        return binding.getRoot();
    }

    private void buttons() {
        binding.btNextMonthAction.setOnClickListener(v -> nextMonthAction());
        binding.btnPreviousMonthAction.setOnClickListener(v -> previousMonthAction());
        binding.btnTodayAction.setOnClickListener(v -> setCalendarDate(CURRENT_DATE));

    }

    private void showCalendarEventData() {
        List<Event> dailyEvents = Event.eventsForDate(CalendarUtils.getSelectedDate());
        eventListView.setAdapter(new EventAdapter(context, dailyEvents, startForResult));
        getEventsFromDB();
    }


    private void setCalendarDate(LocalDate date) {
        CalendarUtils.setSelectedDate(date);
        setMonthView();
    }

    public void getEventsFromDB() {
        eventData.getCourseworkDetails();
        eventData.getClassDetails();
    }

    public void previousMonthAction() {
        CalendarUtils.setSelectedDate(CalendarUtils.getSelectedDate().minusMonths(1));
        setMonthView();
    }

    public void nextMonthAction() {
        CalendarUtils.setSelectedDate(CalendarUtils.getSelectedDate().plusMonths(1));
        setMonthView();
    }


    private void initWidgets() {
        recyclerView = binding.recyclerView;
        monthYearText = binding.monthYearTV;
        eventListView = binding.eventListView;

    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.getSelectedDate()));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.getSelectedDate());
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        recyclerView.setAdapter(new CalendarAdapter(daysInMonth, this));
        CalendarUtils.setEventAdapter(eventListView, context, startForResult);
    }


    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.setSelectedDate(date);
        setMonthView();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.classes_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_coursework_action) calendarActions.addCourseworkAction();
        if (id == R.id.add_class_action) calendarActions.addClassAction();
        if (id == R.id.action_week_view) {
            startForResult.launch(new Intent(activity, WeekViewActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
