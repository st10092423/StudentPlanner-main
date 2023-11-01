package com.studentplanner.studentplanner.addActivities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.models.ModuleTeacher;
import com.studentplanner.studentplanner.models.Teacher;
import com.studentplanner.studentplanner.models.User;
import com.studentplanner.studentplanner.tables.ModuleTable;
import com.studentplanner.studentplanner.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class AddModuleTeacherCheckboxActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        db = DatabaseHelper.getInstance(this);
        setActivityTitle();
        String[] myTeachers = Helper.convertListStringToStringArray(getTeacherNames(db.getTeachers()));

        listView = findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                myTeachers
        ));
    }

    private void setActivityTitle() {
        final String SELECTED_ID = ModuleTable.COLUMN_ID;
        if (getIntent().hasExtra(SELECTED_ID)) {
            int id = getIntent().getIntExtra(SELECTED_ID, 0);
            setTitle(db.getSelectedModule(id).getModuleName());

        }
    }

    private List<String> getTeacherNames(List<Teacher> teachers) {
        return teachers.stream().map(User::getName).toList();
    }

    private List<Integer> getSelectedTeacherIDList() {

        List<Integer> selectedTeacherIds = new ArrayList<>();
        List<Teacher> teacherList = db.getTeachers();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                selectedTeacherIds.add(teacherList.get(i).getUserID());
            }
        }

        return selectedTeacherIds;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkbox_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_done) confirmSelection();
        return super.onOptionsItemSelected(item);
    }

    private void confirmSelection() {
        List<Integer> teacherIDList = getSelectedTeacherIDList();
        if (teacherIDList.isEmpty()) {
            Helper.shortToastMessage(this, getString(R.string.select_teacher));
            return;
        }

        final String SELECTED_ID = ModuleTable.COLUMN_ID;
        int moduleID = getIntent().getIntExtra(SELECTED_ID, 0);
        if (db.addModuleTeacher(new ModuleTeacher(moduleID, teacherIDList))) {
            Helper.longToastMessage(this, "Teacher Added for " + db.getSelectedModule(moduleID).getModuleDetails());
            setResult(RESULT_OK);
            finish();
            return;
        }
        Helper.longToastMessage(this, getString(R.string.module_teacher_error));

    }
}