package com.studentplanner.studentplanner.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.CalendarCellBinding;
import com.studentplanner.studentplanner.interfaces.OnItemListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    private final OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull CalendarCellBinding binding, OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(binding.getRoot());
        parentView = binding.parentView;
        dayOfMonth = binding.cellDayText;
        this.onItemListener = onItemListener;
        binding.getRoot().setOnClickListener(this);
        this.days = days;
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAbsoluteAdapterPosition(), days.get(getAbsoluteAdapterPosition()));

    }
}
