package com.example.cystaff_frontend.time;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.TimeTrackingActivity;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private TextView timeTxt;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        public void setTxt(TextView view) {
            timeTxt = view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Create a string for the minutes
            String minStr;

            //Test if the minutes are less than 10
            if (minute < 10) {
                //If so, prepend a zero (Otherwise you get stuff like 8:4pm)
                minStr = "0" + minute;
            } else {
                //If not, the minute is already two digits
                minStr = minute + "";
            }

            //Convert from 24 hour to twelve hour time
            if (hourOfDay >= 12) {
                //Over 12 noon, need to subtract by 12, pm
                //Testing if it is around noon, which could cause problems
                if(hourOfDay != 12){
                    hourOfDay -= 12;
                }
                timeTxt.setText(hourOfDay + ":" + minStr + "pm");
            } else {
                //Already 12 or below, am
                //Checking if it is part of midnight
                if(hourOfDay == 0){
                    hourOfDay = 12;
                }
                timeTxt.setText(hourOfDay + ":" + minStr + "am");
            }
        }
    }

    // Holds the TextViews used on the page
    private TextView headerTxt, errText, time1Txt, time2Txt, time3Txt, time4Txt;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView;

    // Create the response JSON array
    private JSONObject JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar;

    // Hold the user object for the current user
    private User currUser;

    // Holds the reference to the header's profile button
    private Spinner profile;

    // Holds the date input text field
    private EditText date;

    // Holds the references for the page's buttons
    private Button submitBtn, cancelBtn, time1Btn, time2Btn, time3Btn, time4Btn;

    // Strings that hold the value of the inputted text
    private String dateStr, start1Str, end1Str, start2Str, end2Str;

    private boolean validDate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);

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
        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        date = findViewById(R.id.dateInput);
        time1Btn = findViewById(R.id.start1Btn);
        time2Btn = findViewById(R.id.start2Btn);
        time3Btn = findViewById(R.id.start3Btn);
        time4Btn = findViewById(R.id.start4Btn);
        errText = findViewById(R.id.errorText);
        time1Txt = findViewById(R.id.time1);
        time2Txt = findViewById(R.id.time2);
        time3Txt = findViewById(R.id.time3);
        time4Txt = findViewById(R.id.time4);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // If values are entered in the date field, add backslashes accordingly
        date.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the length of the text value
                int textLength = date.getText().length();
                String currText = date.getText().toString();

                // Depending on the length of the text and whether it was adding new or deleting old characters, add hyphens
                // Else ifs ensure hyphens exist on edge cases as well
                if ((textLength + 1 == 3 || textLength + 1 == 6) && (before - count < 0)) {
                    date.setText(String.format("%s/", date.getText()));
                } else if (textLength == 3 && !currText.matches("\\d{2}/") && (before - count < 0)) {
                    date.setText(String.format("%s/%s", currText.substring(0, 2), currText.substring(2)));
                } else if (textLength == 6 && !currText.matches("\\d{2}/\\d{2}/") && (before - count < 0)) {
                    date.setText(String.format("%s/%s", currText.substring(0, 5), currText.substring(5)));
                }

                // Ensure the cursor is at the end
                date.setSelection(date.getText().length());

                // If the full date is there, check for invalid values
                if (textLength == 10) {
                    // Initialize local booleans
                    boolean validMonth = true, validDay = true, validYear = true;

                    // Check if month is invalid
                    if (Integer.parseInt(currText.substring(0, 2)) > 12) {
                        Toast.makeText(getApplicationContext(), "Invalid month for date.", Toast.LENGTH_SHORT).show();
                        validMonth = false;
                    }

                    // Check if day is invalid
                    if (Integer.parseInt(currText.substring(3, 5)) > 31) {
                        Toast.makeText(getApplicationContext(), "Invalid day for date.", Toast.LENGTH_SHORT).show();
                        validDay = false;
                    }

                    // Check if year is invalid
                    if (Integer.parseInt(currText.substring(6)) > Calendar.getInstance().get(Calendar.YEAR)) {
                        Toast.makeText(getApplicationContext(), "Invalid year for date.", Toast.LENGTH_SHORT).show();
                        validYear = false;
                    }

                    // If all portions are valid, make validBirthday true, otherwise, false
                    validDate = validMonth && validDay && validYear;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set click listener on cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Restart the normal time tracking activity if selected
                Intent i = new Intent(CheckingActivity.this, TimeTrackingActivity.class);
                i.putExtra("userInfo", currUser);
                startActivity(i);
            }
        });

        // Set click listener on time button
        time1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment picker = new TimePickerFragment();
                picker.show(getSupportFragmentManager(), "timePicker");
                picker.setTxt(time1Txt);
            }
        });

        // Set click listener on time button
        time2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment picker = new TimePickerFragment();
                picker.show(getSupportFragmentManager(), "timePicker");
                picker.setTxt(time2Txt);
            }
        });

        // Set click listener on time button
        time3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment picker = new TimePickerFragment();
                picker.show(getSupportFragmentManager(), "timePicker");
                picker.setTxt(time3Txt);
            }
        });

        // Set click listener on time button
        time4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment picker = new TimePickerFragment();
                picker.show(getSupportFragmentManager(), "timePicker");
                picker.setTxt(time4Txt);
            }
        });

        // Set click listener on submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide error text
                errText.setVisibility(View.INVISIBLE);

                //Get all of the time texts
                String txt1 = time1Txt.getText().toString();
                String txt2 = time2Txt.getText().toString();
                String txt3 = time3Txt.getText().toString();
                String txt4 = time4Txt.getText().toString();

                int[] times = new int[8];

                //Testing if any of the time texts are empty
                if (txt1.length() == 0) {
                    errText.setText("Please input a start time before the lunch break");
                    errText.setVisibility(View.VISIBLE);
                    return;
                } else if (txt2.length() == 0) {
                    errText.setText("Please input an end time before the lunch break");
                    errText.setVisibility(View.VISIBLE);
                    return;
                } else if (txt3.length() == 0) {
                    errText.setText("Please input a start time after the lunch break");
                    errText.setVisibility(View.VISIBLE);
                    return;
                } else if (txt4.length() == 0) {
                    errText.setText("Please input an end time after the lunch break");
                    errText.setVisibility(View.VISIBLE);
                    return;
                }

                //Getting the date String
                dateStr = Objects.requireNonNull(date.getText()).toString();

                //Test if the date is valid
                if(dateStr == null || dateStr.length() == 0 || !validDate){
                    errText.setText("Please input a valid date");
                    errText.setVisibility(View.VISIBLE);
                    return;
                }

                String[] values;

                //Get the first time numbers
                values = txt1.split(":");
                times[0] = Integer.parseInt(values[0]);
                if(values[1].charAt(2) == 'p') {
                    times[0] += 12;
                }
                times[1] = Integer.parseInt(values[1].replaceAll("[\\D]", ""));

                //Get the second time numbers
                values = txt2.split(":");
                times[2] = Integer.parseInt(values[0]);
                if(values[1].charAt(2) == 'p') {
                    times[2] += 12;
                }
                times[3] = Integer.parseInt(values[1].replaceAll("[\\D]", ""));

                //Get the third time numbers
                values = txt3.split(":");
                times[4] = Integer.parseInt(values[0]);
                if(values[1].charAt(2) == 'p') {
                    times[4] += 12;
                }
                times[5] = Integer.parseInt(values[1].replaceAll("[\\D]", ""));

                //Get the fourth time numbers
                values = txt4.split(":");
                times[6] = Integer.parseInt(values[0]);
                if(values[1].charAt(2) == 'p') {
                    times[6] += 12;
                }
                times[7] = Integer.parseInt(values[1].replaceAll("[\\D]", ""));

                double time1 = times[0] + (times[1] / 60.0);
                double time2 = times[2] + (times[3] / 60.0);
                double time3 = times[4] + (times[5] / 60.0);
                double time4 = times[6] + (times[7] / 60.0);

                //Checking if the times are plausible
                if(time2 - time1 < 0){
                    errText.setText("The time before the lunch break must be in sequential order");
                    errText.setVisibility(View.VISIBLE);
                    return;
                }
                if(time4 - time3 < 0){
                    errText.setText("The time after the lunch break must be in sequential order");
                    errText.setVisibility(View.VISIBLE);
                    return;
                }
                if(time3 - time2 < 0){
                    errText.setText("The time after the lunch break must be after the time before the lunch break");
                    errText.setVisibility(View.VISIBLE);
                    return;
                }

                //Format the date correctly
                String formattedStr = "" + dateStr.charAt(6);
                formattedStr = formattedStr + dateStr.charAt(7);
                formattedStr = formattedStr + dateStr.charAt(8);
                formattedStr = formattedStr + dateStr.charAt(9);
                formattedStr = formattedStr + "/";
                formattedStr = formattedStr + dateStr.charAt(0);
                formattedStr = formattedStr + dateStr.charAt(1);
                formattedStr = formattedStr + "/";
                formattedStr = formattedStr + dateStr.charAt(3);
                formattedStr = formattedStr + dateStr.charAt(4);

                //Calculate the total time for the date
                int tTime = (int) Math.round((time4 - time3) + (time2 - time1));

                //Post request to send the time
                makeJsonObjReq(tTime, formattedStr);
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

    /**
     * Making json object request
     */
    private void makeJsonObjReq(int hours, String addDate) {
        // Create a JSONObject
        JSONObject postBody = null;
        JSONObject userBody;
        JSONObject typeBody;
        JSONObject statusBody;

        // Try to generate the JSONObject
        try {
            // Provide all of the inputted values & other required values
            postBody = new JSONObject();
            postBody.put("hoursNum", hours);

            postBody.put("date", addDate);

            typeBody = new JSONObject();
            typeBody.put("hourtypeid", 1);
            postBody.put("hourType", typeBody);

            userBody = new JSONObject();
            userBody.put("userid", currUser.getUserID());
            postBody.put("user", userBody);

            statusBody = new JSONObject();
            statusBody.put("statusid", 3);
            postBody.put("status", statusBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate the path string
        String URL = Const.serverURL + "hour";

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
        if(JSONResponse != null){
            //Restart the normal time tracking activity
            Intent i = new Intent(CheckingActivity.this, TimeTrackingActivity.class);
            i.putExtra("userInfo", currUser);
            startActivity(i);
        }
        else{
            errText.setText("Unable to reach the server at this time. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }
    }
}


