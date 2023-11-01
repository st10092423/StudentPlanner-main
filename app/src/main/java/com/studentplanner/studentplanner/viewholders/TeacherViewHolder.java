package com.studentplanner.studentplanner.viewholders;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.databinding.TeacherRowBinding;
import com.studentplanner.studentplanner.models.Teacher;

public class TeacherViewHolder extends RecyclerView.ViewHolder {
    private final TextView teacherID;

    private final TextView name;
    private final TextView email;

    private final CardView layout;

    public CardView getLayout() {
        return layout;
    }

    public TeacherViewHolder(@NonNull TeacherRowBinding binding) {
        super(binding.getRoot());
        teacherID = binding.tvTeacherId;
        name = binding.tvTeacherName;
        email = binding.tvEmail;
        layout = binding.layout;
    }

    public void showDetails(Teacher teacher) {
        teacherID.setText(String.valueOf(teacher.getUserID()));
        name.setText(teacher.getName());
        email.setText(teacher.getEmail());
    }
}
