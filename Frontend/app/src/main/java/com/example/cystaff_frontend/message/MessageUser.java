package com.example.cystaff_frontend.message;

/**
 * A Class that defines an Object representing the user sender of a message.
 */
public class MessageUser {
    // Variables for all MessageUser information
    private final String name;
    private final int userID, profilePicture; // Profile pic is unused for now ***************

    /**
     * Constructor for the message user item
     *
     * @param userID         - The user's unique ID
     * @param name           - The user's full name
     * @param profilePicture - The user's profile picture
     */
    public MessageUser(int userID, String name, int profilePicture) {
        this.userID = userID;
        this.name = name;
        this.profilePicture = profilePicture;
    }

    /**
     * Getter for name
     *
     * @return - Returns the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for user ID
     *
     * @return - Returns the ID of the user
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Getter for profile picture
     *
     * @return - Returns the profile picture of the user
     */
    public int getProfilePicture() {
        return profilePicture;
    }
}
