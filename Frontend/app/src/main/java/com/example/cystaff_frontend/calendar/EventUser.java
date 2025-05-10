package com.example.cystaff_frontend.calendar;

/**
 * Class to hold a user for use in the event activity when searching for people to invite to a meeting
 */
public class EventUser {
    // Variables for all EventUser information
    private final String name, email;
    private final int userID, profilePicture;

    /**
     * Constructor for the event user item
     * @param userID - The user's unique ID
     * @param name - The user's full name
     * @param email - The user's full email
     * @param profilePicture - The user's profile picture
     */
    public EventUser(int userID, String name, String email, int profilePicture) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    /**
     * Getter for the full name
     * @return - Returns the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the full email
     * @return - Returns the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the user ID
     * @return - Returns the unique ID of the user
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Getter for the profile picture
     * @return - Returns the user's profile picture
     */
    public int getProfilePicture() {
        return profilePicture;
    }
}
