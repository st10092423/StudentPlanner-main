package com.studentplanner.studentplanner.viewholders;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.ModuleRowBinding;
import com.studentplanner.studentplanner.models.Module;

import org.apache.commons.text.WordUtils;

public class ModuleViewHolder extends RecyclerView.ViewHolder {
    private final TextView moduleID;
    private final TextView moduleCode;
    private final TextView moduleName;

    private final CardView layout;


    public ModuleViewHolder(@NonNull ModuleRowBinding binding) {
        super(binding.getRoot());
        moduleID = binding.tvModuleId;
        moduleName = binding.tvModuleName;
        moduleCode = binding.tvModuleCode;
        layout = binding.layout;

    }

    public CardView getLayout() {
        return layout;
    }

    public void showDetails(Module module){

        moduleID.setText(String.valueOf(module.getModuleID()));
        moduleName.setText(WordUtils.capitalizeFully(module.getModuleName()));
        moduleCode.setText(module.getModuleCode().toUpperCase());

    }

}
