package com.studentplanner.studentplanner.addActivities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityAddModuleBinding;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;

public class AddModuleActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextInputLayout txtModuleCode;
    private TextInputLayout txtModuleName;
    private Validation form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityAddModuleBinding binding = ActivityAddModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = DatabaseHelper.getInstance(this);
        form = new Validation(this, db);

        txtModuleCode = binding.txtModuleCode;
        txtModuleName = binding.txtModuleName;
        binding.btnAddModule.setOnClickListener(v -> handleAddModuleClick());

    }

    private void handleAddModuleClick() {
        Module module = getModuleDetails();
        if (!form.validateAddModuleForm(module)) return;
        if (db.addModule(module)) {
            Helper.longToastMessage(this, getString(R.string.module_added));
            setResult(RESULT_OK);
            finish();
        }

    }

    private Module getModuleDetails() {
        Module module = new Module(Helper.trimStr(txtModuleCode), Helper.trimStr(txtModuleName));
        module.setTxtModuleCode(txtModuleCode);
        module.setTxtModuleName(txtModuleName);
        return module;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }
}