package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.time.CheckingActivity;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Class defining the behaviour of the time tracking page
 */
public class TimeTrackingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Holds the button instances for selecting a week
    private ImageButton leftBtn, rightBtn;

    // Holds the button instance for adding time
    private Button addBtn;

    // Holds the TextViews used on the page
    private TextView headerTxt, sunTxt, monTxt, tueTxt, wedTxt, thuTxt, friTxt, satTxt, weekTxt, regTime, overTime, totalTime, errTxt;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar;

    // Create the response JSON array
    private JSONArray JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Holds the reference to the header's profile button
    private Spinner profile;

    // Hold the user object for the current user
    private User currUser;

    // Holds the calendar instance used for displaying the weeks
    private Calendar c;

    // The formatted date as a string for the current week that is being viewed
    String date;

    // Integers storing the number of hours worked on those days respectively
    private int monT, tueT, wedT, thuT, friT, satT, sunT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_tracking);

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
        errTxt = findViewById(R.id.errorText);
        leftBtn = findViewById(R.id.leftButton);
        rightBtn = findViewById(R.id.rightButton);
        addBtn = findViewById(R.id.addHoursButton);
        sunTxt = findViewById(R.id.sundayHours);
        monTxt = findViewById(R.id.mondayHours);
        tueTxt = findViewById(R.id.tuesdayHours);
        wedTxt = findViewById(R.id.wednesdayHours);
        thuTxt = findViewById(R.id.thursdayHours);
        friTxt = findViewById(R.id.fridayHours);
        satTxt = findViewById(R.id.saturdayHours);
        weekTxt = findViewById(R.id.weekNum);
        regTime = findViewById(R.id.regularVal);
        overTime = findViewById(R.id.overtimeVal);
        totalTime = findViewById(R.id.totalVal);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        //Set to date to the Monday of the current week
        c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(c.getTime());

        //Set the week number to the current date
        weekTxt.setText(date);

        //Set the on click listener for the left week selector button
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move back a week
                c.add(Calendar.DAY_OF_WEEK, -7);
                //Get the new date and set the weekTxt
                date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(c.getTime());
                weekTxt.setText(date);
                //Get the new week's data
                getWeek();
            }
        });

        //Set the on click listener for the right week selector button
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move forward a week
                c.add(Calendar.DAY_OF_WEEK, 7);
                //Get the new date and set the weekTxt
                date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(c.getTime());
                weekTxt.setText(date);
                //Get the new week's data
                getWeek();
            }
        });

        //Set the on click listener for the add hours button
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TimeTrackingActivity.this, CheckingActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            }
        });

        //Make the GET requests
        getWeek();
    }

    /**
     * Show the times in the table
     */
    private void displayTimes() {
        //Set the days in the table
        monTxt.setText(monT + "");
        tueTxt.setText(tueT + "");
        wedTxt.setText(wedT + "");
        thuTxt.setText(thuT + "");
        friTxt.setText(friT + "");
        satTxt.setText(satT + "");
        sunTxt.setText(sunT + "");

        //Initialise the sums
        int regSum = 0;
        int overSum = 0;

        //Testing if a week day is overtime or not
        if (monT >= 10) {
            overSum += monT - 9;
            regSum += monT - (monT - 9);
        } else {
            regSum += monT;
        }
        if (tueT >= 10) {
            overSum += tueT - 9;
            regSum += tueT - (tueT - 9);
        } else {
            regSum += tueT;
        }
        if (wedT >= 10) {
            overSum += wedT - 9;
            regSum += wedT - (wedT - 9);
        } else {
            regSum += wedT;
        }
        if (thuT >= 10) {
            overSum += thuT - 9;
            regSum += thuT - (thuT - 9);
        } else {
            regSum += thuT;
        }
        if (friT >= 10) {
            overSum += friT - 9;
            regSum += friT - (friT - 9);
        } else {
            regSum += friT;
        }

        //Generate the sums
        overSum += satT + sunT;
        int totalSum = regSum + overSum;

        //Set the totals
        regTime.setText(regSum + "");
        overTime.setText(overSum + "");
        totalTime.setText(totalSum + "");
    }

    /**
     * Used to determine which day has been selected
     *
     * @param id - the id of the selected day
     */
    private void traceDay(int id, int hours) {
        switch (id) {
            case 0:
                sunT = hours;
                break;
            case 1:
                monT = hours;
                break;
            case 2:
                tueT = hours;
                break;
            case 3:
                wedT = hours;
                break;
            case 4:
                thuT = hours;
                break;
            case 5:
                friT = hours;
                break;
            case 6:
                satT = hours;
                break;
            default:
                break;
        }

        displayTimes();
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

    /**
     * Method used to get the hours for the week
     */
    private void getWeek() {
        //Reset error text
        errTxt.setVisibility(View.INVISIBLE);

        c.add(Calendar.DAY_OF_WEEK, -1);
        for (int i = 0; i < 7; i++) {
            String weekDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(c.getTime());
            makeJsonObjReq(weekDate, i);
            c.add(Calendar.DAY_OF_WEEK, 1);
        }
        c.add(Calendar.DAY_OF_WEEK, -6);
    }

    /**
     * Method to make the GET requests to the server.
     */
    private void makeJsonObjReq(String day, int dayID) {
        //Construct the String for the GET request
        String REQ_URL = Const.serverURL + "hour/search?date=" + day + "&userid=" + currUser.getUserID();

        Log.d("TEST", REQ_URL);

        //Instantiate the GET JsonArrayRequest
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, REQ_URL, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        JSONResponse = response;
                        handleResponse(dayID);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                Log.e("TEST", "Code: " + error.networkResponse.statusCode);
                JSONError = error;
                handleResponse(dayID);
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    /**
     * Method used to handle the response or error from the server GET requests.
     */
    private void handleResponse(int day) {
        if (JSONResponse != null) {
            Log.d("TEST", JSONResponse.toString());

            int hours = 0;

            if (JSONResponse.length() != 0) {
                try {
                    JSONObject temp = (JSONObject) JSONResponse.get(0);
                    hours = (int) temp.get("hoursNum");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            traceDay(day, hours);
        } else {
            Log.d("TEST", JSONError.toString());
            errTxt.setText("There was a problem connecting to the server. Please try again later");
            errTxt.setVisibility(View.VISIBLE);
        }
    }
}