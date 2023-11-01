package com.studentplanner.studentplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.ModuleTeacherRowBinding;
import com.studentplanner.studentplanner.editActivities.EditModuleTeacherActivity;
import com.studentplanner.studentplanner.models.ModuleTeacher;
import com.studentplanner.studentplanner.tables.ModuleTable;
import com.studentplanner.studentplanner.viewholders.ModuleTeacherViewHolder;

import java.util.Collections;
import java.util.List;

public class ModuleTeacherAdapter extends RecyclerView.Adapter<ModuleTeacherViewHolder> {

    private List<ModuleTeacher> list;
    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public ModuleTeacherAdapter(List<ModuleTeacher> list, Context context, ActivityResultLauncher<Intent> startForResult) {
        this.list = Collections.unmodifiableList(list);
        this.context = context;
        this.startForResult = startForResult;
    }

    public void filterList(List<ModuleTeacher> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModuleTeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ModuleTeacherViewHolder(ModuleTeacherRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ModuleTeacherViewHolder holder, int position) {
        holder.showDetails(list, position);
        holder.getLayout().setOnClickListener(v -> startForResult.launch(intent(position)));
    }

    private Intent intent(int position) {
        return new Intent(context, EditModuleTeacherActivity.class)
                .putExtra(ModuleTable.COLUMN_ID, list.get(position).moduleID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
