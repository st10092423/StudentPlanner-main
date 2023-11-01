package com.studentplanner.studentplanner.utils;

import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.fragments.Calculator;
import com.studentplanner.studentplanner.fragments.ChatbotFragment;
import com.studentplanner.studentplanner.fragments.CalendarFragment;
import com.studentplanner.studentplanner.fragments.CourseworkFragment;
import com.studentplanner.studentplanner.fragments.HomeFragment;
import com.studentplanner.studentplanner.fragments.ModuleFragment;
import com.studentplanner.studentplanner.fragments.ModuleTeacherFragment;
import com.studentplanner.studentplanner.fragments.PastPapers;
import com.studentplanner.studentplanner.fragments.ReminderFragment;
import com.studentplanner.studentplanner.fragments.SemesterFragment;
import com.studentplanner.studentplanner.fragments.TeacherFragment;

import java.util.HashMap;
import java.util.Map;

public final class FragmentHandler {

    private static final Map<Integer, Fragment> fragments;

    static {
        fragments = new HashMap<>();
        fragments.put(R.id.nav_home, new HomeFragment());
        fragments.put(R.id.nav_reminder, new ReminderFragment());
        fragments.put(R.id.nav_semester, new SemesterFragment());
        fragments.put(R.id.nav_teachers, new TeacherFragment());
        fragments.put(R.id.nav_module_teacher, new ModuleTeacherFragment());
        fragments.put(R.id.nav_calendar, new CalendarFragment());
        fragments.put(R.id.nav_coursework, new CourseworkFragment());
        fragments.put(R.id.nav_module, new ModuleFragment());
        fragments.put(R.id.nav_calculator, new Calculator());
        fragments.put(R.id.nav_Chatbot, new ChatbotFragment());
        fragments.put(R.id.nav_past_papers, new PastPapers());


    }

    private FragmentHandler() {
    }

    public static int activeLink(final Fragment selectedFragment) {
        return getSelectedFragmentID(getFragmentName(selectedFragment));
    }

    private static int getSelectedFragmentID(final String name) {
        return fragments.entrySet().stream()
                .filter(i -> name.equals(getFragmentName(i.getValue())))
                .map(Map.Entry::getKey)
                .toList()
                .get(0);
    }

    private static String getFragmentName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    public static Fragment getSelectedFragment(final MenuItem item) {
        return fragments.get(item.getItemId());
    }
}
