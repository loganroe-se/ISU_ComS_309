package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class defining the behaviour of the home page
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Holds the TextViews used on the page
    private TextView headerTxt, announcementTxt, errText, loggedInTxt, welcomeTxt;

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

    //Holds the reference to the header's profile button
    private Spinner profile;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        errText = findViewById(R.id.errorText);
        announcementTxt = findViewById(R.id.announcementsText);
        profile = findViewById(R.id.profile);
        loggedInTxt = findViewById(R.id.loggedInText);
        welcomeTxt = findViewById(R.id.welcomeText);

        //Set the toolbar as the page's action bar
        setSupportActionBar(toolbar);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        //Set the welcome message
        welcomeTxt.setText("Welcome, " + currUser.getSettings().get("preferredName"));

        //Set the last logged in text
        loggedInTxt.setText("Last Logged In: " + currUser.getLastLogin());

        //Make a call to fetch the information for the different sections
        makeJsonObjReq();
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
     * Method to make the GET requests to the server.
     */
    private void makeJsonObjReq() {
        //Construct the String for the GET request
        String REQ_URL = Const.serverURL + "announcement";

        //Instantiate the GET JsonArrayRequest
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, REQ_URL, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        JSONResponse = response;
                        handleResponse();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                JSONError = error;
                handleResponse();
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    /**
     * Method used to handle the response or error from the server GET requests.
     */
    private void handleResponse() {
        //Reset the error text
        errText.setVisibility(View.INVISIBLE);

        //Declare variables for handling
        JSONObject announcement;
        JSONObject user;

        //A response exists, display the announcements
        if (JSONResponse != null) {
            //Attempt to read the announcements
            try {
                //Reset the announcement TextView
                announcementTxt.setText("");
                //Read through each announcement in the JSONArray
                for (int i = JSONResponse.length() - 1; i >= 0 && i > JSONResponse.length() - 4; i--) {
                    //Read the announcement and add it to the menu
                    announcement = (JSONObject) JSONResponse.get(i);
                    user = (JSONObject) announcement.get("user");
                    if (i != JSONResponse.length() - 1) {
                        announcementTxt.append("\n");
                    }
                    announcementTxt.append(user.get("firstName") + " " + user.get("lastName") + " : " + announcement.get("message") + " (At " + announcement.get("date") + " " + announcement.get("time") + ")");
                    if (i != 0 && i != JSONResponse.length() - 3) {
                        announcementTxt.append("\n");
                    }
                }
            } catch (Exception e) {
                //Catch any errors that might occur with reading the data
                errText.setText("Error: Problem when reading the announcement data.");
                errText.setVisibility(View.VISIBLE);
            }
        }
        //Otherwise, there was an error
        else {
            //Update the error message
            errText.setText("Unable to reach the server. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}