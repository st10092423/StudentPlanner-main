package com.studentplanner.studentplanner.addActivities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityAddClassesBinding;
import com.studentplanner.studentplanner.enums.TimePickerType;
import com.studentplanner.studentplanner.models.Classes;
import com.studentplanner.studentplanner.models.CustomTimePicker;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.tables.ClassTable;
import com.studentplanner.studentplanner.utils.BoundTimePickerDialog;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class AddClassesActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private AutoCompleteTextView txtDays;
    private AutoCompleteTextView txtModules;
    private AutoCompleteTextView txtSemester;
    private AutoCompleteTextView txtClassType;

    private AutoCompleteTextView txtStartTime;
    private AutoCompleteTextView txtEndTime;

    private int selectedModuleID;
    private int selectedSemesterID;
    private TimePickerType type;
    private TextInputLayout txtRoom;


    private DatabaseHelper db;
    private Validation form;

    private TextInputLayout txtDayError;
    private TextInputLayout txtSemesterError;
    private TextInputLayout txtModuleError;
    private TextInputLayout txtClassTypeError;
    private ActivityAddClassesBinding binding;

    private BoundTimePickerDialog startTimePicker;
    private BoundTimePickerDialog endTimePicker;
    private final CustomTimePicker startCustomTimePicker = new CustomTimePicker(LocalTime.now().getHour(), LocalTime.now().getMinute());
    private final CustomTimePicker endCustomTimePicker = new CustomTimePicker(LocalTime.now().getHour(), LocalTime.now().getMinute());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = ActivityAddClassesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAndSetFields();

        db = DatabaseHelper.getInstance(this);
        form = new Validation(this);

        setUpTimePickers();
        Helper.getDays(txtDays, this);

        if (getIntent().getIntExtra(ClassTable.COLUMN_DOW, 0) != 0) {

            int dow = getIntent().getIntExtra(ClassTable.COLUMN_DOW, 0) - 1;
            txtDays.setText(txtDays.getAdapter().getItem(dow).toString(), false);
        }

        getModulesList();
        getSemesterList();
        Helper.getStringArray(this, txtClassType, R.array.type_array);

        binding.btnAddClasses.setOnClickListener(v -> handleClick());
    }

    private void handleClick() {
        if (!form.validateAddClassForm(getErrorFields())) return;
        if (db.classExists(selectedModuleID, selectedSemesterID, Helper.trimStr(txtClassType))) {
            txtClassTypeError.setError(getString(R.string.class_exists_error));
            return;
        }
        txtClassTypeError.setError(null);

        if (db.addClass(getClassDetails())) {
            Helper.longToastMessage(this, getString(R.string.class_added));
            setResult(RESULT_OK);
            finish();
        }
    }

    private Classes getErrorFields() {
        Classes errorFields = new Classes(txtDayError, txtSemesterError, txtModuleError, txtClassTypeError);
        errorFields.setTxtStartTimeError(binding.txtStartTimeError);
        errorFields.setTxtEndTimeError(binding.txtEndTimeError);
        return errorFields;

    }

    private void initAndSetFields() {
        txtDayError = binding.txtDayError;
        txtSemesterError = binding.txtSemesterErrorClasses;
        txtModuleError = binding.txtModuleErrorClasses;
        txtClassTypeError = binding.txtClassTypeError;

        txtDays = binding.txtDay;
        txtModules = binding.txtModuleClasses;
        txtSemester = binding.txtSemesterClasses;
        txtClassType = binding.txtClassType;
        txtStartTime = binding.txtStartTime;
        txtEndTime = binding.txtEndTime;
        txtRoom = binding.txtRoom;

        txtDays.setText(getString(R.string.select_day));
        txtSemester.setText(getString(R.string.select_semester));
        txtModules.setText(getString(R.string.select_module));
        txtClassType.setText(getString(R.string.select_class_type));
        txtStartTime.setText(getString(R.string.select_start_time));
        txtEndTime.setText(getString(R.string.select_end_time));
    }

    private Classes getClassDetails() {
        return new Classes(
                selectedModuleID,
                selectedSemesterID,
                CalendarUtils.getDOWNumber(Helper.trimStr(txtDays)),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(Helper.trimStr(txtStartTime))),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(Helper.trimStr(txtEndTime))),
                Helper.trimStr(txtRoom),
                Helper.trimStr(txtClassType)
        );
    }


    private void getModulesList() {

        final List<Module> moduleList = db.getModules();
        txtModules.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Module.populateDropdown(moduleList)));
        txtModules.setOnItemClickListener((parent, view, position, id) -> selectedModuleID = moduleList.get(position).getModuleID());

    }


    private void getSemesterList() {
        final List<Semester> list = db.getSemester();
        txtSemester.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Semester.populateDropdown(list)));
        txtSemester.setOnItemClickListener((parent, view, position, id) -> selectedSemesterID = list.get(position).semesterID());

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        String selectedTime = String.format(Locale.getDefault(), getString(R.string.time_format_database), selectedHour, selectedMinute);
        String formattedTime = Helper.formatTime(selectedTime);
        switch (type) {
            case START_TIME -> {
                startCustomTimePicker.setSelectedHour(selectedHour);
                startCustomTimePicker.setSelectedMinute(selectedMinute);
                txtStartTime.setText(formattedTime);
            }
            case END_TIME -> {
                endCustomTimePicker.setSelectedHour(selectedHour);
                endCustomTimePicker.setSelectedMinute(selectedMinute);
                txtEndTime.setText(formattedTime);
            }
        }

    }

    private void setUpTimePickers() {
        setStartTimePicker();
        setEndTimePicker();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStartTimePicker() {
        txtStartTime.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                type = TimePickerType.START_TIME;
                startTimePicker = new BoundTimePickerDialog(this, this, startCustomTimePicker.getSelectedHour(), startCustomTimePicker.getSelectedMinute());

                if (!Helper.trimStr(txtEndTime).equals(getString(R.string.select_end_time))) {
                    LocalTime endTime = LocalTime.of(endCustomTimePicker.getSelectedHour(), endCustomTimePicker.getSelectedMinute());
                    startTimePicker.setMax(endTime.getHour(), endTime.getMinute());
                }
                startTimePicker.show();
            }

            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEndTimePicker() {
        txtEndTime.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                type = TimePickerType.END_TIME;
                endTimePicker = new BoundTimePickerDialog(this, this, endCustomTimePicker.getSelectedHour(), endCustomTimePicker.getSelectedMinute());

                if (!Helper.trimStr(txtStartTime).equals(getString(R.string.select_start_time))) {
                    LocalTime startTime = LocalTime.of(startCustomTimePicker.getSelectedHour(), startCustomTimePicker.getSelectedMinute());
                    endTimePicker.setMin(startTime.getHour(), startTime.getMinute());
                }
                endTimePicker.show();
            }

            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }
}