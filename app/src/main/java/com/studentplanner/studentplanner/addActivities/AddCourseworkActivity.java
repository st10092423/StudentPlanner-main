package com.studentplanner.studentplanner.addActivities;

import static com.studentplanner.studentplanner.utils.Helper.deadlineSetup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityAddCourseworkBinding;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.models.CustomTimePicker;
import com.studentplanner.studentplanner.models.ImageHandler;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.tables.CourseworkTable;
import com.studentplanner.studentplanner.utils.BoundTimePickerDialog;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.DatePickerFragment;
import com.studentplanner.studentplanner.utils.Dropdown;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddCourseworkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, EasyPermissions.PermissionCallbacks {
    private final int STORAGE_PERMISSION_CODE = 1;
    private final LocalTime DEFAULT_DEADLINE_TIME = LocalTime.now().plusHours(1);
    private final CustomTimePicker deadlineCustomTimePicker = new CustomTimePicker(DEFAULT_DEADLINE_TIME.getHour(), DEFAULT_DEADLINE_TIME.getMinute());

    private AutoCompleteTextView txtPriority;
    private AutoCompleteTextView txtModules;

    private AutoCompleteTextView txtDeadline;
    private AutoCompleteTextView txtDeadlineTime;
    private TextInputLayout txtTitle;
    private TextInputLayout txtDescription;
    private int selectedModuleID;
    private DatabaseHelper db;
    private BoundTimePickerDialog deadlineTimePicker;
    private Validation form;
    private ActivityAddCourseworkBinding binding;
    private ImageView courseworkImage;
    private Bitmap imageToStore;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            try {

                imageToStore = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), result.getData().getData()));
                courseworkImage.setImageBitmap(imageToStore);
                binding.btnRemovePicture.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                Log.d("ERROR", "There was an error uploading image " + e.getMessage());
            }

        }
    }

    private final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddCourseworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = DatabaseHelper.getInstance(this);
        form = new Validation(this);

        initFields();
        setTimePicker();
        courseworkImage.setOnClickListener(v -> openFilesApp());
        txtDeadlineTime.setText(Helper.showFormattedDBTime(LocalTime.now().plusHours(1), this));
        Dropdown.getStringArray(txtPriority, this, R.array.priority_array);
        txtDeadline.setText(Helper.formatDate(CalendarUtils.getCurrentDate().toString()));

        if (getIntent().getStringExtra(CourseworkTable.COLUMN_DEADLINE) != null) {
            txtDeadline.setText(getIntent().getStringExtra(CourseworkTable.COLUMN_DEADLINE));

        }

        setupDatePicker();
        getModulesList();

        binding.titleTextInputEditText.setOnKeyListener((v, keyCode, event) -> {
            Helper.characterCounter(txtTitle, this);
            return false;
        });

        binding.btnAddCoursework.setOnClickListener(v -> handleAddButton());
        binding.btnRemovePicture.setOnClickListener(v -> handleRemoveClick());


    }

    private void initFields() {
        txtPriority = binding.txtPriority;
        txtDeadline = binding.txtDeadline;
        txtDeadlineTime = binding.txtDeadlineTime;
        txtModules = binding.txtModule;
        txtModules.setText(R.string.select_module);
        txtPriority.setText(getString(R.string.select_priority));
        txtTitle = binding.txtTitle;
        txtDescription = binding.txtDescription;
        courseworkImage = binding.imgCoursework;

    }


    private void handleAddButton() {
        if (!form.validateAddCourseworkForm(getCourseworkErrorFields())) return;
        if (db.addCoursework(getCourseworkDetails())) {
            Helper.changeStatus = true;
            Helper.longToastMessage(this, getString(R.string.coursework_added));
            setResult(RESULT_OK);
            finish();
        }


    }

    private void handleRemoveClick() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_image_title))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    imageToStore = null;
                    courseworkImage.setImageResource(R.drawable.ic_placeholder_image);
                    binding.btnRemovePicture.setVisibility(View.GONE);
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();
    }

    @AfterPermissionGranted(STORAGE_PERMISSION_CODE)
    private void openFilesApp() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImageHandler.openImageGallery(imageActivityResultLauncher);
            return;
        }
        EasyPermissions.requestPermissions(this, getString(R.string.permissions_rationale), STORAGE_PERMISSION_CODE, perms);
    }



    private Coursework getCourseworkErrorFields() {
        TextInputLayout txtModuleError = binding.txtModuleError;
        TextInputLayout errorPriority = binding.txtPriorityError;
        TextInputLayout timeError = binding.txtDeadlineTimeError;
        Coursework coursework = new Coursework(txtTitle, txtModuleError, errorPriority);
        coursework.setTxtDeadline(txtDeadline);
        coursework.setTxtDeadlineTime(txtDeadlineTime);
        coursework.setTxtDeadlineTimeError(timeError);
        return coursework;
    }

    private Coursework getCourseworkDetails() {

        final Coursework coursework = new Coursework(
                selectedModuleID,
                Helper.trimStr(txtTitle),
                Helper.trimStr(txtDescription),
                Helper.trimStr(txtPriority),
                LocalDate.parse(Helper.convertFullDateToYYMMDD(Helper.trimStr(txtDeadline))),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(txtDeadlineTime.getText().toString()))
        );

        if (imageToStore != null) coursework.setImage(imageToStore);

        return coursework;


    }

    private void getModulesList() {

        final List<Module> moduleList = db.getModules();
        txtModules.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Module.populateDropdown(moduleList)));
        txtModules.setOnItemClickListener((parent, view, position, id) -> selectedModuleID = moduleList.get(position).getModuleID());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDateSet(DatePicker d, int year, int month, int day) {
        LocalDate selectedDate = Helper.formatDate(year, month, day);
        txtDeadline.setText(Helper.formatDate(String.valueOf(selectedDate)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDatePicker() {
        txtDeadline.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                DatePickerFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "datePicker");
                datepicker.setMinDateToToday();
                CalendarUtils.setSelectedDate(datepicker, txtDeadline);
            }
            return false;
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setTimePicker() {
        txtDeadlineTime.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                deadlineTimePicker = new BoundTimePickerDialog(this, this, deadlineCustomTimePicker.getSelectedHour(), deadlineCustomTimePicker.getSelectedMinute());
                String deadlineDate = Helper.convertFullDateToYYMMDD(Helper.trimStr(txtDeadline));
                LocalDate deadline = LocalDate.parse(deadlineDate);
                LocalDate today = CalendarUtils.getCurrentDate();
                LocalDate date = deadline.isEqual(today) ? today : deadline;
                deadlineSetup(deadlineTimePicker, date);
                deadlineTimePicker.show();
            }

            return false;
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        deadlineCustomTimePicker.setSelectedHour(selectedHour);
        deadlineCustomTimePicker.setSelectedMinute(selectedMinute);
        final String selectedTime = String.format(Locale.getDefault(), getString(R.string.time_format_database), selectedHour, selectedMinute);
        txtDeadlineTime.setText(Helper.formatTime(selectedTime));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}