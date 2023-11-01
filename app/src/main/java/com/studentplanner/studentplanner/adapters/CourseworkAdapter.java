package com.studentplanner.studentplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.CourseworkRowBinding;
import com.studentplanner.studentplanner.editActivities.EditCourseworkActivity;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.tables.CourseworkTable;
import com.studentplanner.studentplanner.viewholders.CourseworkViewHolder;

import java.util.Collections;
import java.util.List;


public class CourseworkAdapter extends RecyclerView.Adapter<CourseworkViewHolder> {

    private List<Coursework> list;
    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public CourseworkAdapter(List<Coursework> list, Context context, ActivityResultLauncher<Intent> startForResult) {
        this.list = Collections.unmodifiableList(list);
        this.context = context;
        this.startForResult = startForResult;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Coursework> filteredList) {
        list = filteredList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public CourseworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CourseworkViewHolder(CourseworkRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CourseworkViewHolder holder, int position) {

        holder.showDetails(list.get(position));
        holder.getLayout().setOnClickListener(v -> startForResult.launch(intent(position)));
    }

    private Intent intent(int position) {
        return new Intent(context, EditCourseworkActivity.class)
                .putExtra(CourseworkTable.COLUMN_ID, list.get(position).getCourseworkID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}