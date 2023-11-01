package com.studentplanner.studentplanner.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.adapters.ModuleTeacherAdapter;
import com.studentplanner.studentplanner.addActivities.AddModuleTeacherActivity;
import com.studentplanner.studentplanner.databinding.FragmentModuleTeacherBinding;
import com.studentplanner.studentplanner.models.ModuleTeacher;
import com.studentplanner.studentplanner.utils.EmptyData;
import com.studentplanner.studentplanner.utils.Helper;

import org.apache.commons.text.WordUtils;

import java.util.List;


public class ModuleTeacherFragment extends Fragment {
    private Context context;
    private Activity activity;

    private RecyclerView recyclerView;
    private ModuleTeacherAdapter adapter;
    private List<ModuleTeacher> list;
    private FragmentModuleTeacherBinding binding;
    private DatabaseHelper db;

    private EmptyData emptyData;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) getModuleTeacher();
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);


    public ModuleTeacherFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initFragment();

        binding = FragmentModuleTeacherBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;

        binding.fabAdd.setOnClickListener(v -> startForResult.launch(new Intent(activity, AddModuleTeacherActivity.class)));
        emptyData = new EmptyData(binding.emptyImage, binding.emptyText);

        db = DatabaseHelper.getInstance(context);
        if (!getList().isEmpty()) {
            binding.emptyText.setText(getString(R.string.no_module_teacher));
        }

        getModuleTeacher();
        return binding.getRoot();
    }

    private void getModuleTeacher() {
        list = getList();
        buildRecyclerView();

    }

    private List<ModuleTeacher> getList() {
        return db.getModuleTeachers();
    }


    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(WordUtils.capitalizeFully(context.getString(R.string.my_teachers_modules)));
        setHasOptionsMenu(true);

    }


    private void buildRecyclerView() {
        emptyData.emptyResultStatus(list.isEmpty());
        adapter = new ModuleTeacherAdapter(list, context, startForResult);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {


        if (!list.isEmpty()) {
            activity.getMenuInflater().inflate(R.menu.search_menu, menu);

            MenuItem searchItem = menu.findItem(R.id.actionSearch);
            SearchView searchView = (SearchView) searchItem.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return false;
                }
            });

        }
    }


    private void filter(String text) {

        List<Integer> moduleIdList = db.getModuleTeachersFiltered(text);
        List<ModuleTeacher> filteredList = ModuleTeacher.filterModuleTeachers(db.getModuleTeachers(), moduleIdList);
        adapter.filterList(filteredList);
        emptyData.emptyResultStatus(filteredList.isEmpty());
        if (filteredList.isEmpty()) {
            Helper.shortToastMessage(context, context.getString(R.string.no_data_found));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}