package com.studentplanner.studentplanner.addActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityAddModuleTeacherBinding;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.tables.ModuleTable;

import java.util.List;

public class AddModuleTeacherActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddModuleTeacherBinding binding = ActivityAddModuleTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        AutoCompleteTextView txtModules = binding.txtModuleAddTeacher;
        txtModules.setText(R.string.select_module);

        getModulesList(db.getModuleClassesAdd(), txtModules);
    }

    private void getModulesList(List<Module> moduleList, AutoCompleteTextView txtModules) {

        txtModules.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Module.populateDropdown(moduleList)));
        txtModules.setOnItemClickListener((parent, view, position, id) -> startForResult.launch(moduleIntent(moduleList.get(position).getModuleID())));
    }


    private Intent moduleIntent(int moduleID) {
        Intent intent = new Intent(this, AddModuleTeacherCheckboxActivity.class);
        intent.putExtra(ModuleTable.COLUMN_ID, moduleID);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }

}