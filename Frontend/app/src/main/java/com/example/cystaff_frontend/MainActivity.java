package com.example.cystaff_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class defining the behaviour of the login page
 */
public class MainActivity extends AppCompatActivity {
    // Create variables for all buttons
    private Button loginBtn;

    // Create variables for all text entries
    private TextInputLayout emailInput, passwordInput;

    // Create variables for the error text view box
    private TextView errText;

    // Create variables for string inputs
    private String email, password;

    // Set up the progress bar
    private ProgressBar mProgressBar;

    // Create the response JSON object
    private JSONObject JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign screen elements
        loginBtn = findViewById(R.id.loginButton);
        emailInput = findViewById(R.id.emailTextInput);
        passwordInput = findViewById(R.id.passwordTextInput);
        errText = findViewById(R.id.errTextBox);
        mProgressBar = findViewById(R.id.progressBar);

        // Add a listener for the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize boolean values to false
                boolean validEmail = false;
                boolean validPassword = false;

                // Get all input field values
                email = emailInput.getEditText().getText().toString();
                password = passwordInput.getEditText().getText().toString();

                // If inputs are empty, provide an error, otherwise, clear errors
                if (email == null || email.equals("")) {
                    emailInput.setError("You need to enter an email address.");
                } else {
                    emailInput.setError(null);
                    validEmail = true;
                }

                if (password == null || password.equals("")) {
                    passwordInput.setError("You need to enter a password.");
                } else {
                    passwordInput.setError(null);
                    validPassword = true;
                }

                // If a valid email and password were entered (not blank),
                // then create a JSON object request to the server
                if (validEmail && validPassword) {
                    // Start the progress bar
                    mProgressBar.setVisibility(View.VISIBLE);
                    // Check if the username/password is valid
                    makeJsonObjReq();
                }
            }
        });
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        // Create a JSONObject
        JSONObject postBody = null;

        // Try to generate the JSONObject
        try {
            // Provide both an email and a password field
            postBody = new JSONObject();
            postBody.put("email", email);
            postBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update the URL
        String URL = Const.serverURL + "login";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        JSONResponse = response;
                        try {
                            handleResponse();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                JSONError = error;
                try {
                    handleResponse();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ) {
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


        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    private void handleResponse() throws JSONException {
        // End the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Check for an error, if no error, sign the user in
        if (JSONError instanceof TimeoutError || JSONError instanceof NoConnectionError || JSONError instanceof ServerError || JSONError instanceof NetworkError) {
            // Provide an error message given that the server is not working
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
            JSONError = null;
        } else if (JSONResponse != null) {
            // Disable the error message
            errText.setVisibility(View.INVISIBLE);

            // If the settings JSON object is null, create default settings
            if (JSONResponse.isNull("settings")) {
                // Default settings
                JSONObject postBody = new JSONObject();
                JSONObject userObj = new JSONObject();
                userObj.put("userid", JSONResponse.getInt("userid"));
                postBody.put("user", userObj);
                postBody.put("calendarFormat", "Month");
                postBody.put("preferredName", JSONResponse.getString("firstName"));
                postBody.put("weekDisplay", "true");
                postBody.put("eventType", "Meeting");
                postBody.put("locationType", "Online");
                postBody.put("numberOfAnnouncements", 10);
                postBody.put("sortDirectoryBy", "Alphabetical");
                HashMap<String, String> settings = new HashMap<String, String>();
                settings.put("calendarFormat", "Month");
                settings.put("preferredName", JSONResponse.getString("firstName"));
                settings.put("weekDisplay", "true");
                settings.put("eventType", "Meeting");
                settings.put("locationType", "Online");
                settings.put("numberOfAnnouncements", 10 + "");
                settings.put("sortDirectoryBy", "Alphabetical");

                // Request the creation of the settings
                // Generate the path string
                String URL = Const.serverURL + "settings";

                // Make the request
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, postBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Volley Response", response.toString());
                            JSONObject statusObj = response;

                            // Check the status
                            try {
                                if (statusObj.getString("status").equalsIgnoreCase("success")) {
                                    // Succeeded - Create the user object and go to the next page
                                    // Get the nested JSON objects
                                    JSONObject privilegeJSON = JSONResponse.getJSONObject("privilege");

                                    // Get the settings id
                                    settings.put("settingsID", response.getInt("settingsid") + "");

                                    // Create a user object
                                    User currUser = new User(JSONResponse.getInt("userid"), JSONResponse.getString("password"), JSONResponse.getString("firstName"), JSONResponse.getString("lastName"), JSONResponse.getString("birthday"), JSONResponse.getString("phone"), JSONResponse.getString("email"), JSONResponse.getString("building"), JSONResponse.getString("active"), JSONResponse.getString("lastLogin"),
                                            privilegeJSON.getInt("privilegeid"), privilegeJSON.getString("type"), settings);

                                    // Go to the home/main page and include sign in information
                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    i.putExtra("userInfo", currUser);
                                    String serverURL = Const.serverURL + "notifications/" + currUser.getFirstName() + currUser.getLastName();
                                    NotificationSocketManager.getInstance().connectWebSocket(serverURL);
                                    NotificationSocketManager.getInstance().setWebSocketListener(new NotificationHandler());
                                    startActivity(i);
                                } else {
                                    // Failed - Inform the user
                                    Toast.makeText(getApplicationContext(), "There was an error generating default settings. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error", "Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(), "There was an error generating default settings. Please try again later.", Toast.LENGTH_LONG).show();
                        }
                });

                // Adding request to request queue
                AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
            } else {
                // Get the nested JSON objects
                JSONObject privilegeJSON = JSONResponse.getJSONObject("privilege");
                JSONObject settingsJSON = JSONResponse.getJSONObject("settings");

                // Convert settingsJSON to HashMap
                HashMap<String, String> settingsHash = new HashMap<>();
                settingsHash.put("calendarFormat", settingsJSON.getString("calendarFormat"));
                settingsHash.put("preferredName", settingsJSON.getString("preferredName"));
                settingsHash.put("weekDisplay", settingsJSON.getString("weekDisplay"));
                settingsHash.put("eventType", settingsJSON.getString("eventType"));
                settingsHash.put("locationType", settingsJSON.getString("locationType"));
                settingsHash.put("numberOfAnnouncements", settingsJSON.getInt("numberOfAnnouncements") + "");
                settingsHash.put("sortDirectoryBy", settingsJSON.getString("sortDirectoryBy"));
                settingsHash.put("settingsID", settingsJSON.getInt("settingsid") + "");

                // Create a user object
                User currUser = new User( JSONResponse.getInt("userid"), JSONResponse.getString("password"), JSONResponse.getString("firstName"), JSONResponse.getString("lastName"), JSONResponse.getString("birthday"), JSONResponse.getString("phone"), JSONResponse.getString("email"), JSONResponse.getString("building"), JSONResponse.getString("active"), JSONResponse.getString("lastLogin"),
                        privilegeJSON.getInt("privilegeid"), privilegeJSON.getString("type"), settingsHash);

                // Go to the home/main page and include sign in information
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.putExtra("userInfo", currUser);
                String serverURL = Const.serverURL + "notifications/" + currUser.getFirstName() + currUser.getLastName();
                NotificationSocketManager.getInstance().connectWebSocket(serverURL);
                NotificationSocketManager.getInstance().setWebSocketListener(new NotificationHandler());
                startActivity(i);
            }
        } else {
            // Provide an error message saying that the information entered was wrong
            errText.setText("Invalid email/password. Please try again.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}