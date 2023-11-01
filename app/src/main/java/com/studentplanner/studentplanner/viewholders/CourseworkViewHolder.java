package com.studentplanner.studentplanner.viewholders;

import static com.studentplanner.studentplanner.utils.CompletionStatus.COMPLETED;
import static com.studentplanner.studentplanner.utils.CompletionStatus.NOT_COMPLETED;
import static com.studentplanner.studentplanner.utils.Helper.getSnippet;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.CourseworkRowBinding;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.models.ImageHandler;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.utils.Helper;

import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.util.Locale;


public class CourseworkViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvCourseworkID;
    private final TextView title;
    private final TextView tvDescription;
    private final TextView tvDeadline;
    private final TextView priority;
    private final TextView tvTimeLeft;
    private final TextView tvCourseworkModule;
    private final TextView tvCompleted;
    private final ImageView image;
    private final CardView layout;
    private final DatabaseHelper db;
    private final Context context;

    public CardView getLayout() {
        return layout;
    }

    public CourseworkViewHolder(@NonNull CourseworkRowBinding binding) {
        super(binding.getRoot());
        tvCourseworkID = binding.tvCourseworkId;
        title = binding.tvCourseworkTitle;
        tvDescription = binding.tvCourseworkDesc;
        tvDeadline = binding.tvCourseworkDeadline;
        priority = binding.tvCourseworkPriority;
        tvTimeLeft = binding.tvTimeLeft;
        tvCourseworkModule = binding.tvCourseworkModule;
        tvCompleted = binding.tvCourseworkCompleted;

        image = binding.tvCourseworkImage;
        layout = binding.layout;

        context = binding.getRoot().getContext();
        db = DatabaseHelper.getInstance(context);
    }


    public void showDetails(Coursework coursework) {

        tvCourseworkID.setText(String.valueOf(coursework.getCourseworkID()));
        title.setText(getSnippet(WordUtils.capitalizeFully(coursework.getTitle())));

        showDescription(getSnippet(coursework.getDescription(), 120));

        tvDeadline.setText(showDeadlineDetails(coursework));
        priority.setText(coursework.getPriority());
        priority.setTextColor(Helper.getPriorityColour(coursework.getPriority(), context));
        tvCourseworkModule.setText(db.getSelectedModule(coursework.getModuleID()).getModuleDetails());
        tvCompleted.setText(coursework.isCompleted() ? COMPLETED : NOT_COMPLETED);
        tvCompleted.setTextColor(coursework.isCompleted() ? context.getColor(R.color.dark_green) : Color.RED);

        showTimeLeft(coursework.getDeadline(), coursework);
        ImageHandler.showImage(coursework.getByteImage(), image);


    }


    private void showTimeLeft(LocalDate deadline, Coursework coursework) {
        final String timeLeft = Helper.calcDeadlineDate(deadline, coursework.isCompleted());
        if (isBlank(timeLeft)) {
            tvTimeLeft.setVisibility(View.GONE);
            return;
        }
        tvTimeLeft.setText(timeLeft);
        tvTimeLeft.setTextColor(Helper.getPriorityColour(coursework.getPriority(), context));

    }

    private void showDescription(final String description) {
        if (isBlank(description)) {
            tvDescription.setVisibility(View.GONE);
            return;
        }
        tvDescription.setVisibility(View.VISIBLE);
        tvDescription.setText(description);

    }

    private String showDeadlineDetails(Coursework coursework) {
        return String.format(Locale.ENGLISH, "%s, %s",
                Helper.formatDate(coursework.getDeadline().toString()),
                Helper.formatTime(coursework.getDeadlineTime().toString())
        );

    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}