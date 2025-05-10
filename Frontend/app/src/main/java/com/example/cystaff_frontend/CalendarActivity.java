package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.calendar.CalendarAdapter;
import com.example.cystaff_frontend.calendar.CalendarMonthView;
import com.example.cystaff_frontend.calendar.CalendarUtils;
import com.example.cystaff_frontend.calendar.CalendarWeekView;
import com.example.cystaff_frontend.calendar.EventActivity;
import com.example.cystaff_frontend.calendar.EventDisplayAdapter;
import com.example.cystaff_frontend.calendar.EventDisplayInterface;
import com.example.cystaff_frontend.calendar.EventItem;
import com.example.cystaff_frontend.calendar.RoomItem;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.manage.AddUserActivity;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/**
 * Class defining the behaviour of the calendar page
 */
public class CalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EventDisplayInterface, CalendarAdapter.OnItemListener {

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

    // Calendar views
    private CalendarMonthView calendarMonthView;
    private CalendarWeekView calendarWeekView;

    // Material group button for calendar view type selection
    private MaterialButtonToggleGroup calendarViewSelectionGroup;
    private MaterialButton weekSelection, monthSelection;

    // Variables for event display
    private TextView eventDisplayText;
    private RecyclerView eventDisplayRecycler;
    private EventDisplayAdapter eventDisplayAdapter;
    private ArrayList<EventItem> eventItems;

    // Variables for event list request
    private JSONArray eventReqResponse = null;
    private VolleyError eventReqError = null;

    // Variables for room request response
    private JSONArray roomReqResponse = null;
    private VolleyError roomReqError = null;

    // New event variables
    private Button newEventBtn;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

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
        calendarViewSelectionGroup = findViewById(R.id.calendarViewSelection);
        weekSelection = findViewById(R.id.weekSelection);
        monthSelection = findViewById(R.id.monthSelection);
        calendarMonthView = findViewById(R.id.mainCalendarMonthView);
        calendarWeekView = findViewById(R.id.mainCalendarWeekView);
        newEventBtn = findViewById(R.id.newEventBtn);
        eventDisplayText = findViewById(R.id.eventDisplayText);
        eventDisplayRecycler = findViewById(R.id.eventDisplayRecycler);

        // Set the calendar month and week view's item listeners
        calendarMonthView.setItemListener(this);
        calendarWeekView.setItemListener(this);

        // Set up the event display recycler
        eventDisplayRecycler.setLayoutManager(new LinearLayoutManager(this));
        eventDisplayAdapter = new EventDisplayAdapter(getApplicationContext(), new ArrayList<EventItem>(), this);
        eventDisplayRecycler.setAdapter(eventDisplayAdapter);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Request a list of events for the current user
        getEventsReq();

        // Request all of the buildings
        getRoomsReq();

        // Set the proper default selection
        if (currUser.getSettings().get("calendarFormat").equals("Month")) {
            calendarViewSelect(R.id.monthSelection, true);
        } else {
            calendarViewSelect(R.id.weekSelection, true);
        }

