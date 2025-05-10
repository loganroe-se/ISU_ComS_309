package com.example.cystaff_frontend.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A public Class that defines a user with all of its attributes.
 */
public class User implements Parcelable {

    // Variables for the user object
    private String password, firstName, lastName, fullName, birthday, number, email, building, active, lastLogin, privilegeType;
    private int userID, privilegeID;
    private HashMap<String, String> settings;

    /**
     * Constructor for user object
     *
     * @param userID        - The user's id
     * @param password      - The user's password
     * @param firstName     - The user's first name
     * @param lastName      - The user's last name
     * @param birthday      - The user's birthday
     * @param number        - The user's number
     * @param email         - The user's email
     * @param building      - The user's building
     * @param active        - The user's active status
     * @param lastLogin     - The user's last login
     * @param privilegeID   - The user's privilege ID (1 - system admin, 2 - building admin, 3 - normal user)
     * @param privilegeType - The user's privilege type
     * @param settings - The user's settings as a HashMap
     */
    public User(int userID, String password, String firstName, String lastName, String birthday, String number, String email, String building, String active, String lastLogin, int privilegeID, String privilegeType, HashMap<String, String> settings) {
        this.userID = userID;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.birthday = birthday;
        this.number = number;
        this.email = email;
        this.building = building;
        this.active = active;
        this.lastLogin = lastLogin;
        this.privilegeID = privilegeID;
        this.privilegeType = privilegeType;
        this.settings = settings;
    }

    /**
     * Constructor for a User Object given a Object of type Parcel as input.
     *
     * @param in - the Parcel Object to construct a User Object from
     */
    protected User(Parcel in) {
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        fullName = in.readString();
        birthday = in.readString();
        number = in.readString();
        email = in.readString();
        building = in.readString();
        active = in.readString();
        lastLogin = in.readString();
        privilegeType = in.readString();
        userID = in.readInt();
        privilegeID = in.readInt();
        int size = in.readInt();
        settings = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            settings.put(key, in.readString());
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Getter for the password private field
     *
     * @return password - the user's password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the firstName private field
     *
     * @return firstName - the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the lastName private field
     *
     * @return lastName - the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the fullName private field
     *
     * @return fullName - the user's full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Getter for the birthday private field
     *
     * @return birthday - the user's birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Getter for the number private field
     *
     * @return number - the user's phone number
     */
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Getter for the email private field
     *
     * @return email - the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the building private field
     *
     * @return building - the building the user is assigned to
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Getter for the active private field
     *
     * @return active - the user's active status
     */
    public String getActive() {
        return active;
    }

    /**
     * Getter for the lastLogin private field
     *
     * @return lastLogin - the time of the user's last login
     */
    public String getLastLogin() {
        return lastLogin;
    }

    /**
     * Getter for the privilegeType private field
     *
     * @return privilegeType - the user's privilege type
     */
    public String getPrivilegeType() {
        return privilegeType;
    }

    /**
     * Getter for the userID private field
     *
     * @return userID - the user's user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Getter for the privilegeID private field
     *
     * @return privilegeID - the user's privilegeID
     */
    public int getPrivilegeID() {
        return privilegeID;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void updateSettings(HashMap<String, String> settings) {
        this.settings.putAll(settings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(password);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(fullName);
        parcel.writeString(birthday);
        parcel.writeString(number);
        parcel.writeString(email);
        parcel.writeString(building);
        parcel.writeString(active);
        parcel.writeString(lastLogin);
        parcel.writeString(privilegeType);
        parcel.writeInt(userID);
        parcel.writeInt(privilegeID);
        parcel.writeInt(settings.size());
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }
}
