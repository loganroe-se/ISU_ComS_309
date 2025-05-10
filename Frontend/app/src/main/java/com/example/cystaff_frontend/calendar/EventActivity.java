package com.example.cystaff_frontend.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.CalendarActivity;
import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.GeneralUtilFuncs;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Objects;

public class EventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CalendarAdapter.OnItemListener, EventUserInterface, EventUserDeleteInterface {

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

    // Text input variables
    private TextInputLayout titleInput, inviteeInput, buildingInput, roomInput;
    private TextView inviteeText;
    private String titleValue, inviteeValue, buildingValue, roomValue;
    private ArrayAdapter<String> buildingAdapter, roomAdapter;
    private AutoCompleteTextView buildingAutoComplete, roomAutoComplete;

    // Material group variables
    private MaterialButtonToggleGroup eventTypeSelection, locationTypeSelection;
    private MaterialButton reminderSelection, meetingSelection, onlineSelection, inPersonSelection;

    // Time selection variables
    private Button startDay, endDay, startTime, endTime;

    // Save/cancel buttons
    private Button cancelBtn, saveBtn;

    // Previous day selection
    private LocalDate prevDate;

    // Start/end date holders
    private LocalDate startDate, endDate;

    // Start/end time holders
    private int startHour, startMinute = 0, endHour, endMinute = 0;

    // Keeps track of which button is currently pressed, if any
    // -1 : none --- 0 : start date --- 1 : end date
    private int currButton = -1;

    // Keeps track of which button is currently pressed, if any
    // -1 : none --- 0 : start time --- 2 : end time
    private int currTimePicker = -1;

    // Month view for selecting a date
    private CalendarMonthView calendarMonthView;

    // Time selection variable
    private TimePicker timePicker;

    // Flag to avoid unnecessary listener based changes for the time picker
    private boolean timePickerFlag = false;

    // Progress bar
    private ProgressBar mProgressBar;

    // List of users searched for
    private ArrayList<EventUser> inviteeItems;

    // Recycler view when searching for people to invite
    private RecyclerView inviteeRecycler;
    private EventUserAdapter inviteeAdapter;

    // Currently chosen members to invite
    private ArrayList<EventUser> chosenInvitees;

    // Variables for showing/hiding currently invited members
    private ToggleButton showHideCurrMembers;
    private boolean showHideStatus = false;
    private RecyclerView currMemberRecycler;
    private TextView currMemberText;
    private EventUserDeleteAdapter currMemberAdapter;

    // Store event and location types selected
    private String eventType = "Meeting", locationType = "Online";

    // Error text variable
    private TextView errText;

    // Search results response/error
    private JSONArray searchResultsResponse;
    private VolleyError searchResultsError;

    // Room request variables
    private JSONArray roomReqResponse;
    private VolleyError roomReqError;
    private int roomID = -1;

    // Adding event request variables
    private JSONObject addEventResponse;
    private VolleyError addEventError;
    private String startStr, endStr, details, recipients;

    // Hold the user object for the current user
    private User currUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Set the notification handler's root View
        NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
        temp.setActivity(this);

        // Get the user
        currUser = getIntent().getParcelableExtra("userInfo", User.class);

        // Get the previous date selected
        prevDate = CalendarUtils.selectedDate;

