package com.example.cystaff_frontend.announcements;

/**
 * A Class for describing instances of Announcements with their properties.
 */
public class Announcement {

    //Storing all of the information about an announcement
    private final String title;
    private final String firstName;
    private final String lastName;
    private final String message;
    private final String time;
    private final String date;

    /**
     * Constructor for the Announcement. Simply sets the private fields to the corresponding parameters.
     *
     * @param title     - The title of the announcement
     * @param firstName - The first name of posting user
     * @param lastName  - The last name of the posting user
     * @param message   - The announcement message
     * @param time      - The announcement time
     * @param date      - The announcement date
     */
    public Announcement(String title, String firstName, String lastName, String message, String time, String date) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
        this.time = time;
        this.date = date;
    }

    /**
     * Getter for the announcement's title
     *
     * @return title - The announcement's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the poster's first name
     *
     * @return firstName - The poster's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the poster's last name
     *
     * @return lastName - The poster's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the announcement's message
     *
     * @return message - The announcement's message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the announcement's time
     *
     * @return time - The announcement's time
     */
    public String getTime() {
        return time;
    }

    /**
     * Getter for the announcement's date
     *
     * @return date - The announcement's date
     */
    public String getDate() {
        return date;
    }
}
