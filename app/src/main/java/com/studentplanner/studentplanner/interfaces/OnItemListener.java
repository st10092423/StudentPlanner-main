package com.studentplanner.studentplanner.interfaces;
import java.time.LocalDate;
@FunctionalInterface
public interface OnItemListener {
    void onItemClick(int position, LocalDate date);
}