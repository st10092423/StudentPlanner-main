package com.studentplanner.studentplanner.editActivities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityEditModuleBinding;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.tables.ModuleTable;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

public class EditModuleActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextInputLayout txtModuleCode;
    private TextInputLayout txtModuleName;
    private Validation form;
    private String excludedModuleCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEditModuleBinding binding = ActivityEditModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);
        txtModuleCode = binding.txtModuleCode;
        txtModuleName = binding.txtModuleName;
        form = new Validation(this, db);

        setupFields();
        binding.btnEditModule.setOnClickListener(v -> {
            if (form.validateEditModuleForm(new Module(txtModuleCode, txtModuleName), excludedModuleCode)) {
                if (db.updateModule(getModuleDetails())) {
                    Helper.longToastMessage(this, getString(R.string.module_updated));
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private Module getModuleDetails() {
        return new Module(
                getIntent().getIntExtra(ModuleTable.COLUMN_ID, 0),
                Helper.trimStr(txtModuleCode),
                Helper.trimStr(txtModuleName));
    }

    private void setupFields() {
        final String SELECTED_ID = ModuleTable.COLUMN_ID;

        if (getIntent().hasExtra(SELECTED_ID)) {
            int id = getIntent().getIntExtra(SELECTED_ID, 0);
            var module = db.getSelectedModule(id);
            txtModuleCode.getEditText().setText(module.getModuleCode());
            txtModuleName.getEditText().setText(module.getModuleName());
            excludedModuleCode = module.getModuleCode();

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
        if (ID == android.R.id.home) finish();
        if (ID == R.id.ic_delete) confirmDelete();
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_module_message))
                .setCancelable(false)
                .setTitle(getString(R.string.delete_module))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    int id = getIntent().getIntExtra(ModuleTable.COLUMN_ID, 0);
                    if (db.deleteRecord(ModuleTable.TABLE_NAME, ModuleTable.COLUMN_ID, id)) {
                        setResult(RESULT_OK);

                        Helper.longToastMessage(this, getString(R.string.module_deleted));
                        finish();
                    }


                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel()).create().show();
    }

}