        // Listen for a selection to be made
        calendarViewSelectionGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                calendarViewSelect(checkedId, isChecked);
            }
        });

        // Listen for a new event to be created and enter that activity
        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the intent and start the new activity
                Intent i = new Intent(CalendarActivity.this, EventActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
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

    // Helper function for choosing day/week/month calendar views
    private void calendarViewSelect(int checkedId, boolean isChecked) {
        if (isChecked) {
            if (checkedId == R.id.weekSelection) {
                // Set week selection to true and background to gray and others false with background white
                weekSelection.setChecked(true);
                weekSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                monthSelection.setChecked(false);
                monthSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                // Set week calendar to visible and others to gone
                calendarWeekView.setVisibility(View.VISIBLE);
                calendarMonthView.setVisibility(View.GONE);
                // Update the week view
                calendarWeekView.setWeekView(getApplicationContext());
            } else if (checkedId == R.id.monthSelection) {
                // Set month selection to true and background to gray and others false with background white
                monthSelection.setChecked(true);
                monthSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
                weekSelection.setChecked(false);
                weekSelection.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                // Set month calendar to visible and others to gone
                calendarWeekView.setVisibility(View.GONE);
                calendarMonthView.setVisibility(View.VISIBLE);
                // Update the month view
                calendarMonthView.setMonthView(getApplicationContext());
            }
        }
    }

    // Request method to get all of the events related to the current user
    private void getEventsReq() {
        // Generate the path string
        String URL = Const.serverURL + "searchMeeting?userid=" + currUser.getUserID();

        // Create the request
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        eventReqResponse = response;
                        handleEventReqResponse();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                eventReqError = error;
                handleEventReqResponse();
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    // Handles the response from the get events request
    private void handleEventReqResponse() {
        // If there is a response, read it and store the events
        // Otherwise, if there are no events, but no error, then inform the user no events yet
        // Otherwise, inform the user that no events could be found due to server error
        if (eventReqResponse != null && eventReqResponse.length() != 0) {
            // Read the event information
            try {
                // Clear all events
                CalendarUtils.allEvents.clear();
                // Iterate through the event information in the response array
                for (int i = 0; i < eventReqResponse.length(); i++) {
                    // Get the current event
                    JSONObject currEvent = eventReqResponse.getJSONObject(i);

                    // Read the information and pull the wanted information out into local variables
                    int meetingID = currEvent.getInt("meetingid");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/d/yyyy");
                    LocalDate startDay = LocalDate.parse(currEvent.getString("startTime").substring(0, currEvent.getString("startTime").indexOf(" ")), dtf);
                    LocalDate endDay = LocalDate.parse(currEvent.getString("endTime").substring(0, currEvent.getString("endTime").indexOf(" ")), dtf);
                    String startTime = currEvent.getString("startTime").substring(currEvent.getString("startTime").indexOf(" ") + 1);
                    String endTime = currEvent.getString("endTime").substring(currEvent.getString("endTime").indexOf(" ") + 1);
                    int duration = 0;
                    String details = currEvent.getString("details");
                    String title = details.substring(details.indexOf("(!Title): ") + 10, details.indexOf(" (!EventType): "));
                    String eventType = details.substring(details.indexOf(" (!EventType): ") + 15, details.indexOf(" (!LocationType): "));
                    String locationType = details.substring(details.indexOf(" (!LocationType): ") + 18);
                    String currRecipients = currEvent.getString("recipients");
                    currRecipients = currRecipients.substring(1); // Remove the leading ampersand
                    int numAmpersands = (int) currRecipients.chars().filter(ch -> ch == '&').count();
                    int[] recipientIDs = new int[numAmpersands];
                    String[] recipientNames = new String[numAmpersands];
                    for (int j = 0; j < numAmpersands; j++) {
                        String currName = currRecipients.substring(0, currRecipients.indexOf("&"));
                        recipientIDs[j] = Integer.parseInt(currName.substring(0, currName.indexOf(":")));
                        recipientNames[j] = currName.substring(currName.indexOf(":") + 1);
                        currRecipients = currRecipients.substring(currRecipients.indexOf("&") + 1);
                    }
                    int roomID = currEvent.getJSONObject("room").getInt("roomid");
                    int statusID = currEvent.getJSONObject("status").getInt("statusid");
                    JSONObject userJSON = currEvent.getJSONObject("user");
                    int userID = userJSON.getInt("userid");
                    String creatorName = userJSON.getString("firstName") + " " + userJSON.getString("lastName");

                    // Create a new event item and add it to the list of events
                    CalendarUtils.allEvents.add(new EventItem(meetingID, startDay, endDay, startTime, endTime, duration, details, title, eventType, locationType, recipientIDs, recipientNames, roomID, statusID, userID, creatorName));
                }

                // Update the calendar view
                if (calendarMonthView.getVisibility() == View.VISIBLE) {
                    calendarMonthView.setMonthView(getApplicationContext());
                } else {
                    calendarWeekView.setWeekView(getApplicationContext());
                }

                // Ensure the events are showing if there are any on the selected date
                if (CalendarUtils.findEventsByDate(CalendarUtils.selectedDate).size() != 0) {
                    eventDisplayAdapter.updateEventList(new ArrayList<>(CalendarUtils.findEventsByDate(CalendarUtils.selectedDate)));
                    eventDisplayText.setText(CalendarUtils.selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
                    eventDisplayText.setVisibility(View.VISIBLE);
                    eventDisplayRecycler.setVisibility(View.VISIBLE);
                } else {
                    eventDisplayText.setVisibility(View.GONE);
                    eventDisplayRecycler.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                // If an error occurs, inform the user
                Toast.makeText(getApplicationContext(), "There was an error. No events will show up on your calendar.", Toast.LENGTH_LONG).show();
            }
        } else if (eventReqError == null) {
            // Inform the user they have no events
            Toast.makeText(getApplicationContext(), "You currently have no events on your calendar.", Toast.LENGTH_LONG).show();
        } else {
            // Inform the user the server seems to be down
            Toast.makeText(getApplicationContext(), "There was an error. No events will show up on your calendar.", Toast.LENGTH_LONG).show();
        }
    }

    // Requests all room information - contains building and room
    private void getRoomsReq() {
        // Generate the path string
        String URL = Const.serverURL + "room";

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        roomReqResponse = response;
                        handleRoomReqResponse();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                roomReqError = error;
                handleRoomReqResponse();
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    // Handles the room request response
    private void handleRoomReqResponse() {
        // If there is a response, read it and store room information
        // Otherwise, provide an error that the server is currently unreachable
        if (roomReqResponse != null && roomReqResponse.length() != 0) {
            // Read the user information
            try {
                // Iterate through the room information in the response array
                for (int i = 0; i < roomReqResponse.length(); i++) {
                    JSONObject currRoom = roomReqResponse.getJSONObject(i);
                    JSONObject currBuilding = currRoom.getJSONObject("building");

                    // Add the new room item to the list
                    CalendarUtils.allRooms.add(new RoomItem(currRoom.getInt("roomid"), currBuilding.getString("name"), currRoom.getInt("roomNumber")));
                }
            } catch (Exception e) {
                // If an error occurs, inform the user that there was a problem in loading the data
                Toast.makeText(getApplicationContext(), "There was an error when reading room information.", Toast.LENGTH_LONG).show();
            }
        } else {
            // Inform the user there will be no room information on meetings;
            Toast.makeText(getApplicationContext(), "There was an error. There will be no room information on events.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When clear button is pressed on a displayed event
     *
     * @param position - The position of the event in the recycler
     */
    @Override
    public void onEndItemClick(int position) {
        // Get the event
        EventItem item = eventItems.get(position);

        // If the person attempting to delete it is the creator, allow them to, otherwise, don't allow it
        if (item.getUserID() == currUser.getUserID()) {
            // Generate the path string
            String URL = Const.serverURL + "meeting?meetingid=" + item.getMeetingID();

            // Call the server to remove the item
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Volley Response", response.toString());

                            // Get the response value from the JSON object
                            String responseVal = "Failure";
                            try {
                                responseVal = response.getString("status");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Possible Responses:
                            // Failure - Failed for some reason
                            // Success - Event was removed
                            if (responseVal.equalsIgnoreCase("Success")) {
                                // Remove the item from the list of events
                                eventItems.remove(item);
                                CalendarUtils.allEvents.remove(item);

                                // Create a deep copy to avoid bugs and update the recycler view
                                ArrayList<EventItem> temp = new ArrayList<>(eventItems);
                                eventDisplayAdapter.updateEventList(temp);

                                // Update the calendar
                                if (calendarMonthView.getVisibility() == View.VISIBLE) {
                                    calendarMonthView.setMonthView(getApplicationContext());
                                } else {
                                    calendarWeekView.setWeekView(getApplicationContext());
                                }

                                // Inform the user that the item was removed
                                Toast.makeText(getApplicationContext(), "The event has been successfully removed!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Inform the user that the event was unable to be deleted
                                Toast.makeText(getApplicationContext(), "Failed to delete the event. If the problem persists, please contact IT.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", "Error: " + error.getMessage());

                    // Inform the user that the user unable to be deleted and close the pop ups
                    Toast.makeText(getApplicationContext(), "Failed to delete the event. If the problem persists, please contact IT.", Toast.LENGTH_SHORT).show();
                }
            });

            // Adding request to request queue
            AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
        } else {
            Toast.makeText(getApplicationContext(), "You are not the creator so you cannot delete this event.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When a calendar's item gets clicked
     *
     * @param position - The position of the date clicked
     * @param date     - The date clicked
     * @param context  - The context of the date clicked
     */
    @Override
    public void onItemClick(int position, LocalDate date, Context context) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            // Update the proper view
            if (calendarMonthView.getVisibility() == View.VISIBLE) {
                calendarMonthView.setMonthView(context);
            } else {
                calendarWeekView.setWeekView(context);
            }

            // Clear the event items list
            eventItems = new ArrayList<EventItem>();

            // Check if the date has any events, if so, display them
            eventItems = CalendarUtils.findEventsByDate(date);
            if (eventItems.size() != 0) {
                eventDisplayAdapter.updateEventList(new ArrayList<>(eventItems));
                eventDisplayText.setText(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
                eventDisplayText.setVisibility(View.VISIBLE);
                eventDisplayRecycler.setVisibility(View.VISIBLE);
            } else {
                eventDisplayText.setVisibility(View.GONE);
                eventDisplayRecycler.setVisibility(View.GONE);
            }
        }
    }
}