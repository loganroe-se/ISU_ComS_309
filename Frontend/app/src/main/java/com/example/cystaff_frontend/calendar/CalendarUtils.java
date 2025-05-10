package com.example.cystaff_frontend.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Holds various utilities for the calendar classes
 */
public class CalendarUtils {
    /**
     * The currently selected date - May only be one value
     */
    public static LocalDate selectedDate;
    /**
     * All events for the current user as EventItems
     */
    public static ArrayList<EventItem> allEvents = new ArrayList<EventItem>();
    /**
     * All possible Buildings and Rooms
     */
    public static ArrayList<RoomItem> allRooms = new ArrayList<RoomItem>();

    /**
     * Given a roomID, finds the corresponding building and room and outputs: Building, Room
     * @param roomID - The roomID to search for
     * @return - The Building and Room: Building, Room
     */
    public static String findBuildingRoom(int roomID) {
        // Loop through all of the room items to find the right one and return it
        for (int i = 0; i < allRooms.size(); i++) {
            if (allRooms.get(i).getRoomID() == roomID) {
                return allRooms.get(i).getBuilding() + ", " + allRooms.get(i).getRoom();
            }
        }
        return "";
    }

    /**
     * Given a date, finds all events on that given date
     * @param date - The date to find events on
     * @return - The ArrayList of the EventItems for the given date
     */
    public static ArrayList<EventItem> findEventsByDate(LocalDate date) {
        // Create an empty JSONArray to hold the events
        ArrayList<EventItem> events = new ArrayList<EventItem>();

        // Loop through all of the events
        for (int i = 0; i < allEvents.size(); i++) {
            // Get the current event
            EventItem currEvent = allEvents.get(i);
            // If either the start or end date, or in between the start/end dates, is equal to the
            // current date being checked, add it to the JSONArray of events
            if (date.isEqual(currEvent.getStartDay()) || date.isEqual(currEvent.getEndDay()) || (date.isAfter(currEvent.getStartDay()) && date.isBefore(currEvent.getEndDay()))) {
                events.add(currEvent);
            }
        }

        return events;
    }

    /**
     * Gets an array that holds all days for a given month
     * @param date - The date to get the days for
     * @return The ArrayList of LocalDates for a given month
     */
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        // Create variables
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // Loop through all possible days of the month and append items to the days array
        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(null);
            } else {
                daysInMonthArray.add(LocalDate.of(date.getYear(), date.getMonth(), i - dayOfWeek));
            }
        }

        return daysInMonthArray;
    }

    /**
     * Gets an array that holds all days for a given week
     * @param selectedDate - The date to get the days for
     * @return The ArrayList of LocalDates for a given week
     */
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        // Create variables
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate date = sundayForDate(selectedDate);
        LocalDate endDate = date.plusWeeks(1);

        // Add as many days as necessary
        while (date.isBefore(endDate)) {
            daysInWeekArray.add(date);
            date = date.plusDays(1);
        }

        return daysInWeekArray;
    }

    private static LocalDate sundayForDate(LocalDate date) {
        LocalDate oneWeekEarlier = date.minusWeeks(1);

        // Loop through the dats and return a sunday
        while (date.isAfter(oneWeekEarlier)) {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return date;
            }

            date = date.minusDays(1);
        }

        return null;
    }

    /**
     * Gets the month and year for a given date in the MMMM yyyy format
     * @param date - The date to get the month and year for
     * @return The formatted Month year string - Month Year
     */
    public static String monthYearFromDate(LocalDate date) {
        // Format the returned date to show month and year
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }
}
