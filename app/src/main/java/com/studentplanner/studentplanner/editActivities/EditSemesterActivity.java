package com.studentplanner.studentplanner.editActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityEditSemesterBinding;
import com.studentplanner.studentplanner.enums.DatePickerType;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.tables.SemesterTable;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.DatePickerFragment;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

import java.time.LocalDate;

public class EditSemesterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private DatabaseHelper db;
    private AutoCompleteTextView txtStartDate;
    private AutoCompleteTextView txtEndDate;
    private DatePickerType type;
    private DatePickerFragment datePickerStart;
    private DatePickerFragment datePickerEnd;
    private TextInputLayout txtName;
    private Validation form;
    private ActivityEditSemesterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSemesterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);
        form = new Validation(this);
        findTextFields();
        setUpDatePickers();
        setupFields();
        binding.btnEditSemester.setOnClickListener(v -> {

            if (!form.validateSemesterForm(txtName)) return;
            if (db.updateSemester(getSemesterDetails())) {
                Helper.longToastMessage(this, getString(R.string.semester_updated));
                setResult(RESULT_OK);
                finish();
            }


        });
    }

    private Semester getSemesterDetails() {
        return new Semester(
                getIntent().getIntExtra(SemesterTable.COLUMN_ID, 0),
                Helper.trimStr(txtName),
                LocalDate.parse(Helper.convertFullDateToYYMMDD(txtStartDate.getEditableText().toString())),
                LocalDate.parse(Helper.convertFullDateToYYMMDD(txtEndDate.getEditableText().toString()))
        );

    }


    private void setupFields() {
        final String SELECTED_ID = SemesterTable.COLUMN_ID;
        if (getIntent().hasExtra(SELECTED_ID)) {

            final int ID = getIntent().getIntExtra(SELECTED_ID, 0);
            var semester = db.getSelectedSemester(ID);
            txtName.getEditText().setText(semester.name());
            txtStartDate.setText(Helper.formatDate(semester.start().toString()));
            txtEndDate.setText(Helper.formatDate(semester.end().toString()));
            txtName.getEditText().setText(semester.name());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int ID = item.getItemId();
        if (ID == R.id.ic_delete) confirmDelete();
        if (ID == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_semester_message))
                .setCancelable(false)
                .setTitle(getString(R.string.delete_semester_title))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    final int id = getIntent().getIntExtra(SemesterTable.COLUMN_ID, 0);
                    if (db.deleteRecord(SemesterTable.TABLE_NAME, SemesterTable.COLUMN_ID, id)) {
                        Helper.longToastMessage(this, getString(R.string.delete_semester));
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();
    }

    private void findTextFields() {
        txtName = binding.txtSemesterName;
        txtStartDate = binding.txtStartDate;
        txtEndDate = binding.txtEndDate;
    }

    private void setUpDatePickers() {
        setStartDatePicker();
        setEndDatePicker();

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

    private void createDatePickerConstraint(DatePickerFragment datePickerStart) {
        String endDate = Helper.convertFullDateToYYMMDD(txtEndDate.getEditableText().toString());
        String startDate = Helper.convertFullDateToYYMMDD(txtStartDate.getEditableText().toString());
        datePickerStart.setDatePickerStartEnd(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDateSet(DatePicker d, int year, int month, int day) {

        LocalDate date = Helper.formatDate(year, month, day);
        String formattedDate = Helper.formatDate(String.valueOf(date));
        switch (type) {
            case START_DATE -> txtStartDate.setText(formattedDate);
            case END_DATE -> txtEndDate.setText(formattedDate);
        }

    }
}