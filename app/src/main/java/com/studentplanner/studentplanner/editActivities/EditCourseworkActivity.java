package com.studentplanner.studentplanner.editActivities;

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
import android.view.Menu;
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

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityEditCourseworkBinding;
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

public class EditCourseworkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, EasyPermissions.PermissionCallbacks {
    private final int STORAGE_PERMISSION_CODE = 1;

    private CustomTimePicker deadlineCustomTimePicker;

    private DatabaseHelper db;
    private Validation form;
    private AutoCompleteTextView txtPriority;
    private AutoCompleteTextView txtModules;

    private AutoCompleteTextView txtDeadline;
    private AutoCompleteTextView txtDeadlineTime;
    private TextInputLayout txtTitle;
    private TextInputLayout txtDescription;
    private int selectedModuleID;
    private TextInputLayout txtDeadlineError;
    private MaterialCheckBox checkboxCompleted;

    private ActivityEditCourseworkBinding binding;
    private BoundTimePickerDialog deadlineTimePicker;
    private ImageView courseworkImage;
    private Bitmap imageToStore;
    private boolean deleteImage;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            try {
                imageToStore = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), result.getData().getData()));
                courseworkImage.setImageBitmap(imageToStore);
                binding.btnRemovePicture.setVisibility(View.VISIBLE);
                deleteImage = false;
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
        setTitle(R.string.edit_coursework);
        binding = ActivityEditCourseworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = DatabaseHelper.getInstance(this);
        form = new Validation(this);
        initFields();

        getModulesList();

        setupFields();
        setupDatePicker();
        setTimePicker();


        binding.btnEditCoursework.setOnClickListener(v -> btnEditCoursework());
        binding.btnRemovePicture.setOnClickListener(v -> btnRemovePicture());
        courseworkImage.setOnClickListener(v -> openFilesApp());
        binding.titleTextInputEditText.setOnKeyListener((v, keyCode, event) -> {
            Helper.characterCounter(txtTitle, getApplicationContext());
            return false;
        });


    }

    private void btnEditCoursework() {
        if (form.validateEditCourseworkForm(getCourseworkErrorFields())) {
            if (db.updateCoursework(getCourseworkDetails(), deleteImage)) {
                Helper.longToastMessage(this, getString(R.string.coursework_updated));
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private Coursework getCourseworkErrorFields() {
        Coursework coursework = new Coursework();
        coursework.setTxtDeadlineTime(txtDeadlineTime);
        coursework.setTxtDeadlineTimeError(binding.txtDeadlineTimeError);
        coursework.setTxtDeadline(txtDeadline);
        coursework.setTxtDeadlineError(txtDeadlineError);
        coursework.setTxtTitle(txtTitle);
        return coursework;
    }

    private void initFields() {
        txtPriority = binding.txtPriority;
        txtDeadline = binding.txtDeadline;
        txtDeadlineTime = binding.txtDeadlineTime;
        txtModules = binding.txtModule;
        txtTitle = binding.txtTitle;
        txtDescription = binding.txtDescription;
        txtDeadlineError = binding.txtDeadlineError;
        checkboxCompleted = binding.checkboxCompleted;
        courseworkImage = binding.imgCoursework;
        Dropdown.getStringArray(txtPriority, this, R.array.priority_array);

    }

    private void setupFields() {
        final String SELECTED_ID = CourseworkTable.COLUMN_ID;

        if (getIntent().hasExtra(SELECTED_ID)) {
            int id = getIntent().getIntExtra(SELECTED_ID, 0);

            List<Integer> moduleIDList = db.getModules().stream().map(Module::getModuleID).toList();

            var coursework = db.getSelectedCoursework(id);
            LocalTime deadlineTime = coursework.getDeadlineTime();

            txtTitle.getEditText().setText(coursework.getTitle());
            txtDescription.getEditText().setText(coursework.getDescription());
            txtPriority.setText(txtPriority.getAdapter().getItem(Dropdown.getSelectedStringArrayNumber(coursework.getPriority(), this, R.array.priority_array)).toString(), false);
            txtDeadline.setText(Helper.formatDate(coursework.getDeadline().toString()));
            txtDeadlineTime.setText(Helper.showFormattedDBTime(deadlineTime, this));
            txtModules.setText(txtModules.getAdapter().getItem(Dropdown.getDropDownID(coursework.getModuleID(), moduleIDList)).toString(), false);
            selectedModuleID = coursework.getModuleID();
            checkboxCompleted.setChecked(coursework.isCompleted());

            deadlineCustomTimePicker = new CustomTimePicker(deadlineTime.getHour(), deadlineTime.getMinute());
            showCourseworkImage(coursework.getByteImage());


        }

    }

    private void showCourseworkImage(byte[] image) {
        if (image == null) {
            courseworkImage.setImageResource(R.drawable.ic_placeholder_image);
            return;
        }
        courseworkImage.setImageBitmap(ImageHandler.decodeBitmapByteArray(image));
        binding.btnRemovePicture.setVisibility(View.VISIBLE);

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


    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        deadlineCustomTimePicker.setSelectedHour(selectedHour);
        deadlineCustomTimePicker.setSelectedMinute(selectedMinute);
        String selectedTime = String.format(Locale.getDefault(), getString(R.string.time_format_database), selectedHour, selectedMinute);
        txtDeadlineTime.setText(Helper.formatTime(selectedTime));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDateSet(DatePicker d, int year, int month, int day) {
        LocalDate date = Helper.formatDate(year, month, day);
        txtDeadline.setText(Helper.formatDate(String.valueOf(date)));
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

    private void getModulesList() {

        final List<Module> moduleList = db.getModules();
        txtModules.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Module.populateDropdown(moduleList)));
        txtModules.setOnItemClickListener((parent, view, position, id) -> selectedModuleID = moduleList.get(position).getModuleID());
    }


    private Coursework getCourseworkDetails() {

        Coursework coursework = new Coursework(
                getIntent().getIntExtra(CourseworkTable.COLUMN_ID, 0),
                selectedModuleID,
                Helper.trimStr(txtTitle),
                Helper.trimStr(txtDescription),
                Helper.trimStr(txtPriority),
                LocalDate.parse(Helper.convertFullDateToYYMMDD(Helper.trimStr(txtDeadline))),
                LocalTime.parse(Helper.convertFormattedTimeToDBFormat(txtDeadlineTime.getText().toString()))

        );

        if (imageToStore != null) coursework.setImage(imageToStore);
        coursework.setCompleted(checkboxCompleted.isChecked());
        return coursework;
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
                .setTitle(getString(R.string.delete_coursework_title))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    int id = getIntent().getIntExtra(CourseworkTable.COLUMN_ID, 0);
                    if (db.deleteRecord(CourseworkTable.TABLE_NAME, CourseworkTable.COLUMN_ID, id)) {
                        Helper.longToastMessage(this, getString(R.string.delete_coursework));
                        setResult(RESULT_OK);
                        finish();
                    }


                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();
    }


    private void btnRemovePicture() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_image_title))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    imageToStore = null;
                    deleteImage = true;
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

}