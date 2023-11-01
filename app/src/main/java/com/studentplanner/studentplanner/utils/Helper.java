package com.studentplanner.studentplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.models.ModuleTeacher;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Helper {
    private static final String ellipses = "...";
    public static boolean changeStatus;
    private static final String ERROR_TAG = "ERROR";


    private Helper() {
    }

    public static void goToActivity(Activity currentActivity, Class<? extends Activity> activityPageToOpen) {
        currentActivity.startActivity(new Intent(currentActivity, activityPageToOpen));

    }

    public static void longToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void shortToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String trimStr(TextInputLayout textField) {
        return textField.getEditText().getText().toString().trim();

    }

    public static String trimStr(AutoCompleteTextView textField) {
        return textField.getText().toString().trim();

    }

    public static String trimStr(TextInputLayout textField, boolean isTrimmed) {
        return textField.getEditText().getText().toString();

    }


    public static String formatDateShort(String date) {

        return formatDateStyle(FormatStyle.MEDIUM).format(LocalDate.parse(date));
    }

    private static DateTimeFormatter formatDateStyle(FormatStyle style) {
        return DateTimeFormatter.ofLocalizedDate(style);
    }


    public static String convertFullDateToYYMMDD(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .format(Objects.requireNonNull(new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH)
                            .parse(dateStr)));
        } catch (ParseException e) {
            Log.d(ERROR_TAG, Objects.requireNonNull(e.getMessage()));

        }
        return null;

    }

    public static long setFutureDate(long startDate, long noOfDaysBetween) {
        return startDate + (1000 * 60 * 60 * 24 * noOfDaysBetween);
    }

    public static long convertLocalDateToLong(LocalDate localDate) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
        return date.getTime();

    }

    public static long getNow() {
        return System.currentTimeMillis() - 1000;
    }

    public static String formatDate(String date) {
        return formatDateStyle(FormatStyle.FULL).format(LocalDate.parse(date));
    }

    public static LocalDate formatDate(int year, int month, int day) {
        return LocalDate.of(year, ++month, day);
    }

    public static String formatTime(String time) {
        return DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(time));
    }

    public static String formatTimeShort(String time) {
        return DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(time));
    }

    // link https://beginnersbook.com/2014/01/how-to-convert-12-hour-time-to-24-hour-date-in-java/
    public static String convertFormattedTimeToDBFormat(String time) {

        try {
            return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Objects.requireNonNull(new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).parse(time)));
        } catch (ParseException e) {
            Log.d(ERROR_TAG, Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }

    public static int getPriorityColour(String priority, Context c) {
        return switch (priority) {
            case "Low" -> c.getColor(R.color.dark_green);
            case "Medium" -> c.getColor(R.color.orange);
            default -> c.getColor(R.color.red);
        };
    }


    private static int[] isWeek() {
        return IntStream.range(0, 365)
                .filter(i -> i % 7 == 0)
                .toArray();
    }

    public static String calcDeadlineDate(LocalDate deadline, boolean isCompleted) {
        LocalDate today = LocalDate.now();
        Period p = Period.between(today, deadline);
        long weeks = ChronoUnit.WEEKS.between(today, deadline);
        int days = p.getDays();
        int months = p.getMonths();

        if (days == 0) {
            return "Due Today";
        }

        var sb = new StringBuilder("In ");
        if (months == 0 && days == 0 && !isCompleted) {

            return "Overdue";
        }


        if (months == 1 && days == 0 && isCompleted) {

            return sb.append("1 month").toString();
        }

        if (months == 0 && ArrayUtils.contains(isWeek(), days)) {

            return sb.append(weeks == 1 ? "1 week" : weeks + " weeks").toString();
        }

        if (months > 1 && days == 0) {
            return sb.append(months).append(" months").toString();

        }
        if (months > 1 && days == 1) {
            return sb.append(months).append(" months").append(" and 1 day").toString();

        }
        if (months == 1 && days == 1) {
            return sb.append("1 month and 1 day").toString();

        }

        if (months == 0 && days > 0) {
            return sb.append(days).append(" days").toString();

        }


        if (days < 0 && !isCompleted) {
            return "Overdue";
        } else if (days < 0) {

            return "";
        }

        return sb.append(months).append(" months and ").append(days).append(" days").toString();


    }


    public static String showFormattedDBTime(LocalTime t, Context context) {
        return formatTime(String.format(Locale.getDefault(), context.getString(R.string.time_format_database), t.getHour(), t.getMinute()));
    }


    public static void getDays(AutoCompleteTextView field, Context context) {
        field.setAdapter(new ArrayAdapter<>(context, R.layout.list_item, CalendarUtils.getDays()));
    }

    public static void getStringArray(Context context, AutoCompleteTextView field, int array) {
        final List<String> types = Arrays.asList(context.getResources().getStringArray(array));
        field.setAdapter(new ArrayAdapter<>(context, R.layout.list_item, types));
    }

    public static String[] convertListStringToStringArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    public static List<Integer> convertStringArrayToIntArrayList(List<String> numbers) {
        return numbers.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


    public static String getTeachersForSelectedModule(Context context, int moduleID) {
        return removeFirstAndLastChar(DatabaseHelper.getInstance(context).getTeachersForSelectedModuleID(moduleID).toString());
    }

    private static String removeFirstAndLastChar(String s) {
        return s.substring(1, s.length() - 1);
    }

    public static boolean moduleIDExistsInModuleTeacher(List<ModuleTeacher> moduleTeacherList, int moduleID) {
        return moduleTeacherList.stream().anyMatch(m -> m.moduleID() == moduleID);
    }

    public static String getReminderTitle() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = now.getMonth().toString();
        return WordUtils.capitalizeFully(String.format(Locale.ENGLISH, "coursework %s %s", month, year));

    }


    public static String readStream(final InputStream in) {
        var sb = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(in))) {
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            Log.d(ERROR_TAG, Objects.requireNonNull(e.getMessage()));
        }
        return sb.toString();
    }


    public static void deadlineSetup(BoundTimePickerDialog deadlineTimePicker, LocalDate date) {
        // set min time status
        deadlineTimePicker.setMinTimeToNow(date.isEqual(LocalDate.now()));
    }

    public static String getSnippet(String str, int length) {

        return StringUtils.left(str, length) + (str.length() >= length ? ellipses : "");
    }

    public static String getSnippet(String str) {
        final int length = 36;
        return StringUtils.left(str, length) + (str.length() >= length ? ellipses : "");
    }

    public static void characterCounter(TextInputLayout textField, Context context) {
        final int maxLength = textField.getCounterMaxLength();
        int length = trimStr(textField).length();
        final int greenLength = maxLength / 4;
        final int warningLength = maxLength / 2;

        textField.setBoxStrokeColor(context.getColor(R.color.black));

        if (ValueRange.of(greenLength, warningLength).isValidIntValue(length)) {
            textField.setBoxStrokeColor(context.getColor(R.color.dark_green));
            return;

        }

        if (ValueRange.of(warningLength, maxLength).isValidIntValue(length)) {
            textField.setBoxStrokeColor(context.getColor(R.color.dark_yellow));
            textField.setError(length == maxLength ? context.getString(R.string.max_length_error) : null);

        }

    }

    public static void setEditTextMaxLength(final EditText editText, int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(filterArray);
    }


    public static List<DayOfWeek> weekends() {
        return Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    }

}
