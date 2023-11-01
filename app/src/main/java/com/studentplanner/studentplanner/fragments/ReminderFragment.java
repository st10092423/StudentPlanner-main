package com.studentplanner.studentplanner.fragments;

import static android.app.Activity.RESULT_OK;

import static com.studentplanner.studentplanner.models.Coursework.sortList;

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
import com.studentplanner.studentplanner.adapters.CourseworkAdapter;
import com.studentplanner.studentplanner.databinding.FragmentReminderBinding;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.models.Search;
import com.studentplanner.studentplanner.utils.EmptyData;
import com.studentplanner.studentplanner.utils.Helper;

import java.util.List;


public class ReminderFragment extends Fragment {
    private Context context;
    private Activity activity;
    private EmptyData emptyData;

    private RecyclerView recyclerView;
    private CourseworkAdapter adapter;
    private List<Coursework> list;
    private FragmentReminderBinding binding;
    private DatabaseHelper db;

    private void activityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) getReminders();
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResult);

    public ReminderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initFragment();
        binding = FragmentReminderBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;
        emptyData = new EmptyData(binding.emptyImage, binding.emptyText);
        db = DatabaseHelper.getInstance(context);

        if (!db.getUpComingCourseworkByMonth().isEmpty()) {
            binding.emptyText.setText(getString(R.string.no_coursework_found));
        }

        getReminders();
        return binding.getRoot();
    }

    private void getReminders() {
        list = sortList(getList());
        buildRecyclerView();
    }

    private List<Coursework> getList() {
        return db.getUpComingCourseworkByMonth();
    }


    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(context.getString(R.string.my_reminders));
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
                    filter(newText);
                    return false;
                }
            });

        }
    }


    private void filter(String text) {

        List<Coursework> filteredList = (List<Coursework>) Search.textSearch(getList(), text);
        sortList(filteredList);
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