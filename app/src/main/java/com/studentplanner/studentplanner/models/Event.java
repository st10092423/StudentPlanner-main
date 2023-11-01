package com.studentplanner.studentplanner.models;

import com.studentplanner.studentplanner.enums.EventType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Event {

    private int id;
    private Coursework coursework;
    private Classes classes;
    private final EventType eventType;
    private final LocalDate date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Coursework getCoursework() {
        return coursework;
    }


    public Classes getClasses() {
        return classes;
    }


    public EventType getEventType() {
        return eventType;
    }

    public static ArrayList<Event> getEventsList() {
        return eventsList;
    }


    private static final ArrayList<Event> eventsList = new ArrayList<>();


    public static List<Event> eventsForDate(LocalDate date) {
        return eventsList.stream().filter(event -> event.getDate().equals(date)).toList();

    }

    // for coursework entries
    public Event(int id, LocalDate date, EventType eventType, Coursework coursework) {
        this.id = id;
        this.date = date;
        this.eventType = eventType;
        this.coursework = coursework;
    }

    // recurring events
    public Event(int id, LocalDate date, EventType eventType, Classes classes) {
        this.id = id;
        this.date = date;
        this.eventType = eventType;
        this.classes = classes;

    }

    public LocalDate getDate() {
        return date;
    }


}

