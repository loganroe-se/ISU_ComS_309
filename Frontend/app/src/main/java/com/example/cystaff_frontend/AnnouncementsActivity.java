package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cystaff_frontend.announcements.Announcement;
import com.example.cystaff_frontend.announcements.AnnouncementAdapter;
import com.example.cystaff_frontend.announcements.CreateAnnouncementActivity;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class defining the behaviour of the announcements page
 */
public class AnnouncementsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Holds the TextViews used on the page
    private TextView headerTxt, errText;

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

    //Holds the announcements RecyclerView
    private RecyclerView normal_recycler;

    //Holds the Object associated with the announcements
    private AnnouncementAdapter adapter;
    private ArrayList<Announcement> announcementArrayList;

    //Holds the reference to the header's profile button
    private Spinner profile;

    //Holds the add announcement button
    private Button addBtn;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

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
        normal_recycler = findViewById(R.id.recycler);
        profile = findViewById(R.id.profile);
        addBtn = findViewById(R.id.addAnnouncementBtn);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        //Set up the list of announcements
        normal_recycler.setLayoutManager(new LinearLayoutManager(this));
        announcementArrayList = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementArrayList);
        normal_recycler.setAdapter(adapter);

        //Checking if the add announcements button should be available
        if (currUser.getPrivilegeID() != 3) {
            //Set the button to visible
            addBtn.setVisibility(View.VISIBLE);

            //Add a listener for the add announcements button
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AnnouncementsActivity.this, CreateAnnouncementActivity.class);
                    i.putExtra("userInfo", currUser);
                    startActivity(i);
                }
            });
        }

        //Make a call to fetch the information and display the announcements
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

        //A response exists, display the announcements
        if (JSONResponse != null) {
            //Attempt to read the announcements
            try {
                // Get the settings value
                int numAnnouncementsToDisplay = Integer.parseInt(currUser.getSettings().get("numberOfAnnouncements"));
                int iterator = JSONResponse.length() - numAnnouncementsToDisplay;
                if (numAnnouncementsToDisplay > JSONResponse.length()) {
                    iterator = 0;
                }
                //Read through each announcement in the JSONArray
                for (int i = JSONResponse.length() - 1; i >= iterator; i--) {
                    //Call the Method to add the announcement to the list
                    createListData((JSONObject) JSONResponse.get(i), i);
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

    /**
     * Method used to add announcements into the announcement ArrayList that serves as the data for the RecyclerView.
     *
     * @param announcementObj - The Announcement JSONObject to add to the announcementArrayList
     * @param i               - The loop iterator, used to determine the announcement number
     * @throws Exception - An Exception resulting from being unable to properly read from the announcement JSONObject
     */
    private void createListData(JSONObject announcementObj, int i) throws Exception {
        //Preparing the Announcement Title
        String title = "Announcement #" + (i + 1);

        //Declare and initialise variable for handling
        JSONObject user = (JSONObject) announcementObj.get("user");

        //Construct a new Announcement Object
        Announcement announcement = new Announcement(title, user.get("firstName").toString(), user.get("lastName").toString(), announcementObj.get("message").toString(), announcementObj.get("time").toString(), announcementObj.get("date").toString());

        //Add the announcement to the list
        announcementArrayList.add(announcement);

        //Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}