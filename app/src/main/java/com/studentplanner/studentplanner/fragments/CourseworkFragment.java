package com.studentplanner.studentplanner.fragments;

import static android.app.Activity.RESULT_OK;
import static com.studentplanner.studentplanner.models.Coursework.sortList;
import static com.studentplanner.studentplanner.utils.Dropdown.getSpinnerText;
import static com.studentplanner.studentplanner.utils.Dropdown.setDefaultSpinnerPosition;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.studentplanner.studentplanner.adapters.CourseworkAdapter;
import com.studentplanner.studentplanner.addActivities.AddCourseworkActivity;
import com.studentplanner.studentplanner.databinding.FragmentCourseworkBinding;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.models.SearchCoursework;
import com.studentplanner.studentplanner.utils.EmptyData;
import com.studentplanner.studentplanner.utils.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class CourseworkFragment extends Fragment {
    private SearchCoursework search;
    private Spinner txtPriority;
    private Spinner txtCompletionStatus;

    private Context context;
    private Activity activity;

    private RecyclerView recyclerView;
    private CourseworkAdapter adapter;
    private List<Coursework> list;
    private DatabaseHelper db;
    private FragmentCourseworkBinding binding;

    private EmptyData emptyData;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) getCoursework();
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    @Override
    public void onResume() {
        super.onResume();
        if (Helper.changeStatus) {
            getCoursework();
            Helper.changeStatus = false;

        }

    }

    public CourseworkFragment() {
    }

    private void setCompletionDropdown() {
        setSpinnerAdapter(txtCompletionStatus, List.of(context.getResources().getStringArray(R.array.completion_array_search)));
    }

    private void setPriorityDropdown() {
        Deque<String> deque = new LinkedList<>(Arrays.asList(context.getResources().getStringArray(R.array.priority_array)));
        deque.addFirst(context.getString(R.string.any_priority));
        List<String> priorityArray = new ArrayList<>(deque);
        setSpinnerAdapter(txtPriority, priorityArray);

    }

    private void setSpinnerAdapter(Spinner spinner, List<String> list) {
        spinner.setAdapter(new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_dropdown_item,
                list
        ));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initFragment();
        binding = FragmentCourseworkBinding.inflate(inflater, container, false);

        txtPriority = binding.txtPriority;
        txtCompletionStatus = binding.txtCompletionStatus;
        setPriorityDropdown();
        setCompletionDropdown();

        setDefaultSpinnerPosition(txtPriority, txtCompletionStatus);

        prioritySpinnerChanged();
        CompletionStatusSpinnerChanged();
        binding.fabAdd.setOnClickListener(v -> Helper.goToActivity(activity, AddCourseworkActivity.class));

        recyclerView = binding.recyclerView;

        db = DatabaseHelper.getInstance(context);

        emptyData = new EmptyData(binding.emptyImage, binding.emptyText);
        getCoursework();

        return binding.getRoot();
    }


    private void prioritySpinnerChanged() {

        txtPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search.setPriority(getSpinnerText(txtPriority, position));
                List<Coursework> filteredList = search.filterResults();
                checkEmptyResults(filteredList);
                adapter.filterList(filteredList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void CompletionStatusSpinnerChanged() {
        txtCompletionStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    search.setDefaultStatus(true);
                    List<Coursework> filteredList = search.filterResults();
                    checkEmptyResults(filteredList);
                    adapter.filterList(search.filterResults());
                    return;
                }
                search.setDefaultStatus(false);
                boolean isCompleted = getString(R.string.completed).equalsIgnoreCase(getSpinnerText(txtCompletionStatus, position));
                search.setCompleted(isCompleted);
                List<Coursework> filteredList = search.filterResults();
                checkEmptyResults(filteredList);
                adapter.filterList(filteredList);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCoursework() {
        list = sortList(db.getCoursework());
        search = new SearchCoursework(context, list);
        buildRecyclerView();
    }



    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(context.getString(R.string.my_coursework));
        setHasOptionsMenu(true);
    }

    private void buildRecyclerView() {
        emptyData.emptyResultStatus(list.isEmpty());
        adapter = new CourseworkAdapter(list, context, startForResult);
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
                    filterTitle(newText);
                    return false;
                }
            });

        }
    }


    private void filterTitle(String title) {
        search.setTitle(title);
        List<Coursework> filteredList = search.filterResults();
        checkEmptyResults(filteredList);
        adapter.filterList(filteredList);


    }

    private void checkEmptyResults(List<Coursework> filteredList) {
        if (filteredList.isEmpty()) {
            Helper.shortToastMessage(context, context.getString(R.string.no_data_found));
            emptyData.emptyResultStatus(true);
            return;

        }
        emptyData.emptyResultStatus(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}