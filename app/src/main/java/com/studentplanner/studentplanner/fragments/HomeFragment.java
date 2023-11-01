package com.studentplanner.studentplanner.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.utils.AccountPreferences;
import com.studentplanner.studentplanner.utils.EmptyData;
import com.studentplanner.studentplanner.utils.Helper;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initFragment();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView navNameLabel = view.findViewById(R.id.nav_name_label);

        if (AccountPreferences.getStudentID(requireContext()) != 0) {
            DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
            int studentID = AccountPreferences.getStudentID(requireContext());
            String userName = db.getUserFirstAndLastName(studentID).getName();
            navNameLabel.setText(userName);
        }

        CardView calendarCard = view.findViewById(R.id.calendar_card1);
        CardView chatbotCard = view.findViewById(R.id.chatbot_card);
        CardView modulesCard = view.findViewById(R.id.modules);
        CardView pastPapersCard = view.findViewById(R.id.past_papers);
        CardView courseworkCard = view.findViewById(R.id.coursework);
        CardView remindersCard = view.findViewById(R.id.reminders);

        calendarCard.setOnClickListener(this);
        chatbotCard.setOnClickListener(this);
        modulesCard.setOnClickListener(this);
        pastPapersCard.setOnClickListener(this);
        courseworkCard.setOnClickListener(this);
        remindersCard.setOnClickListener(this);


        return view;
    }
    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(context.getString(R.string.Home));
        setHasOptionsMenu(true);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.calendar_card1) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CalendarFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.chatbot_card) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatbotFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.modules) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ModuleFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.past_papers) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PastPapers())
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.coursework) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseworkFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (view.getId() == R.id.reminders) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReminderFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

}
