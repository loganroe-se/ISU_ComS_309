package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.settings.SettingsList;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.GeneralUtilFuncs;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class defining the behaviour of the settings page
 */
public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Holds the TextViews used on the page
    private TextView headerTxt;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar;

    //Holds the reference to the header's profile button
    private Spinner profile;

    // List view variables
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private ArrayList<String> expandableListTitles;
    private HashMap<String, ArrayList<String>> expandableListDetails;

    // HashMap to hold settings changes
    private HashMap<String, String> settingsChanges = new HashMap<>();
    private TextView numberOfAnnouncements = null, numAnnouncementsTitle = null;
    private TextView preferredDisplayName = null, preferredDisplayNameTitle = null;
    private EditText phoneNumber = null;
    private TextView phoneNumberTitle = null;

    // Change password variables
    private Dialog changePasswordPopUp;
    private Button cancelPasswordChanges, confirmPasswordChanges;
    private TextView changePasswordErrText;
    private TextInputLayout currentPasswordTIL, newPasswordTIL, newPasswordRepeatTIL;
    private String currentPasswordValue = null, newPasswordValue = null, newPasswordRepeatValue = null, changedPasswordValue = null;

    // Error text view
    private TextView errText;

    // Button to apply changes
    private Button applyBtn;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Set the notification handler's root View
        NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
        temp.setActivity(this);

        // Get the user
        currUser = getIntent().getParcelableExtra("userInfo", User.class);

        //Find screen element(s)
        headerTxt = findViewById(R.id.headerText);
        drwLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.header);
        profile = findViewById(R.id.profile);
        expandableListView = findViewById(R.id.settingsListView);
        applyBtn = findViewById(R.id.settingsApplyBtn);
        errText = findViewById(R.id.settingsErrText);

        // Set up the expandable list view
        expandableListDetails = SettingsList.getSettings();
        expandableListTitles = new ArrayList<String>(expandableListDetails.keySet());
        expandableListAdapter = new SettingsExpandableListAdapter(this, expandableListTitles, expandableListDetails);
        expandableListView.setAdapter(expandableListAdapter);

        // Set up the pop up views
        changePasswordPopUp = new Dialog(this);
        changePasswordPopUp.setContentView(R.layout.settings_change_password);
        Objects.requireNonNull(changePasswordPopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Define buttons/text layouts in the password pop up
        cancelPasswordChanges = changePasswordPopUp.findViewById(R.id.cancelPasswordChange);
        confirmPasswordChanges = changePasswordPopUp.findViewById(R.id.confirmPasswordChange);
        changePasswordErrText = changePasswordPopUp.findViewById(R.id.changePasswordErrText);
        currentPasswordTIL = changePasswordPopUp.findViewById(R.id.currentPasswordTextInput);
        newPasswordTIL = changePasswordPopUp.findViewById(R.id.newPasswordTextInput);
        newPasswordRepeatTIL = changePasswordPopUp.findViewById(R.id.newPasswordRepeatedTextInput);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Listener for when the apply changes button is pressed
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset error text
                errText.setVisibility(View.GONE);
                // Hide the soft keyboard
                GeneralUtilFuncs.hideKeyboard(SettingsActivity.this);
                // Change the HashMap values that are not live edited - Also update titles accordingly
                // Number of announcements change
                if (numberOfAnnouncements != null) {
                    String numAnnouncements = numberOfAnnouncements.getText().toString();
                    if (numAnnouncements.length() != 0 && Integer.parseInt(numAnnouncements) >= 1) {
                        settingsChanges.put("numberOfAnnouncements", numAnnouncements);
                        numAnnouncementsTitle.setText("Number of Announcements To Display [" + numAnnouncements + "]");
                    } else if (numAnnouncements.length() != 0) {
                        Toast.makeText(getApplicationContext(), "Number of announcements will not be changed. The value must be greater than 0.", Toast.LENGTH_LONG).show();
                    }
                }

                // Preferred display name change
                if (preferredDisplayName != null) {
                    String preferredName = preferredDisplayName.getText().toString();
                    if (preferredName.length() >= 3) {
                        settingsChanges.put("preferredName", preferredName);
                        preferredDisplayNameTitle.setText("Preferred Display Name [" + preferredName + "]");
                    } else if (preferredName.length() != 0) {
                        Toast.makeText(getApplicationContext(), "Preferred name will not be changed. The length must be greater than 2.", Toast.LENGTH_LONG).show();
                    }
                }

                // Make the request to change the settings
                if (settingsChanges.size() != 0) {
                    // Generate the path string
                    String settingsURL = Const.serverURL + "settings";

                    // Get the JSON object
                    JSONObject settingsObj = new JSONObject(settingsChanges);
                    try {
                        settingsObj.put("settingsid", currUser.getSettings().get("settingsID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest settingsChangeReq = new JsonObjectRequest(Request.Method.PUT, settingsURL, settingsObj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Volley Response", response.toString());
                                // Get the response in string format
                                String responseVal = "Failure";
                                try {
                                    responseVal = response.getString("status");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Possible Responses:
                                // Failure - Failed for some reason
                                // Success - User was edited
                                if (responseVal.equalsIgnoreCase("Success")) {
                                    // Inform the user that the user was successfully edited
                                    Toast.makeText(getApplicationContext(), "Successfully applied the changes.", Toast.LENGTH_SHORT).show();
                                    currUser.updateSettings(settingsChanges);
                                } else {
                                    // Inform the user there was an error when changing the password/phone number
                                    errText.setText("There was an error when changing the requested settings. Please try again.");
                                    errText.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", "Error: " + error.getMessage());
                                // Inform the user there was an error when changing the password/phone number
                                errText.setText("There was an error when changing the requested settings. Please try again.");
                                errText.setVisibility(View.VISIBLE);
                            }
                    });

                    // Adding request to request queue
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(settingsChangeReq);
                }

                // JSONObject to hold the phone and password changes - Both done in a separate request
                JSONObject editUserObj = new JSONObject();
                if (phoneNumber != null) {
                    String phoneNumberStr = phoneNumber.getText().toString();
                    if (phoneNumberStr.length() == 12) {
                        try {
                            if (editUserObj.length() == 0) {
                                editUserObj.put("userid", currUser.getUserID());
                            }
                            editUserObj.put("phone", phoneNumberStr);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        phoneNumberTitle.setText("Phone Number [" + phoneNumberStr + "]");
                    } else if (phoneNumberStr.length() != 0) {
                        Toast.makeText(getApplicationContext(), "Make sure to enter a valid phone number. Format: 000-000-0000", Toast.LENGTH_LONG).show();
                    }
                }
                if (changedPasswordValue != null) {
                    try {
                        if (editUserObj.length() == 0) {
                            editUserObj.put("userid", currUser.getUserID());
                        }
                        editUserObj.put("password", changedPasswordValue);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                // If the JSONObject is not empty, create the edit user request
                if (editUserObj.length() != 0) {
                    // Generate the path string
                    String URL = Const.serverURL + "users";

                    // Get the text for later use
                    String properTextDisplay = "";
                    if (changedPasswordValue != null) {
                        properTextDisplay = properTextDisplay + "password";
                        if (!editUserObj.isNull("phone")) {
                            properTextDisplay = properTextDisplay + " & the phone number";
                        }
                    } else if (!editUserObj.isNull("phone")) {
                        properTextDisplay = properTextDisplay + "phone number";
                    }

                    String finalProperTextDisplay = properTextDisplay;
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, URL, editUserObj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Volley Response", response.toString());
                                // Get the response in string format
                                String responseVal = "Failure";
                                try {
                                    responseVal = response.getString("status");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Possible Responses:
                                // Failure - Failed for some reason
                                // Success - User was edited
                                if (responseVal.equalsIgnoreCase("Success")) {
                                    // Inform the user that the user was successfully edited
                                    Toast.makeText(getApplicationContext(), "Successfully edited the " + finalProperTextDisplay + ".", Toast.LENGTH_SHORT).show();
                                    if (finalProperTextDisplay.contains("number")) {
                                        currUser.setNumber(phoneNumber.getText().toString());
                                    }
                                    if (finalProperTextDisplay.contains("password")) {
                                        currUser.setPassword(changedPasswordValue);
                                    }
                                } else {
                                    // Inform the user there was an error when changing the password/phone number
                                    errText.setText("There was an error when changing the " + finalProperTextDisplay + ". Please try again.");
                                    errText.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", "Error: " + error.getMessage());
                                // Inform the user there was an error when changing the password/phone number
                                errText.setText("There was an error when changing the " + finalProperTextDisplay + ". Please try again.");
                                errText.setVisibility(View.VISIBLE);
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
                }
            }
        });
    }

    /**
     * Used for setting up the buttons in the navigation menu. Calls the Header's selectHelper Method.
     *
     * @param item - The selected item
     * @return true upon successful execution
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Header.selectHelper(item, drwLayout, this, currUser);
        return true;
    }

    public class SettingsExpandableListAdapter extends BaseExpandableListAdapter {
        // Private variables
        private Context context;
        private ArrayList<String> expandableListTitles;
        private HashMap<String, ArrayList<String>> expandableListDetails;

        /**
         * Constructor for the expandable list adapter
         * @param context - The context
         * @param expandableListTitles - The titles of the items in the list
         * @param expandableListDetails - The sub-contents of the expandable lists
         */
        public SettingsExpandableListAdapter(Context context, ArrayList<String> expandableListTitles, HashMap<String, ArrayList<String>> expandableListDetails) {
            this.context = context;
            this.expandableListTitles = expandableListTitles;
            this.expandableListDetails = expandableListDetails;
        }


        @Override
        public int getGroupCount() {
            return this.expandableListTitles.size();
        }

        @Override
        public Object getGroup(int listPos) {
            return this.expandableListTitles.get(listPos);
        }

        @Override
        public long getGroupId(int listPos) {
            return listPos;
        }

        @Override
        public View getGroupView(int listPos, boolean isExpanded, View view, ViewGroup parent) {
            String listTitle = (String) getGroup(listPos);
            // Check if the view is null and inflate a group item if so
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.settings_list_group, null);
            }

            // Get the title's text view and set the text
            TextView listTitleTextView = view.findViewById(R.id.settingsListTitle);
            listTitleTextView.setText(listTitle);
            return view;
        }

        @Override
        public int getChildrenCount(int listPos) {
            return this.expandableListDetails.get(this.expandableListTitles.get(listPos)).size();
        }

        @Override
        public Object getChild(int listPos, int expandedListPos) {
            return this.expandableListDetails.get(this.expandableListTitles.get(listPos)).get(expandedListPos);
        }

        @Override
        public long getChildId(int listPos, int expandedListPos) {
            return expandedListPos;
        }

        @Override
        public View getChildView(int listPos, int expandedListPos, boolean isLastChild, View view, ViewGroup parent) {
            // Get the corresponding string
            String expandedListText = (String) getChild(listPos, expandedListPos);
            String title = expandedListText;
            // Reset the view if the titles do not match
            if (view != null && !((TextView) view.findViewById(R.id.settingsExpandedListItem)).getText().toString().contains(title.substring(0, title.indexOf(" (!")))) {
                view = null;
            }
            // Check if the view is null and inflate an item if so
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.settings_list_item, null);
                Spinner optionSelect = view.findViewById(R.id.optionSelect);
                optionSelect.setVisibility(View.GONE);
                int selectLoc = 0;
                if (expandedListText.contains("(!Options)")) {
                    // Set the options
                    ArrayAdapter<CharSequence> adapter = null;
                    switch (title) {
                        case "Calendar Format (!Options)":
                            adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.calendarFormat, android.R.layout.simple_spinner_item);
                            // Set the right option
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (currUser.getSettings().get("calendarFormat").equals(adapter.getItem(i).toString())) {
                                    selectLoc = i;
                                    break;
                                }
                            }
                            break;
                        case "Create Events - Event Type (!Options)":
                            adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.eventType, android.R.layout.simple_spinner_item);
                            // Set the right option
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (currUser.getSettings().get("eventType").equals(adapter.getItem(i).toString())) {
                                    selectLoc = i;
                                    break;
                                }
                            }
                            break;
                        case "Create Events - Location Type (!Options)":
                            adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.locationType, android.R.layout.simple_spinner_item);
                            // Set the right option
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (currUser.getSettings().get("locationType").equals(adapter.getItem(i).toString())) {
                                    selectLoc = i;
                                    break;
                                }
                            }
                            break;
                        case "Employee Directory - Sort By Last Name (!Options)":
                            adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sortBy, android.R.layout.simple_spinner_item);
                            // Set the right option
                            for (int i = 0; i < adapter.getCount(); i++) {
                                String adapterString = adapter.getItem(i).toString();
                                if (adapterString.equals("A to Z")) {
                                    adapterString = "Alphabetical";
                                } else {
                                    adapterString = "Reverse Alphabetical";
                                }
                                if (currUser.getSettings().get("sortDirectoryBy").equals(adapterString)) {
                                    selectLoc = i;
                                    break;
                                }
                            }
                    }
                    if (adapter == null) { optionSelect.setVisibility(View.GONE); } else {
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        optionSelect.setAdapter(adapter);
                        optionSelect.setSelection(selectLoc);
                    }
                }
            }

            // Get the child's text view
            TextView expandedListTextView = view.findViewById(R.id.settingsExpandedListItem);

            // Make all views gone
            view.findViewById(R.id.optionSelect).setVisibility(View.GONE);
            view.findViewById(R.id.intEntry).setVisibility(View.GONE);
            view.findViewById(R.id.textEntry).setVisibility(View.GONE);
            view.findViewById(R.id.changePasswordBtn).setVisibility(View.GONE);
            view.findViewById(R.id.phoneNumberEntry).setVisibility(View.GONE);

            // Alter the title accordingly
            if (expandedListText.contains("(!Options)")) {
                view.findViewById(R.id.optionSelect).setVisibility(View.VISIBLE);
                // Parse the string
                title = expandedListText.substring(0, expandedListText.indexOf(" (!Options)"));
            } else if (expandedListText.contains("(!IntEntry)")) {
                view.findViewById(R.id.intEntry).setVisibility(View.VISIBLE);
                // Parse the string
                title = expandedListText.substring(0, expandedListText.indexOf(" (!IntEntry)"));
                // Provide the previous value
                title = title + " [" + currUser.getSettings().get("numberOfAnnouncements") + "]";
            } else if (expandedListText.contains("(!Text)")) {
                view.findViewById(R.id.textEntry).setVisibility(View.VISIBLE);
                // Parse the string
                title = expandedListText.substring(0, expandedListText.indexOf(" (!Text)"));
                // Provide the previous value
                title = title + " [" + currUser.getSettings().get("preferredName") + "]";
            } else if (expandedListText.contains("(!Password)")) {
                view.findViewById(R.id.changePasswordBtn).setVisibility(View.VISIBLE);
                // Parse the string
                title = expandedListText.substring(0, expandedListText.indexOf(" (!Password)"));
            } else if (expandedListText.contains("(!Number)")) {
                view.findViewById(R.id.phoneNumberEntry).setVisibility(View.VISIBLE);
                // Parse the string
                title = expandedListText.substring(0, expandedListText.indexOf(" (!Number)"));
                // Provide the previous value
                title = title + " [" + currUser.getNumber() + "]";
            }

            // Set the text on the text view
            expandedListTextView.setText(title);

            // Set on click listeners
            setOnClickListeners(view, expandedListTextView);

            return view;
        }

        @Override
        public boolean isChildSelectable(int listPos, int expandedListPos) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    // Sets on click listeners for the various items in the expandable list view
    private void setOnClickListeners(View view, TextView expandedListTextView) {
        // Calendar format, event type, location type, & directory sort by
        Spinner spinner = view.findViewById(R.id.optionSelect);
        if (spinner != null && spinner.getVisibility() == View.VISIBLE) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    // Get the currently selected item
                    String item = ((Spinner) adapterView.findViewById(R.id.optionSelect)).getSelectedItem().toString();
                    // Get the current settings
                    HashMap<String, String> currSettings = currUser.getSettings();
                    // Check which item is being altered based on the option
                    if (item.equalsIgnoreCase("Week") || item.equalsIgnoreCase("Month")) {
                        // Calendar Format Setting - Change the current HashMap
                        if (!currSettings.get("calendarFormat").equals(item)){
                            settingsChanges.put("calendarFormat", item);
                        }
                    } else if (item.equalsIgnoreCase("Reminder") || item.equalsIgnoreCase("Meeting")) {
                        // Create Events - Event Type Setting - Change the current HashMap
                        if (!currSettings.get("eventType").equals(item)){
                            settingsChanges.put("eventType", item);
                        }
                    } else if (item.equalsIgnoreCase("Online") || item.equalsIgnoreCase("In-Person")) {
                        // Create Events - Location Type Setting - Change the current HashMap
                        if (!currSettings.get("locationType").equals(item)){
                            settingsChanges.put("locationType", item);
                        }
                    } else if (item.equalsIgnoreCase("A to Z") || item.equalsIgnoreCase("Z to A")) {
                        // Employee Directory - Sort By Last Name Setting - Change the current HashMap
                        String compareTo = "Reverse Alphabetical";
                        if (item.equals("A to Z")) {
                            compareTo = "Alphabetical";
                        }
                        if (!currSettings.get("sortDirectoryBy").equals(compareTo)){
                            settingsChanges.put("sortDirectoryBy", compareTo);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do nothing, there will always be one selected
                }
            });
        }

        // Set the text view for the number of announcements
        TextView intEntry = view.findViewById(R.id.intEntry);
        if (intEntry != null && intEntry.getVisibility() == View.VISIBLE) {
            numberOfAnnouncements = intEntry;
            numAnnouncementsTitle = expandedListTextView;
        }

        // Set the text view for the preferred display name
        TextView textEntry = view.findViewById(R.id.textEntry);
        if (textEntry != null && textEntry.getVisibility() == View.VISIBLE) {
            preferredDisplayName = textEntry;
            preferredDisplayNameTitle = expandedListTextView;
        }

        // Set a listener for when the Change Password button gets pressed
        Button changePassword = view.findViewById(R.id.changePasswordBtn);
        if (changePassword != null && changePassword.getVisibility() == View.VISIBLE) {
            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open up the pop up view
                    changePasswordPopUp.show();

                    // Listener for the cancel button in the pop up
                    cancelPasswordChanges.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Reset the error text visibility
                            changePasswordErrText.setVisibility(View.GONE);
                            // Close the pop up and empty the values
                            currentPasswordValue = null; newPasswordValue = null; newPasswordRepeatValue = null;
                            changePasswordPopUp.hide();
                        }
                    });

                    // Listener for the confirm button in the pop up
                    confirmPasswordChanges.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Reset the error text visibility
                            changePasswordErrText.setVisibility(View.GONE);
                            // Confirm that the current password is correct
                            currentPasswordValue = currentPasswordTIL.getEditText().getText().toString();
                            String existingPassword;
                            if (changedPasswordValue == null) {
                                existingPassword = currUser.getPassword();
                            } else {
                                existingPassword = changedPasswordValue;
                            }
                            if (existingPassword.equals(currentPasswordValue)) {
                                // Check if both of the new password values have entries of length 8 or more and are equal
                                newPasswordValue = newPasswordTIL.getEditText().getText().toString();
                                newPasswordRepeatValue = newPasswordRepeatTIL.getEditText().getText().toString();
                                if (newPasswordValue.length() >= 8) {
                                    // Confirm that the passwords are the same
                                    if (newPasswordValue.equals(newPasswordRepeatValue)) {
                                        // Confirm that the new password is not equal to the old one
                                        if (!newPasswordValue.equals(currentPasswordValue)) {
                                            // Password change was successful
                                            Toast.makeText(getApplicationContext(), "The password change was valid.", Toast.LENGTH_SHORT).show();
                                            changedPasswordValue = newPasswordValue;
                                            changePasswordPopUp.hide();
                                        } else {
                                            // Inform the user that their new password cannot match their old password
                                            changePasswordErrText.setText("Your new password cannot match your old password.");
                                            changePasswordErrText.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        // Inform the user that their new passwords do not match
                                        changePasswordErrText.setText("Your new passwords do not match each other.");
                                        changePasswordErrText.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    // Inform the user that their new password is invalid
                                    changePasswordErrText.setText("Your new password must contain 8 or more characters.");
                                    changePasswordErrText.setVisibility(View.VISIBLE);
                                }
                            } else {
                                // Inform the user their current password is wrong
                                changePasswordErrText.setText("Your current password entry is invalid. Your password will not be changed. Please try again.");
                                changePasswordErrText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        }

        // Set a listener for the phone number input
        EditText phone = view.findViewById(R.id.phoneNumberEntry);
        if (phone != null && phone.getVisibility() == View.VISIBLE) {
            phoneNumber = phone;
            phoneNumberTitle = expandedListTextView;

            // Set a listener for text change
            phoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // Get the length of the text value
                    int textLength = phoneNumber.getText().length();
                    String currText = phoneNumber.getText().toString();

                    // Depending on the length of the text and whether it was adding new or deleting old characters, add hyphens
                    // Else ifs ensure hyphens exist on edge cases as well
                    if ((textLength + 1 == 4 || textLength + 1 == 8) && (before - count < 0)) {
                        phoneNumber.setText(String.format("%s-", phoneNumber.getText()));
                    } else if (textLength == 4 && !currText.matches("\\d{3}-") && (before - count < 0)) {
                        phoneNumber.setText(String.format("%s-%s", currText.substring(0, 3), currText.substring(3)));
                    } else if (textLength == 8 && !currText.matches("\\d{3}-\\d{3}-") && (before - count < 0)) {
                        phoneNumber.setText(String.format("%s-%s", currText.substring(0, 7), currText.substring(7)));
                    }

                    // Ensure the cursor is at the end
                    phoneNumber.setSelection(phoneNumber.getText().length());
                }
                @Override
                public void afterTextChanged(Editable editable) { }
            });
        }
    }
}