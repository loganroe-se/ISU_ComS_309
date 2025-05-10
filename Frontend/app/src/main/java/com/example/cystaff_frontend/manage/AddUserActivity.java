package com.example.cystaff_frontend.manage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.ManageUsersActivity;
import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Public Class for defining the behaviour of adding a user on the Add User page.
 */
public class AddUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    // Variables for inputs
    private TextInputLayout firstNameInput, lastNameInput, passwordInput, emailInput, numberInput, privilegeInput, buildingInput, birthdayInput;
    private EditText numberText, birthdayText;
    private String firstNameValue, lastNameValue, passwordValue, emailValue, numberValue, privilegeValue, buildingValue, birthdayValue;
    private ArrayAdapter<String> privilegeAdapter, buildingAdapter;
    private AutoCompleteTextView privilegeAutoComplete, buildingAutoComplete;
    private boolean validBirthday = true;

    // Text view for the error text
    private TextView errText;

    // Variables for save/cancel buttons
    private Button cancelBtn, saveBtn;

    // Progress bar
    private ProgressBar mProgressBar;

    // Create the response JSON object
    private JSONObject JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

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
        firstNameInput = findViewById(R.id.firstNameTextLayout);
        lastNameInput = findViewById(R.id.lastNameTextLayout);
        passwordInput = findViewById(R.id.passwordTextLayout);
        emailInput = findViewById(R.id.emailTextLayout);
        numberInput = findViewById(R.id.numberTextLayout);
        privilegeInput = findViewById(R.id.privilegeTypeLayout);
        privilegeAutoComplete = findViewById(R.id.privilegeTypeInput);
        buildingInput = findViewById(R.id.buildingTextLayout);
        buildingAutoComplete = findViewById(R.id.buildingInput);
        birthdayInput = findViewById(R.id.birthdayTextLayout);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        errText = findViewById(R.id.errText);
        mProgressBar = findViewById(R.id.progressBar);

        // Set up any drop down menus
        privilegeAdapter = new ArrayAdapter<String>(this, R.layout.list_dir_item, Const.privilegeTypes.keySet().toArray(new String[0]));
        privilegeAutoComplete.setAdapter(privilegeAdapter);
        buildingAdapter = new ArrayAdapter<String>(this, R.layout.list_dir_item, Const.buildings);
        buildingAutoComplete.setAdapter(buildingAdapter);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Get the number input edit text value
        numberText = numberInput.getEditText();

        // If values in the phone number field are entered, add hyphens accordingly
        numberText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the length of the text value
                int textLength = numberText.getText().length();
                String currText = numberText.getText().toString();

                // Depending on the length of the text and whether it was adding new or deleting old characters, add hyphens
                // Else ifs ensure hyphens exist on edge cases as well
                if ((textLength + 1 == 4 || textLength + 1 == 8) && (before - count < 0)) {
                    numberText.setText(String.format("%s-", numberText.getText()));
                } else if (textLength == 4 && !currText.matches("\\d{3}-") && (before - count < 0)) {
                    numberText.setText(String.format("%s-%s", currText.substring(0, 3), currText.substring(3)));
                } else if (textLength == 8 && !currText.matches("\\d{3}-\\d{3}-") && (before - count < 0)) {
                    numberText.setText(String.format("%s-%s", currText.substring(0, 7), currText.substring(7)));
                }

                // Ensure the cursor is at the end
                numberText.setSelection(numberText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Get the birthday input edit text value
        birthdayText = birthdayInput.getEditText();

        // If values are entered in the birthday field, add hyphens accordingly
        birthdayText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the length of the text value
                int textLength = birthdayText.getText().length();
                String currText = birthdayText.getText().toString();

                // Depending on the length of the text and whether it was adding new or deleting old characters, add hyphens
                // Else ifs ensure hyphens exist on edge cases as well
                if ((textLength + 1 == 3 || textLength + 1 == 6) && (before - count < 0)) {
                    birthdayText.setText(String.format("%s-", birthdayText.getText()));
                } else if (textLength == 3 && !currText.matches("\\d{2}-") && (before - count < 0)) {
                    birthdayText.setText(String.format("%s-%s", currText.substring(0, 2), currText.substring(2)));
                } else if (textLength == 6 && !currText.matches("\\d{2}-\\d{2}-") && (before - count < 0)) {
                    birthdayText.setText(String.format("%s-%s", currText.substring(0, 5), currText.substring(5)));
                }

                // Ensure the cursor is at the end
                birthdayText.setSelection(birthdayText.getText().length());

                // If the full date is there, check for invalid values
                if (textLength == 10) {
                    // Initialize local booleans
                    boolean validMonth = true, validDay = true, validYear = true;

                    // Check if month is invalid
                    if (Integer.parseInt(currText.substring(0, 2)) > 12) {
                        Toast.makeText(getApplicationContext(), "Invalid month for birthday date.", Toast.LENGTH_SHORT).show();
                        validMonth = false;
                    }

                    // Check if day is invalid
                    if (Integer.parseInt(currText.substring(3, 5)) > 31) {
                        Toast.makeText(getApplicationContext(), "Invalid day for birthday date.", Toast.LENGTH_SHORT).show();
                        validDay = false;
                    }

                    // Check if year is invalid
                    if (Integer.parseInt(currText.substring(6)) > Calendar.getInstance().get(Calendar.YEAR)) {
                        Toast.makeText(getApplicationContext(), "Invalid year for birthday date.", Toast.LENGTH_SHORT).show();
                        validYear = false;
                    }

                    // If all portions are valid, make validBirthday true, otherwise, false
                    validBirthday = validMonth && validDay && validYear;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // If the save button is pressed, send the information to the server and, if no errors, exit activity
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read all text inputs
                firstNameValue = Objects.requireNonNull(firstNameInput.getEditText()).getText().toString();
                lastNameValue = Objects.requireNonNull(lastNameInput.getEditText()).getText().toString();
                passwordValue = Objects.requireNonNull(passwordInput.getEditText()).getText().toString();
                emailValue = Objects.requireNonNull(emailInput.getEditText()).getText().toString();
                numberValue = Objects.requireNonNull(numberInput.getEditText()).getText().toString();
                privilegeValue = Objects.requireNonNull(privilegeInput.getEditText()).getText().toString();
                buildingValue = Objects.requireNonNull(buildingInput.getEditText()).getText().toString();
                birthdayValue = Objects.requireNonNull(birthdayInput.getEditText()).getText().toString();

                // If there are any empty required inputs, tell the user to provide values for all required inputs
                if (checkInputs() && (validBirthday || birthdayValue.length() == 0) && (Arrays.asList(Const.buildings).contains(buildingValue) || buildingValue.length() == 0)) {
                    // Make the progress bar visible
                    mProgressBar.setVisibility(View.VISIBLE);

                    // All inputs were valid, so send the information to the server
                    makeJsonObjReq();
                } else if (!validBirthday && birthdayValue.length() != 0) {
                    // Invalid date input for the birthday
                    errText.setText("Please enter a valid date for the birthday. This is an optional input, so it can be left blank.");
                    errText.setVisibility(View.VISIBLE);
                } else if (!(Arrays.asList(Const.buildings).contains(buildingValue) || buildingValue.length() == 0)) {
                    // Invalid building input
                    errText.setText("Please enter a valid building. For simplicity, use the text entry to narrow search and click a building selection. This is an optional input, so it can be left blank.");
                    errText.setVisibility(View.VISIBLE);
                } else {
                    // At least one input was invalid, so inform the user and wait for another action
                    errText.setText("Please ensure that all required inputs (*) have values.");
                    errText.setVisibility(View.VISIBLE);
                }
            }
        });

        // If the cancel button is pressed, exit activity
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the intent to send it back to the manage users page
                Intent i = new Intent(AddUserActivity.this, ManageUsersActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
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
     * Used to check all required inputs and sets errors accordingly
     *
     * @return - False if there are any invalid inputs, true otherwise
     */
    private boolean checkInputs() {
        // Boolean to track if there are any errors
        boolean valid = true;

        // If any of the required inputs are empty, turn on the error and return true
        // Note: Birthday and building are optional inputs, so they are not checked here
        if (firstNameValue.equals("")) {
            firstNameInput.setError("error");
            valid = false;
        } else {
            firstNameInput.setError(null);
        }
        if (lastNameValue.equals("")) {
            lastNameInput.setError("error");
            valid = false;
        } else {
            lastNameInput.setError(null);
        }
        if (passwordValue.equals("")) {
            passwordInput.setError("error");
            valid = false;
        } else {
            passwordInput.setError(null);
        }
        if (emailValue.equals("")) {
            emailInput.setError("error");
            valid = false;
        } else {
            emailInput.setError(null);
        }
        if (numberValue.equals("")) {
            numberInput.setError("error");
            valid = false;
        } else {
            numberInput.setError(null);
        }
        if (privilegeValue.equals("")) {
            privilegeInput.setError("error");
            valid = false;
        } else {
            privilegeInput.setError(null);
        }

        return valid;
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        // Create a JSONObject
        JSONObject postBody = null;
        JSONObject privilegeBody;

        // Try to generate the JSONObject
        try {
            // Provide all of the inputted values & other required values
            postBody = new JSONObject();
            postBody.put("firstName", firstNameValue);
            postBody.put("lastName", lastNameValue);
            postBody.put("password", passwordValue);
            postBody.put("email", emailValue);
            postBody.put("phone", numberValue);
            postBody.put("building", buildingValue);
            postBody.put("birthday", birthdayValue);
            postBody.put("active", "No");
            postBody.put("lastLogin", "Today");
            privilegeBody = new JSONObject();
            privilegeBody.put("privilegeid", Const.privilegeTypes.get(privilegeValue));
            postBody.put("privilege", privilegeBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate the path string
        String URL = Const.serverURL + "users";

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
                Log.e("Volley Error", "Error: " + error.getMessage());
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


        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    @SuppressLint("SetTextI18n")
    private void handleResponse() {
        // Disable the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Reset the error text
        errText.setVisibility(View.GONE);

        // If there is a response, read it and, if valid, return to the other activity
        // Otherwise, provide an error that the server is currently unreachable
        if (JSONResponse != null) {
            // Get the response in string format
            String responseVal = "Failure";
            try {
                responseVal = JSONResponse.getString("status");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Possible Responses:
            // Failure - Failed for some reason
            // Invalid Email - Duplicate emails
            // Success - User was added
            if (responseVal.equalsIgnoreCase("Success")) {
                // Inform the user that the user was successfully added and return to the manage users activity
                Toast.makeText(getApplicationContext(), "Successfully added the user!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AddUserActivity.this, ManageUsersActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            } else if (responseVal.equalsIgnoreCase("Invalid Email")) {
                errText.setText("A duplicate email was entered. Please choose a new email and try again.");
                errText.setVisibility(View.VISIBLE);
            } else {
                // Assume failure since it was not a success or invalid email
                errText.setText("Unable to create the user right now. Please try again later.");
                errText.setVisibility(View.VISIBLE);
            }
        } else {
            // Update the error message
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}
