package com.example.cystaff_frontend.directory;

/**
 * A Class describing the fields for the directory Objects
 */
public class DirectoryItem {
    // Variables for all employee directory item information
    private final String firstName, lastName, fullName, email, number, building, jobLevel, birthday;
    private final int userID, profilePic;

    /**
     * Constructor for the employee directory item
     *
     * @param firstName  - Employee's first name
     * @param lastName   - Employee's last name
     * @param email      - Employee's email
     * @param number     - Employee's number
     * @param building   - Employee's assigned building
     * @param jobLevel   - Employee's job level
     * @param birthday   - Employee's birthday
     * @param profilePic - Employee's profile picture
     */
    public DirectoryItem(int userID, String firstName, String lastName, String email, String number, String building, String jobLevel, String birthday, int profilePic) {
        // Check the values in the item for optional inputs, if they are blank, put in applicable text
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.number = number;
        if (building.equals("")) {
            this.building = "No assigned building";
        } else {
            this.building = building;
        }
        this.jobLevel = jobLevel;
        if (birthday.equals("")) {
            this.birthday = "No assigned birthday";
        } else {
            this.birthday = birthday;
        }
        this.profilePic = profilePic;
    }

    /**
     * Getter for user id
     *
     * @return - Returns the user's id as an int
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Getter for first name
     *
     * @return - Returns the first name of the employee as a string
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for last name
     *
     * @return - Returns the last name of the employee as a string
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for full name
     *
     * @return - Returns the full name of the employee as a string
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Getter for email
     *
     * @return - Returns the email of the employee as a string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for phone number
     *
     * @return - Returns the phone number of the employee as a string
     */
    public String getNumber() {
        return number;
    }

    /**
     * Getter for building
     *
     * @return - Returns the building of the employee as a string
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Getter for job level
     *
     * @return - Returns the job level of the employee as an integer
     */
    public String getJobLevel() {
        return jobLevel;
    }

    /**
     * Getter for job level
     *
     * @return - Returns the job level of the employee as an integer
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Getter for profile picture
     *
     * @return - Returns the profile picture of the employee as an integer
     */
    public int getProfilePic() {
        return profilePic;
    }
}

