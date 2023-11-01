package com.studentplanner.studentplanner.editActivities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityEditClassesBinding;
import com.studentplanner.studentplanner.enums.TimePickerType;
import com.studentplanner.studentplanner.models.Classes;
import com.studentplanner.studentplanner.models.CustomTimePicker;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.tables.ClassTable;
import com.studentplanner.studentplanner.utils.BoundTimePickerDialog;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.Dropdown;
import com.studentplanner.studentplanner.utils.Helper;

import org.apache.commons.text.WordUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class EditClassesActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
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
    private ActivityEditClassesBinding binding;
    private BoundTimePickerDialog startTimePicker;
    private BoundTimePickerDialog endTimePicker;
    private CustomTimePicker startCustomTimePicker;
    private CustomTimePicker endCustomTimePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditClassesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);

        findFields();
        getModulesList();
        getSemesterList();
        setupFields();
        setUpTimePickers();

        binding.btnEditClass.setOnClickListener(v -> {

            if (db.updateClass(getClassDetails())) {
                Helper.longToastMessage(this, getString(R.string.class_updated));
                setResult(RESULT_OK);
                finish();
            }
        });


    }


    private void setUpTimePickers() {
        setStartTimePicker();
        setEndTimePicker();
    }

    private Classes getClassDetails() {
        return new Classes(
                getIntent().getIntExtra(ClassTable.COLUMN_ID, 0),
                selectedModuleID,
                selectedSemesterID,
                CalendarUtils.getDOWNumber(Helper.trimStr(txtDays)),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(Helper.trimStr(txtStartTime))),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(Helper.trimStr(txtEndTime))),
                Helper.trimStr(txtRoom),
                Helper.trimStr(txtClassType)

        );
    }

    private void findFields() {
        txtDays = binding.txtDay;
        txtModules = binding.txtModuleClasses;
        txtSemester = binding.txtSemesterClasses;
        txtClassType = binding.txtClassType;

        txtStartTime = binding.txtStartTime;
        txtEndTime = binding.txtEndTime;
        txtRoom = binding.txtRoom;
    }


    private void setupFields() {
        final String SELECTED_ID = ClassTable.COLUMN_ID;
        if (getIntent().hasExtra(SELECTED_ID)) {
            int id = getIntent().getIntExtra(SELECTED_ID, 0);
            var myClass = db.getSelectedClass(id);

            txtRoom.getEditText().setText(myClass.getRoom());
            Helper.getDays(txtDays, this);
            Helper.getStringArray(this, txtClassType, R.array.type_array);


            List<Integer> semesterIDList = db.getSemester().stream().map(Semester::semesterID).toList();
            List<Integer> moduleIDList = db.getModules().stream().map(Module::getModuleID).toList();

            txtSemester.setText(txtSemester.getAdapter().getItem(Dropdown.getDropDownID(myClass.getSemesterID(), semesterIDList)).toString(), false);
            txtModules.setText(txtModules.getAdapter().getItem(Dropdown.getDropDownID(myClass.getModuleID(), moduleIDList)).toString(), false);

            txtClassType.setText(txtClassType.getAdapter().getItem(Dropdown.getSelectedStringArrayNumber(myClass.getClassType(), this, R.array.type_array)).toString(), false);
            DayOfWeek classDay = DayOfWeek.of(myClass.getDow());

            txtDays.setText(
                    txtDays.getAdapter()
                            .getItem(Dropdown.setSelectedDayIndex(classDay)).toString(),
                    false);

            txtStartTime.setText(Helper.showFormattedDBTime(myClass.getStartTime(), this));
            txtEndTime.setText(Helper.showFormattedDBTime(myClass.getEndTime(), this));

            selectedModuleID = myClass.getModuleID();
            selectedSemesterID = myClass.getSemesterID();

            LocalTime startTime = myClass.getStartTime();
            LocalTime endTime = myClass.getStartTime();

            startCustomTimePicker = new CustomTimePicker(startTime.getHour(), startTime.getMinute());
            endCustomTimePicker = new CustomTimePicker(endTime.getHour(), endTime.getMinute());


        }
    }

    private void getModulesList() {

        final List<Module> list = db.getModules();
        txtModules.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Module.populateDropdown(list)));
        txtModules.setOnItemClickListener((parent, view, position, id) -> selectedModuleID = list.get(position).getModuleID());
    }

    private void getSemesterList() {

        final List<Semester> list = db.getSemester();
        txtSemester.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Semester.populateDropdown(list)));
        txtSemester.setOnItemClickListener((parent, view, position, id) -> selectedSemesterID = list.get(position).semesterID());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int ID = item.getItemId();
        if (ID == android.R.id.home) finish();
        if (ID == R.id.ic_delete) confirmDelete();
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage("You can't undo this").setCancelable(false)
                .setTitle(getString(R.string.delete_class_title))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    int id = getIntent().getIntExtra(ClassTable.COLUMN_ID, 0);
                    if (db.deleteRecord(ClassTable.TABLE_NAME, ClassTable.COLUMN_ID, id)) {
                        Helper.longToastMessage(this, WordUtils.capitalizeFully(getString(R.string.class_deleted)));
                        setResult(RESULT_OK);
                        finish();
                    }


                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();

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

}