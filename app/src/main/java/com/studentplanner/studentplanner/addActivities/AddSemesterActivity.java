package com.studentplanner.studentplanner.addActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityAddSemesterBinding;
import com.studentplanner.studentplanner.enums.DatePickerType;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.DatePickerFragment;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

import java.time.LocalDate;

public class AddSemesterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextInputLayout txtName;
    private AutoCompleteTextView txtStartDate;
    private AutoCompleteTextView txtEndDate;
    private DatePickerType type;
    private DatePickerFragment datePickerStart;
    private DatePickerFragment datePickerEnd;
    private ActivityAddSemesterBinding binding;
    private DatabaseHelper db;
    Validation form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSemesterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DatabaseHelper.getInstance(this);
        form = new Validation(this);

        findTextFields();
        setUpDatePickers();
        setDefaultValues();

        binding.btnAddSemester.setOnClickListener(v -> {
            if (!form.validateSemesterForm(txtName)) return;
            if (db.addSemester(getSemesterDetails())) {
                Helper.longToastMessage(this, getString(R.string.semester_added));
                setResult(RESULT_OK);
                finish();
            }


        });


    }

    private Semester getSemesterDetails() {
        String name = Helper.trimStr(txtName);
        LocalDate start = LocalDate.parse(Helper.convertFullDateToYYMMDD(txtStartDate.getEditableText().toString()));
        LocalDate end = LocalDate.parse(Helper.convertFullDateToYYMMDD(txtEndDate.getEditableText().toString()));
        return new Semester(name, start, end);
    }

    private void setUpDatePickers() {

        setStartDatePicker();
        setEndDatePicker();

    }

    private void setDefaultValues() {
        txtStartDate.setText(Helper.formatDate(LocalDate.now().toString()));
        txtEndDate.setText(Helper.formatDate(LocalDate.now().plusWeeks(3).toString()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStartDatePicker() {
        txtStartDate.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                type = DatePickerType.START_DATE;
                datePickerStart = new DatePickerFragment();
                datePickerStart.show(getSupportFragmentManager(), "datePickerStart");
                datePickerStart.setConstrainEndDate();
                createDatePickerConstraint(datePickerStart);
                CalendarUtils.setSelectedDate(datePickerStart, txtStartDate);

            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEndDatePicker() {
        txtEndDate.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                type = DatePickerType.END_DATE;
                datePickerEnd = new DatePickerFragment();
                datePickerEnd.show(getSupportFragmentManager(), "datePickerEnd");
                datePickerEnd.setConstrainStartDate();
                createDatePickerConstraint(datePickerEnd);
                CalendarUtils.setSelectedDate(datePickerEnd, txtEndDate);

            }
            return false;
        });
    }


    private void findTextFields() {
        txtName = binding.txtSemesterName;
        txtStartDate = binding.txtStartDate;
        txtEndDate = binding.txtEndDate;
    }

    private void createDatePickerConstraint(DatePickerFragment datePickerStart) {
        String endDate = Helper.convertFullDateToYYMMDD(txtEndDate.getEditableText().toString());
        String startDate = Helper.convertFullDateToYYMMDD(txtStartDate.getEditableText().toString());
        datePickerStart.setDatePickerStartEnd(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDateSet(DatePicker d, int year, int month, int day) {

        LocalDate date = Helper.formatDate(year, month, day);
        final String formattedDate = Helper.formatDate(String.valueOf(date));
        switch (type) {
            case START_DATE -> txtStartDate.setText(formattedDate);
            case END_DATE -> txtEndDate.setText(formattedDate);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }
}