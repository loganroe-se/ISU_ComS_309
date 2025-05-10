package com.example.cystaff_frontend.announcements;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.AnnouncementsActivity;
import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Public Class defining the behaviour of creating an announcement in the Create Announcement Page.
 */
public class CreateAnnouncementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Holds the TextViews used on the page
    private TextView headerTxt, errText;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar;

    // Holds the reference to the header's profile button
    private Spinner profile;

    // Hold the user object for the current user
    private User currUser, sentUser;

    // Holds the screen's button instances
    private Button cancelBtn, submitBtn;

    // Holds the message input text box
    private TextInputLayout messageInput;

    // Stores the notification checkbox instance
    private CheckBox notificationCheck;

    // Stores the information about the announcement being sent
    private String message, date, time;

    // Create the response JSON object
    private JSONObject JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);

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
        cancelBtn = findViewById(R.id.cancel_button);
        submitBtn = findViewById(R.id.submitButton);
        messageInput = findViewById(R.id.messageInput);
        notificationCheck = findViewById(R.id.notificationCheckbox);
        errText = findViewById(R.id.errorText);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Set click listener on cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Restart the normal announcement activity if selected
                Intent i = new Intent(CreateAnnouncementActivity.this, AnnouncementsActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            }
        });

        // Set click listener on submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the components needed for the announcement
                message = messageInput.getEditText().getText().toString();
                date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
                time = (String) DateFormat.format(new String("hh:mm aaa"), Calendar.getInstance().getTime());
                sentUser = new User(currUser.getUserID(), null, currUser.getFirstName(),
                        currUser.getLastName(), null, currUser.getNumber(), currUser.getEmail(),
                        currUser.getBuilding(), currUser.getActive(), null, currUser.getPrivilegeID(),
                        currUser.getPrivilegeType(), currUser.getSettings());

                //Testing if the message was blank
                if (message == null || message.trim().isEmpty()) {
                    //Message was blank, send error
                    messageInput.setError("Please enter a message.");
                    // Reset the server error text
                    errText.setVisibility(View.GONE);
                } else {
                    //Message was not blank
                    messageInput.setError(null);

                    //Check if the notification checkbox is checked
                    if (notificationCheck.isChecked()) {
                        //If not, send over websocket as notification
                        NotificationSocketManager.getInstance().sendMessage(message);
                        //Create a success Toast
                        Toast.makeText(CreateAnnouncementActivity.this, "Notification Sent Successfully!", Toast.LENGTH_LONG).show();
                        //Go back to the previous page
                        Intent i = new Intent(CreateAnnouncementActivity.this, AnnouncementsActivity.class);
                        i.putExtra("userInfo", currUser);
                        startActivity(i);
                    } else {
                        //If so, send to the server as an announcement
                        makeJsonObjReq();
                    }
                }
            }
        });
    }

    /**
     * Used for setting the buttons in the navigation menu
     *
     * @param item The selected item
     * @return true when an item is selected successfully
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Header.selectHelper(item, drwLayout, this, currUser);
        return true;
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        // Create a JSONObject
        JSONObject postBody = null;
        JSONObject userBody;

        // Try to generate the JSONObject
        try {
            // Provide all of the inputted values & other required values
            postBody = new JSONObject();
            postBody.put("message", message);
            postBody.put("date", date);
            postBody.put("time", time);
            userBody = new JSONObject();
            userBody.put("userid", sentUser.getUserID());
            postBody.put("user", userBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate the path string
        String URL = Const.serverURL + "announcement";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, postBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        JSONResponse = response;
                        handleResponse();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error Stuff", "Error: " + error.getMessage());
                JSONError = error;
                handleResponse();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void handleResponse() {
        // Reset the error text
        errText.setVisibility(View.GONE);

        //Testing if there is a JSONResponse from the server
        if (JSONResponse != null) {

            // Get the response in string format
            String responseVal = null;

            try {
                responseVal = JSONResponse.getString("status");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //List of possible responses:
            // success - Announcement was successfully added
            // failure - The sent announcement was a null pointer
            if (responseVal.equals("success")) {
                //Create a success Toast
                Toast.makeText(CreateAnnouncementActivity.this, "Announcement Created Successfully!", Toast.LENGTH_LONG).show();
                //Go back to the previous page
                Intent i = new Intent(CreateAnnouncementActivity.this, AnnouncementsActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            } else if (responseVal.equals("failure")) {
                errText.setText("Invalid announcement. Please try again.");
                errText.setVisibility(View.VISIBLE);
            } else {
                // Assume failure since it was not a success or known error
                errText.setText("Unable to store the announcement. Please try again later.");
                errText.setVisibility(View.VISIBLE);
            }
        } else {
            // Update the error message
            errText.setText("There were problems with communicating with the server. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}