package com.example.cystaff_frontend.settings;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsList {
    public static HashMap<String, ArrayList<String>> getSettings() {
        // Create a HashMap to store the settings
        HashMap<String, ArrayList<String>> expandableListDetails = new HashMap<String, ArrayList<String>>();

        // Fill the HashMap with the wanted settings
        // All default settings
        ArrayList<String> defaultSettings = new ArrayList<String>();
        defaultSettings.add("Calendar Format (!Options)");
        defaultSettings.add("Create Events - Event Type (!Options)");
        defaultSettings.add("Create Events - Location Type (!Options)");
        defaultSettings.add("Number of Announcements To Display (!IntEntry)");
        defaultSettings.add("Employee Directory - Sort By Last Name (!Options)");
        expandableListDetails.put("Default Settings", defaultSettings);

        // All profile settings
        ArrayList<String> profileSettings = new ArrayList<String>();
        profileSettings.add("Preferred Display Name (!Text)");
        profileSettings.add("Change Password (!Password)");
        profileSettings.add("Phone Number (!Number)");
        expandableListDetails.put("Profile Settings", profileSettings);

        return expandableListDetails;
    }
}