        //Find screen element(s)
        headerTxt = findViewById(R.id.headerText);
        drwLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.header);
        profile = findViewById(R.id.profile);
        titleInput = findViewById(R.id.titleTextLayout);
        inviteeInput = findViewById(R.id.searchTextInputLayout);
        inviteeText = findViewById(R.id.searchTextInput);
        buildingInput = findViewById(R.id.buildingTextLayout);
        buildingAutoComplete = findViewById(R.id.buildingInput);
        roomInput = findViewById(R.id.roomTextLayout);
        roomAutoComplete = findViewById(R.id.roomInput);
        eventTypeSelection = findViewById(R.id.eventTypeSelection);
        reminderSelection = findViewById(R.id.reminderSelection);
        meetingSelection = findViewById(R.id.meetingSelection);
        locationTypeSelection = findViewById(R.id.locationTypeSelection);
        onlineSelection = findViewById(R.id.onlineSelection);
        inPersonSelection = findViewById(R.id.inPersonSelection);
        startDay = findViewById(R.id.startDay);
        endDay = findViewById(R.id.endDay);
        startTime = findViewById(R.id.startTime);
        endTime  = findViewById(R.id.endTime);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        calendarMonthView = findViewById(R.id.calendarMonthViewSelect);
        timePicker = findViewById(R.id.timePicker);
        inviteeRecycler = findViewById(R.id.inviteeRecycler);
        mProgressBar = findViewById(R.id.progressBar);
        errText = findViewById(R.id.errText);
        showHideCurrMembers = findViewById(R.id.showHideCurrMembers);
        currMemberRecycler = findViewById(R.id.currentMembersRecycler);
        currMemberText = findViewById(R.id.currentMemberText);

        // Set the calendar month view's item listener
        calendarMonthView.setItemListener(this);

        // Set up the invitee recycler
        inviteeItems = new ArrayList<EventUser>();
        inviteeRecycler.setLayoutManager(new LinearLayoutManager(this));
        inviteeAdapter = new EventUserAdapter(getApplicationContext(), inviteeItems, this);
        inviteeRecycler.setAdapter(inviteeAdapter);

        // Set up chosen invitee recycler
        chosenInvitees = new ArrayList<EventUser>();
        currMemberRecycler.setLayoutManager(new LinearLayoutManager(this));
        currMemberAdapter = new EventUserDeleteAdapter(getApplicationContext(), chosenInvitees, this);
        currMemberRecycler.setAdapter(currMemberAdapter);

        // Set up drop down menus
        buildingAdapter = new ArrayAdapter<String>(this, R.layout.list_dir_item, Const.buildings);
        buildingAutoComplete.setAdapter(buildingAdapter);
        roomAdapter = new ArrayAdapter<String>(this, R.layout.list_dir_item, Const.rooms);
        roomAutoComplete.setAdapter(roomAdapter);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Set default values
        startDate = CalendarUtils.selectedDate;
        int currHour = LocalTime.now().getHour() + 1;
        if (currHour != 24) { startDay.setText(formatDayMonth(startDate)); }
        String currHourFormat, nextHourFormat;
        if (currHour > 12 && currHour < 24) {
            // PM
            currHourFormat = (currHour - 12) + ":00 PM";
            startHour = currHour;
            nextHourFormat = (currHour - 11) + ":00 PM";
            endHour = currHour + 1;
        } else {
            // AM
            if (currHour == 12) { nextHourFormat = (currHour - 11) + ":00 PM"; } else { nextHourFormat = (currHour + 1) + ":00 AM"; }
            endHour = currHour + 1;
            if (currHour == 24) { currHourFormat = "12:00 AM"; nextHourFormat = "1:00 AM"; endHour = 1; } else { currHourFormat = currHour + ":00 AM"; }
            startHour = currHour;
        }
        startTime.setText(currHourFormat);
        if (currHour == 23) {
            endDay.setText(formatDayMonth(CalendarUtils.selectedDate.plusDays(1)));
            endDate = CalendarUtils.selectedDate.plusDays(1);
            endTime.setText("12:00 AM");
            endHour = 24;
        } else if (currHour == 24) {
            endDay.setText(formatDayMonth(CalendarUtils.selectedDate.plusDays(1)));
            endDate = CalendarUtils.selectedDate.plusDays(1);
            endTime.setText("1:00 AM");
            endHour = 1;
            startDate = startDate.plusDays(1);
            startDay.setText(formatDayMonth(startDate));
            CalendarUtils.selectedDate = startDate;
            calendarMonthView.setMonthView(getApplicationContext());
        } else {
            endDay.setText(formatDayMonth(CalendarUtils.selectedDate));
            endDate = startDate;
            endTime.setText(nextHourFormat);
        }

        // Set default button group selections
        if (currUser.getSettings().get("eventType").equals("Meeting")) {
            eventTypeSelect(R.id.meetingSelection, true);
        } else {
            eventTypeSelect(R.id.reminderSelection, true);
        }
        if (currUser.getSettings().get("locationType").equals("Online")) {
            locationTypeSelect(R.id.onlineSelection, true);
        } else {
            locationTypeSelect(R.id.inPersonSelection, true);
        }

        // Listen for an event type selection to be made
        eventTypeSelection.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                eventTypeSelect(checkedId, isChecked);
            }
        });

        // Listen for a location type selection to be made
        locationTypeSelection.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                locationTypeSelect(checkedId, isChecked);
            }
        });

        // Listen for the start day button to be pressed
        startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a helper to handle this
                handleDaySelection(0);
            }
        });

        // Listen for the end day button to be pressed
        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a helper to handle this
                handleDaySelection(1);
            }
        });

        // Listen for the start time button to be pressed
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the start time and a flag to make the listener ignore this change
                timePickerFlag = true;
                timePicker.setHour(startHour);
                timePicker.setMinute(startMinute);
                timePickerFlag = false;
                // Call a helper to handle this
                handleDaySelection(2);
            }
        });

        // Listen for the end time button to be pressed
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the end time and a flag to make the listener ignore this change
                timePickerFlag = true;
                timePicker.setHour(endHour);
                timePicker.setMinute(endMinute);
                timePickerFlag = false;
                // Call a helper to handle this
                handleDaySelection(3);
            }
        });

        // Listen for the time to be changed
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                // Ensure this is not happening due to a start/endTime button click
                if (timePickerFlag) { return; }
                // Check which button is currently pressed
                if (currTimePicker == 0) {
                    startTime.setText(formatHourMin(hour, minute));
                    // Update start time holders
                    startHour = hour;
                    startMinute = minute;
                } else if (currTimePicker == 1) {
                    endTime.setText(formatHourMin(hour, minute));
                    // Update end time holders
                    endHour = hour;
                    endMinute = minute;
                }
            }
        });

        // Listen for the toggle button to show/hide current chosen members to be pressed
        showHideCurrMembers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // If just checked, show the recycler view
                // Otherwise, hide the recycler view
                if (isChecked && chosenInvitees.size() != 0) {
                    currMemberRecycler.setVisibility(View.VISIBLE);
                    showHideStatus = true;
                } else {
                    currMemberRecycler.setVisibility(View.GONE);
                    showHideStatus = false;
                }
            }
        });

        // Listen for a value to be typed into the invitee box
        inviteeText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                // Send a request to get all results for the current text value when enter key is pressed
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Read text input
                    inviteeValue = Objects.requireNonNull(inviteeInput.getEditText()).getText().toString();

                    // Clear all previous results
                    inviteeItems = new ArrayList<EventUser>();

                    // Hide the keyboard
                    GeneralUtilFuncs.hideKeyboard(EventActivity.this);

                    // If inputs are not empty, call the server
                    if (!inviteeValue.equals("")) {
                        // Start the progress bar
                        mProgressBar.setVisibility(View.VISIBLE);
                        // Make the JSON object request to get search results
                        getSearchResultsReq();
                    }

                    return true;
                }
                return false;
            }
        });

        // Listen for the clearing of text on the invitee text box
        inviteeInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the text and hide the recycler view
                inviteeText.setText("");
                inviteeRecycler.setVisibility(View.GONE);
            }
        });

        // If the cancel button is clicked, return to the calendar screen
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reassign the selected date
                CalendarUtils.selectedDate = prevDate;
                // Create the intent and start the new activity
                Intent i = new Intent(EventActivity.this, CalendarActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            }
        });

        // If the save button is pressed, attempt to add the event after ensuring base requirements are met
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear error text
                errText.setVisibility(View.GONE);

                // String to keep track of the entirety of the error text
                String errTextVal = "";

                // Ensure that there is a title
                titleValue = titleInput.getEditText().getText().toString();
                if (titleValue.equals("")) {
                    errTextVal = "Please enter a value for the title. This is a required input.";
                }

                // Keep track if it is a valid date
                boolean valid = true;

                // Ensure that the given start time is BEFORE the given end time
                if (startDate.isEqual(endDate)) {
                    // Dates are equal, check time
                    if ((startHour == endHour && endMinute <= startMinute) || endHour < startHour) {
                        // Dates are the same AND end time is BEFORE or EQUAL to start time
                        valid = false;
                    }
                } else if (startDate.isAfter(endDate)) {
                    // The start date is after the end date which is invalid
                    valid = false;
                }

                // Update error text
                if (!valid) {
                    if (errTextVal.equals("")) {
                        errTextVal = "Your end date must come after the start date by any amount.";
                    } else {
                        errTextVal = errTextVal + "\nYour end date must come after the start date by any amount.";
                    }
                }

                // Display the error if there is one
                // Otherwise, make the request to add the event
                if (!errTextVal.equals("")) {
                    errText.setText(errTextVal);
                    errText.setVisibility(View.VISIBLE);
                } else {
                    // At this point, all values must be valid, just have to gather them all
                    // Ensure the minute values have two digits
                    String tempStartMin = startMinute + "", tempEndMin = endMinute + "";
                    if (startMinute < 10) { tempStartMin = "0" + tempStartMin; }
                    if (endMinute < 10) { tempEndMin = "0" + tempEndMin; }
                    // Find the date concatenated with the time -- both start and end
                    startStr = startDate.format(new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter()) + " " + LocalTime.parse(startHour + ":" + tempStartMin, DateTimeFormatter.ofPattern("H:mm")).format(DateTimeFormatter.ofPattern("h:mm a"));
                    endStr = endDate.format(new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter()) + " " + LocalTime.parse(endHour + ":" + tempEndMin, DateTimeFormatter.ofPattern("H:mm")).format(DateTimeFormatter.ofPattern("h:mm a"));
                    // Determine the details string -- Includes title + event type + location type
                    details = "(!Title): " + titleValue + " (!EventType): " + eventType + " (!LocationType): " + locationType;
                    // Get userids of all recipients
                    StringBuilder recipientsBuilder = new StringBuilder("&");
                    for (int i = 0; i < chosenInvitees.size(); i++) {
                        recipientsBuilder.append(chosenInvitees.get(i).getUserID()).append(":").append(chosenInvitees.get(i).getName()).append("&");
                    }
                    recipients = String.valueOf(recipientsBuilder);
                    // Show the progress bar
                    mProgressBar.setVisibility(View.VISIBLE);
                    // Get the room id by requesting all buildings and rooms and searching for the correct one
                    // -- Only if location type is in person -- Otherwise set it to 1 --
                    if (locationType.equals("In-Person")) {
                        buildingValue = buildingInput.getEditText().getText().toString();
                        roomValue = roomInput.getEditText().getText().toString();
                        roomID = -1;
                        findRoomReq(buildingValue, Integer.parseInt(roomValue));
                    } else {
                        roomID = 1;
                        // Set status to 1 - approved - and duration to 0
                        int statusInt = 1, duration = 0;
                        // Create JSON objects
                        JSONObject room = new JSONObject(), status = new JSONObject(), user = new JSONObject();
                        try {
                            room.put("roomid", roomID);
                            status.put("statusid", statusInt);
                            user.put("userid", currUser.getUserID());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        // Everything is prepared to request to create the event
                        addEventReq(startStr, endStr, duration, details, recipients, room, status, user);
                    }
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

    // Formats the day and month as follows: Day, Mon Day#
    private String formatDayMonth(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().toString();
        String selMonth = date.getMonth().toString();
        return dayOfWeek.charAt(0) + dayOfWeek.substring(1, 3).toLowerCase() + ", " + selMonth.charAt(0) + selMonth.substring(1, 3).toLowerCase() + " " + date.getDayOfMonth();
    }

    // Formats hours/minutes given a 24-hour based hour int -- XX:XX AM/PM
    private String formatHourMin(int hour, int minute) {
        // Define holder variables
        String AM_PM, newMinute = minute + "";
        int newHour;

        // Ensure that the minutes have two digits at all times
        if (minute < 10) {
            newMinute = "0" + minute;
        }

        // Check if AM or PM
        if (hour == 0) {
            newHour = 12;
            AM_PM = "AM";
        } else if (hour < 12) {
            newHour = hour;
            AM_PM = "AM";
        } else if (hour > 12) {
            newHour = hour - 12;
            AM_PM = "PM";
        } else {
            newHour = 12;
            AM_PM = "PM";
        }
        return newHour + ":" + newMinute + " " + AM_PM;
    }

    // Selects an event type
    private void eventTypeSelect(int checkedId, boolean isChecked) {
        if (isChecked) {
            if (checkedId == R.id.reminderSelection) {
                eventType = "Reminder";
                // Set reminder selection to true and background to gray and others false with background white
                reminderSelection.setChecked(true);
                reminderSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                meetingSelection.setChecked(false);
                meetingSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                // Set invitees box, current member text, and toggle button to gone
                inviteeInput.setVisibility(View.GONE);
                currMemberText.setVisibility(View.GONE);
                showHideCurrMembers.setVisibility(View.GONE);
                if (currMemberRecycler.getVisibility() == View.VISIBLE) { currMemberRecycler.setVisibility(View.GONE); }
            } else if (checkedId == R.id.meetingSelection) {
                eventType = "Meeting";
                // Set meeting selection to true and background to gray and others false with background white
                reminderSelection.setChecked(false);
                reminderSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                meetingSelection.setChecked(true);
                meetingSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                // Set invitees box, current member text, and toggle button to visible
                inviteeInput.setVisibility(View.VISIBLE);
                currMemberText.setVisibility(View.VISIBLE);
                showHideCurrMembers.setVisibility(View.VISIBLE);
                if (showHideStatus) { currMemberRecycler.setVisibility(View.VISIBLE); }
            }
        }
    }

    // Selects a location type
    private void locationTypeSelect(int checkedId, boolean isChecked) {
        if (isChecked) {
            if (checkedId == R.id.onlineSelection) {
                locationType = "Online";
                // Set online selection to true and background to gray and others false with background white
                onlineSelection.setChecked(true);
                onlineSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                inPersonSelection.setChecked(false);
                inPersonSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                // Set building and room selections to gone
                buildingInput.setVisibility(View.GONE);
                roomInput.setVisibility(View.GONE);
            } else if (checkedId == R.id.inPersonSelection) {
                locationType = "In-Person";
                // Set in-person selection to true and background to gray and others false with background white
                onlineSelection.setChecked(false);
                onlineSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                inPersonSelection.setChecked(true);
                inPersonSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                // Set building and room selections to visible
                buildingInput.setVisibility(View.VISIBLE);
                roomInput.setVisibility(View.VISIBLE);
            }
        }
    }

    // Handles the case when either the start or end day button is pressed
    // callLoc -> 0 for startDay and 1 for endDay -- 2 for startTime and 3 for endTime
    private void handleDaySelection(int callLoc) {
        // If being called from a time variable, ensure month view is hidden and invert time visibility
        if (callLoc == 2 || callLoc == 3) {
            // Ensure calendarMonthView is gone and both buttons are white
            calendarMonthView.setVisibility(View.GONE);
            startDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            endDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            // Invert the visibility
            if (timePicker.getVisibility() == View.GONE || ((callLoc - 2) != currTimePicker && currTimePicker != -1)) {
                if (timePicker.getVisibility() == View.GONE) { timePicker.setVisibility(View.VISIBLE); }
                // Update current button pressed - Subtract by two to get it to 0's and 1's
                currTimePicker = callLoc - 2;
                // Change button background colors
                if (currTimePicker == 0) {
                    startTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                    endTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                } else {
                    endTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                    startTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            } else {
                currTimePicker = -1;
                timePicker.setVisibility(View.GONE);
                startTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                endTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        } else {
            // Ensure time view is gone and both buttons are white
            timePicker.setVisibility(View.GONE);
            startTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            endTime.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            // Invert the visibility and, if now being made visible, select the proper date
            if (calendarMonthView.getVisibility() == View.GONE || (callLoc != currButton && currButton != -1)) {
                if (calendarMonthView.getVisibility() == View.GONE) { calendarMonthView.setVisibility(View.VISIBLE); }
                // Update current button pressed
                currButton = callLoc;
                // Change selected date and button background colors
                if (callLoc == 0) {
                    CalendarUtils.selectedDate = startDate;
                    startDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                    endDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                } else {
                    CalendarUtils.selectedDate = endDate;
                    endDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                    startDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
                calendarMonthView.setMonthView(getApplicationContext());
            } else {
                currButton = -1;
                calendarMonthView.setVisibility(View.GONE);
                endDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                startDay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }

        }
    }

    /**
     * When a calendar's item gets clicked
     * @param position - The position of the date clicked
     * @param date - The date clicked
     * @param context - The context of the date clicked
     */
    @Override
    public void onItemClick(int position, LocalDate date, Context context) {
        // Update selected date
        if (date != null) {
            CalendarUtils.selectedDate = date;
            calendarMonthView.setMonthView(context);

            // If the left button is the current one, update start date, else, update end date
            if (currButton == 0) {
                startDate = date;
                // Change the start day's button text
                startDay.setText(formatDayMonth(date));
            } else if (currButton == 1) {
                endDate = date;
                // Change the end day's button text
                endDay.setText(formatDayMonth(date));
            }
        }
    }

    /**
     * When an event user item gets clicked -- used for adding/removing people to/from a meeting
     * @param position - The position of the user clicked
     */
    @Override
    public void onItemClick(int position) {
        // Get the chosen user
        EventUser item = inviteeItems.get(position);

        // Loop through and check for duplicates
        boolean containsItem = false;
        for (int i = 0; i < chosenInvitees.size(); i++) {
            if (chosenInvitees.get(i).getUserID() == item.getUserID()) {
                containsItem = true;
                break;
            }
        }

        // Ensure the attempted invite is not for the user creating the event
        if (currUser.getUserID() != item.getUserID()) {
            // Add the user to the list of total users invited if not already in it
            if (!containsItem) {
                chosenInvitees.add(item);

                // Update the text to resemble the number of current invitees
                currMemberText.setText("Current Invited Members (" + chosenInvitees.size() + "):");

                // If the recycler view is currently visible, update the list
                if (currMemberRecycler.getVisibility() == View.VISIBLE) {
                    // Create a deep copy to avoid bugs
                    ArrayList<EventUser> temp = new ArrayList<>(chosenInvitees);
                    currMemberAdapter.updateUserList(temp);
                }
            } else {
                // Inform the user they chose a duplicate user to add to the meeting
                Toast.makeText(getApplicationContext(), "This is a duplicate user, so it will not be added.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Inform the user they chose a themselves to add to the meeting
            Toast.makeText(getApplicationContext(), "You cannot add yourself to an event as you are already added by default.", Toast.LENGTH_LONG).show();
        }


        // Clear the current search result
        inviteeText.setText("");

        // Hide the recycler view & clear previous results
        inviteeRecycler.setVisibility(View.GONE);
        inviteeItems.clear();
        inviteeAdapter.updateUserList(inviteeItems);
    }

    // Used to request the users relating to the entered value
    private void getSearchResultsReq() {
        // Generate the path string
        String URL = Const.serverURL + "searchUser?name=" + inviteeValue;

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("Volley Response", response.toString());
                    searchResultsResponse = response;
                    handleSearchResultsResponse();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", "Error: " + error.getMessage());
                    searchResultsError = error;
                    handleSearchResultsResponse();
                }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    // Handles the JSON response from getSearchResultsReq
    private void handleSearchResultsResponse() {
        // Disable the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Reset the error text
        errText.setVisibility(View.GONE);

        // If there is a response, read it and output user information
        // Otherwise, if there is no error, report that there were no results to the search
        // Otherwise, provide an error that the server is currently unreachable
        if (searchResultsResponse != null && searchResultsResponse.length() != 0) {
            // Read the user information
            try {
                // Iterate through the user information in the response array
                for (int i = 0; i < searchResultsResponse.length(); i++) {
                    JSONObject currItem = searchResultsResponse.getJSONObject(i);

                    // Create a new event user item
                    //EventUser item = new EventUser(currItem.getInt("userID"), currItem.getString("firstName") + currItem.getString("lastName"), currItem.getString("email"), (int) currItem.get("image"));
                    EventUser item = new EventUser(currItem.getInt("userid"), currItem.getString("firstName") + " " + currItem.getString("lastName"), currItem.getString("email"), R.drawable.profile);

                    // Add the user to the list of users
                    inviteeItems.add(item);

                    // Notify the adapter of the new addition
                    inviteeAdapter.notifyItemInserted(inviteeItems.size());
                }

                // Notify the adapter to update the listing
                inviteeAdapter.updateUserList(inviteeItems);

                // Make the items visible
                inviteeRecycler.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                // If an error occurs, inform the user that there was a problem in loading the data
                errText.setText("Error: There was an issue when reading the user information.");
                errText.setVisibility(View.VISIBLE);
            }
        } else if (searchResultsError == null) {
            // There were no results found given the search
            errText.setText("No results were found for the given search. Please try again.");
            errText.setVisibility(View.VISIBLE);
        } else {
            // Update the error message
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When the clear button is pressed on an already added user to delete them
     * @param position - The position of the user to be removed
     */
    @Override
    public void onEndItemClick(int position) {
        // Remove the item from the current list
        chosenInvitees.remove(position);

        // Update the text to resemble the number of current invitees
        currMemberText.setText("Current Invited Members (" + chosenInvitees.size() + "):");

        // Create a deep copy to avoid bugs and update the recycler view
        ArrayList<EventUser> temp = new ArrayList<>(chosenInvitees);
        currMemberAdapter.updateUserList(temp);
    }

    // Finds the room given the building and room values
    private void findRoomReq(String building, int room) {
        // Generate the path string
        String URL = Const.serverURL + "room";

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("Volley Response", response.toString());
                    roomReqResponse = response;
                    handleRoomReqResponse(building, room);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", "Error: " + error.getMessage());
                    roomReqError = error;
                    handleRoomReqResponse(building, room);
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    // Handles the room request response
    private void handleRoomReqResponse(String building, int roomInt) {
        // Reset the error text
        errText.setVisibility(View.GONE);

        // If there is a response, read it and output user information
        // Otherwise, if there is no error, report that there were no results to the search
        // Otherwise, provide an error that the server is currently unreachable
        if (roomReqResponse != null && roomReqResponse.length() != 0) {
            // Read the user information
            try {
                // Iterate through the user information in the response array
                for (int i = 0; i < roomReqResponse.length(); i++) {
                    JSONObject currRoom = roomReqResponse.getJSONObject(i);
                    JSONObject currBuilding = currRoom.getJSONObject("building");

                    if (currBuilding.getString("name").equals(building) && currRoom.getInt("roomNumber") == roomInt) {
                        // Found a matching building/room
                        roomID = currRoom.getInt("roomid");
                        break;
                    }
                }
            } catch (Exception e) {
                // If an error occurs, inform the user that there was a problem in loading the data
                errText.setText("Error: There was an issue when reading the room information.");
                errText.setVisibility(View.VISIBLE);
            }
        } else {
            // Update the error message
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }

        // If the roomID is equal to -1 then there was an error when finding rooms, cancel request
        if (roomID == -1) {
            errText.setText("The room requested could not be found. Please try again.");
            errText.setVisibility(View.VISIBLE);
        } else {
            // Set status to 1 - approved - and duration to 0
            int statusInt = 1, duration = 0;
            // Create JSON objects
            JSONObject room = new JSONObject(), status = new JSONObject(), user = new JSONObject();
            try {
                room.put("roomid", roomID);
                status.put("statusid", statusInt);
                user.put("userid", currUser.getUserID());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // Everything is prepared to request to create the event
            addEventReq(startStr, endStr, duration, details, recipients, room, status, user);
        }
    }

    // Request to add an event
    private void addEventReq(String startTime, String endTime, int duration, String details, String recipients, JSONObject room, JSONObject status, JSONObject user) {
        // Create a JSONObject
        JSONObject postBody = new JSONObject();

        // Try to generate the JSONObject
        try {
            // Provide all of the given values
            postBody.put("startTime", startTime);
            postBody.put("endTime", endTime);
            postBody.put("duration", duration);
            postBody.put("details", details);
            postBody.put("recipients", recipients);
            postBody.put("room", room);
            postBody.put("status", status);
            postBody.put("user", user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate the path string
        String URL = Const.serverURL + "meeting";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, postBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Volley Response", response.toString());
                    addEventResponse = response;
                    try {
                        handleAddEventResponse();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", "Error: " + error.getMessage());
                    addEventError = error;
                    try {
                        handleAddEventResponse();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    // Handles the add event request response
    private void handleAddEventResponse() throws JSONException {
        // Hide the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Reset the error text
        errText.setVisibility(View.GONE);

        // If there is a response and it is a success, go back to the calendar activity
        // Otherwise, if there is a response and it is a fail, inform the user
        // Otherwise, provide an error that the server is currently unreachable
        if (addEventResponse != null) {
            // Get the string result from the JSONObject
            String status = addEventResponse.getString("status");
            if (status.equals("Success")) {
                // Inform the user the event was added
                Toast.makeText(getApplicationContext(), "Your event has successfully been added!", Toast.LENGTH_LONG).show();
                // Return to the calendar page - Ensure the soft keyboard is closed
                GeneralUtilFuncs.hideKeyboard(this);
                Intent i = new Intent(EventActivity.this, CalendarActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            } else {
                // Inform the user that the event failed to add
                errText.setText("The event failed to be created. Please try again.");
                errText.setVisibility(View.VISIBLE);
            }
        } else {
            // Update the error message
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}