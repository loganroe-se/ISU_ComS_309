package com.example.cystaff_frontend.calendar;

import java.time.LocalDate;

/**
 * Class used to hold all related variables for an event to be displayed on the calendar page
 */
public class EventItem {
    // Variables for all EventItem information
    private final int meetingID;
    private final LocalDate startDay, endDay;
    private final String startTime, endTime;
    private final int duration;
    private final String details, title, eventType, locationType;
    private final int[] recipientIDs;
    private final String[] recipientNames;
    private final int roomID, statusID, userID;
    private final String creatorName;

    /**
     * Constructor for the event item
     * @param meetingID - The meeting id - unique identifier
     * @param startDay - LocalDate showing the start time of the event
     * @param endDay - LocalDate showing the end time of the event
     * @param startTime - String showing the start time of the event
     * @param endTime - String showing the end time of the event
     * @param duration - The duration of the event
     * @param details - Any added details
     * @param title - The title of the event
     * @param eventType - Whether the event is a reminder or a meeting
     * @param locationType - Whether the event is online or in-person
     * @param recipientIDs - The userIDs of the users receiving this - Excluding the creator
     * @param recipientNames - The names of the users receiving this - Excluding the creator
     * @param roomID - The roomID that corresponds to the building/room - Will default to 1 if in-person
     * @param statusID - The statusID - 1 means approved, 2 means declined, 3 means pending
     * @param userID - The userID of the user who created the event
     * @param creatorName - The creator's full name
     */
    public EventItem(int meetingID, LocalDate startDay, LocalDate endDay, String startTime, String endTime, int duration, String details, String title, String eventType, String locationType, int[] recipientIDs, String[] recipientNames, int roomID, int statusID, int userID, String creatorName) {
        this.meetingID = meetingID;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.details = details;
        this.title = title;
        this.eventType = eventType;
        this.locationType = locationType;
        this.recipientIDs = recipientIDs;
        this.recipientNames = recipientNames;
        this.roomID = roomID;
        this.statusID = statusID;
        this.userID = userID;
        this.creatorName = creatorName;
    }

    /**
     * Getter for the meeting ID
     * @return - The meeting ID
     */
    public int getMeetingID() { return meetingID; }

    /**
     * Getter for the start day
     * @return - Returns the start day
     */
    public LocalDate getStartDay() {
        return startDay;
    }

    /**
     * Getter for the end day
     * @return - Returns the end day
     */
    public LocalDate getEndDay() {
        return endDay;
    }

    /**
     * Getter for the start time
     * @return - Returns the start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Getter for the end time
     * @return - Returns the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Getter for the duration of the event
     * @return - Returns the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Getter for the details
     * @return - Returns the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Getter for the title
     * @return - Returns the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the event type - Either Reminder or Meeting
     * @return - Returns the event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Getter for the location type - Either Online or In-Person
     * @return - Returns the location type
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     * Getter for the recipient IDs - List of user IDs of those invited, excluding the creator
     * @return - Returns the recipients
     */
    public int[] getRecipientIDs() {
        return recipientIDs;
    }

    /**
     * Getter for the recipient names - List of user names, excluding the creator
     * @return - Returns the recipient names
     */
    public String[] getRecipientNames() {
        return recipientNames;
    }

    /**
     * Getter for the room ID - Unique ID that signifies the building and the room
     * @return - Returns the room ID
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Getter for the status ID - 1 : Approved, 2 : Declined, 3 : Pending
     * @return - Returns the status ID
     */
    public int getStatusID() {
        return statusID;
    }

    /**
     * Getter for the user ID of the creator - Unique ID
     * @return - Returns the user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Getter for the full name of the creator
     * @return - Returns the creator's full name
     */
    public String getCreatorName() {
        return creatorName;
    }
}
