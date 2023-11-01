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
import com.studentplanner.studentplanner.adapters.SemesterAdapter;
import com.studentplanner.studentplanner.addActivities.AddSemesterActivity;
import com.studentplanner.studentplanner.databinding.FragmentSemesterBinding;
import com.studentplanner.studentplanner.models.Search;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.utils.EmptyData;
import com.studentplanner.studentplanner.utils.Helper;

import java.util.Collections;
import java.util.List;


public class SemesterFragment extends Fragment {
    private Context context;
    private Activity activity;

    private RecyclerView recyclerView;
    private SemesterAdapter adapter;
    private List<Semester> list;
    private EmptyData emptyData;

    private FragmentSemesterBinding binding;
    private DatabaseHelper db;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) getSemester();

    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    private List<Semester> getList() {
        return Collections.unmodifiableList(db.getSemester());
    }

    public SemesterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initFragment();
        binding = FragmentSemesterBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;
        binding.fabAdd.setOnClickListener(v -> startForResult.launch(new Intent(activity, AddSemesterActivity.class)));

        emptyData = new EmptyData(binding.emptyImage, binding.emptyText);
        db = DatabaseHelper.getInstance(context);

        getSemester();

        return binding.getRoot();
    }

    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(context.getString(R.string.my_semesters));
        setHasOptionsMenu(true);

    }

    private void getSemester() {
        list = getList();
        buildRecyclerView();
    }

    private void buildRecyclerView() {
        emptyData.emptyResultStatus(list.isEmpty());
        adapter = new SemesterAdapter(list, context, startForResult);
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

        List<Semester> filteredList = (List<Semester>) Search.textSearch(getList(), text);
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