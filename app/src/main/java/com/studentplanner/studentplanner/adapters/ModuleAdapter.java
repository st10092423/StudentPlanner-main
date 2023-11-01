package com.studentplanner.studentplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.ModuleRowBinding;
import com.studentplanner.studentplanner.editActivities.EditModuleActivity;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.tables.ModuleTable;
import com.studentplanner.studentplanner.viewholders.ModuleViewHolder;

import java.util.Collections;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleViewHolder> {

    private List<Module> list;
    private final Context context;
    private final ActivityResultLauncher<Intent> startForResult;

    public ModuleAdapter(List<Module> list, Context context, ActivityResultLauncher<Intent> startForResult) {
        this.list = Collections.unmodifiableList(list);
        this.context = context;
        this.startForResult = startForResult;
    }


    public void filterList(List<Module> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ModuleViewHolder(ModuleRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        holder.showDetails(list.get(position));
        holder.getLayout().setOnClickListener(v -> startForResult.launch(intent(position)));
    }

    private Intent intent(int position) {
        return new Intent(context, EditModuleActivity.class)
                .putExtra(ModuleTable.COLUMN_ID, list.get(position).getModuleID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}