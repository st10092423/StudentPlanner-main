package com.studentplanner.studentplanner.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.studentplanner.studentplanner.R;

import org.apache.commons.text.WordUtils;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Dropdown {
    private Dropdown() {
    }

    public static void getStringArray(AutoCompleteTextView field, Context c, int array) {
        field.setAdapter(new ArrayAdapter<>(c, R.layout.list_item, c.getResources().getStringArray(array)));
    }


    public static int getSelectedStringArrayNumber(String item, Context c, int array) {

        return Arrays.asList(c.getResources().getStringArray(array)).indexOf(item);
    }

    public static int setSelectedDayIndex(DayOfWeek dayOfWeek) {
        return CalendarUtils.getDays().indexOf(WordUtils.capitalize(dayOfWeek.toString().toLowerCase(Locale.ROOT)));

    }

    public static int getDropDownID(final int id, final List<Integer> list) {
        return list.indexOf(id);

    }

    public static void setDefaultSpinnerPosition(Spinner... spinners) {
        Arrays.stream(spinners).toList().forEach(spinner -> spinner.setSelection(0, false));
    }

    public static String getSpinnerText(Spinner spinner, int position) {
        return spinner.getAdapter().getItem(position).toString();

    }

}
