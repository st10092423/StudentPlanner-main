package com.studentplanner.studentplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.TeacherRowBinding;
import com.studentplanner.studentplanner.editActivities.EditTeacherActivity;
import com.studentplanner.studentplanner.models.Teacher;
import com.studentplanner.studentplanner.tables.TeacherTable;
import com.studentplanner.studentplanner.viewholders.TeacherViewHolder;

import java.util.Collections;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherViewHolder> {

    private List<Teacher> list;
    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public TeacherAdapter(List<Teacher> list, Context context, ActivityResultLauncher<Intent> startForResult) {
        this.list = Collections.unmodifiableList(list);
        this.context = context;
        this.startForResult = startForResult;
    }

    public void filterList(List<Teacher> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TeacherViewHolder(TeacherRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        holder.showDetails(list.get(position));
        holder.getLayout().setOnClickListener(view -> startForResult.launch(intent(position)));
    }

    private Intent intent(int position) {
        return new Intent(context, EditTeacherActivity.class)
                .putExtra(TeacherTable.COLUMN_ID, list.get(position).getUserID());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}