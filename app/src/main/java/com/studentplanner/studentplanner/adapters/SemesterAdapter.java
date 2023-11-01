package com.studentplanner.studentplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.SemesterRowBinding;
import com.studentplanner.studentplanner.editActivities.EditSemesterActivity;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.tables.SemesterTable;
import com.studentplanner.studentplanner.viewholders.SemesterViewHolder;

import java.util.Collections;
import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterViewHolder> {

    private List<Semester> list;
    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public SemesterAdapter(List<Semester> list, Context context, ActivityResultLauncher<Intent> startForResult) {
        this.list = Collections.unmodifiableList(list);
        this.context = context;
        this.startForResult = startForResult;
    }

    public void filterList(List<Semester> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SemesterViewHolder(SemesterRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {

        holder.showDetails(list.get(position));
        holder.getLayout().setOnClickListener(v -> startForResult.launch(intent(position)));

    }

    private Intent intent(int position) {
        return new Intent(context, EditSemesterActivity.class)
                .putExtra(SemesterTable.COLUMN_ID, list.get(position).semesterID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}