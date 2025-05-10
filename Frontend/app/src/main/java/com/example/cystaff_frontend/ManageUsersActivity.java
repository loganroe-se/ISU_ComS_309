package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.directory.DirectoryItem;
import com.example.cystaff_frontend.directory.DirectoryViewAdapter;
import com.example.cystaff_frontend.directory.DirectoryViewInterface;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.manage.AddUserActivity;
import com.example.cystaff_frontend.manage.EditUserActivity;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class defining the behaviour of the manage users page
 */
public class ManageUsersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DirectoryViewInterface {

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

    // Create variable for search bar text layout
    private TextInputLayout searchInput;

    // Create variables for search bar text input and results text
    private TextView searchText, resultsText;

    // Create a variable for the search bar input string
    private String searchValue;

    // Create variables for the recycler view
    private RecyclerView employeeListView;
    private ArrayList<DirectoryItem> employeeItems;
    private DirectoryViewAdapter employeeListAdapter;

    // Set up the progress bar
    private ProgressBar mProgressBar;

    // Create error text textview
    private TextView errText;

    // Floating action button
    private FloatingActionButton addUserFAB;

    // Create variables for the user pop up
    private Dialog userPopup;
    private Button backBtn, deleteBtn, editBtn;
    private TextView nameView, emailView, numberView, buildingView, jobLevelView, birthdayView;
    private ImageView profilePicture;

    // Variables for confirmation pop up
    private Dialog confirmationPopup;
    private Button noBtn, yesBtn;
    private TextView confirmationText;

    // Create the response JSON object
    private JSONArray JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        //Set the notification handler's root View
        NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
        temp.setActivity(this);

        // Get the user
        currUser = getIntent().getParcelableExtra("userInfo", User.class);

        // Find screen element(s)
        headerTxt = findViewById(R.id.headerText);
        drwLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.header);
        profile = findViewById(R.id.profile);
        searchInput = findViewById(R.id.searchTextInputLayout);
        searchText = findViewById(R.id.searchTextInput);
        mProgressBar = findViewById(R.id.progressBar);
        resultsText = findViewById(R.id.results);
        errText = findViewById(R.id.manErrText);
        addUserFAB = findViewById(R.id.addUserFAB);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Set up the recycler view
        employeeListView = findViewById(R.id.recyclerView);
        employeeItems = new ArrayList<DirectoryItem>();
        employeeListView.setLayoutManager(new LinearLayoutManager(this));
        employeeListAdapter = new DirectoryViewAdapter(getApplicationContext(), employeeItems, this);
        employeeListView.setAdapter(employeeListAdapter);

        // Set up the pop up activity
        userPopup = new Dialog(this);
        userPopup.setContentView(R.layout.manage_employee_popup);
        Objects.requireNonNull(userPopup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Define pop up buttons, text views, and images
        backBtn = userPopup.findViewById(R.id.backBtn);
        deleteBtn = userPopup.findViewById(R.id.deleteBtn);
        editBtn = userPopup.findViewById(R.id.editBtn);
        nameView = userPopup.findViewById(R.id.fullNameView);
        emailView = userPopup.findViewById(R.id.emailView);
        numberView = userPopup.findViewById(R.id.numberView);
        buildingView = userPopup.findViewById(R.id.buildingView);
        jobLevelView = userPopup.findViewById(R.id.jobLevelView);
        birthdayView = userPopup.findViewById(R.id.birthdayView);
        profilePicture = userPopup.findViewById(R.id.profilePic);

        // Set up the are you sure pop up
        confirmationPopup = new Dialog(this);
        confirmationPopup.setContentView(R.layout.confirmation_popup);
        Objects.requireNonNull(confirmationPopup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Define pop up buttons and text views
        noBtn = confirmationPopup.findViewById(R.id.noBtn);
        yesBtn = confirmationPopup.findViewById(R.id.yesBtn);
        confirmationText = confirmationPopup.findViewById(R.id.confirmationText);

        // Perform actions when the enter key is pressed
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                // Check if the event is an enter key
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Read text input
                    searchValue = Objects.requireNonNull(searchInput.getEditText()).getText().toString();

                    // Clear all previous results
                    employeeItems = new ArrayList<DirectoryItem>();

                    // If inputs are empty, provide an error, otherwise, clear errors and call the server
                    if (searchValue.equals("")) {
                        searchInput.setError("You need to enter a search query.");
                    } else {
                        searchInput.setError(null);
                        // Start the progress bar
                        mProgressBar.setVisibility(View.VISIBLE);
                        // Make the JSON object request
                        makeJsonObjReq();
                    }

                    return true;
                }
                return false;
            }
        });

        // When the X button is clicked, clear text in the search bar
        searchInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });

        // When the floating action button is clicked, open a new activity to create a new user
        addUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the intent and start the new activity
                Intent i = new Intent(ManageUsersActivity.this, AddUserActivity.class);
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

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        // Generate the path string
        String URL = Const.serverURL + "searchUser?name=" + searchValue;

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL, null,
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


        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    @SuppressLint("SetTextI18n")
    private void handleResponse() {
        // Disable the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Reset the error text
        errText.setVisibility(View.GONE);

        // If there is a response, read it and output employee information
        // Otherwise, if there is no error, report that there were no results to the search
        // Otherwise, provide an error that the server is currently unreachable
        if (JSONResponse != null) {
            // Read the employee information
            try {
                // Iterate through the employee directory information in the JSON Array
                for (int i = 0; i < JSONResponse.length(); i++) {
                    createEmployeeList(JSONResponse.getJSONObject(i));
                }
            } catch (Exception e) {
                // If an error occurs, inform the user that there was a problem in loading the data
                errText.setText("Error: There was an issue when reading the employee directory information.");
                errText.setVisibility(View.VISIBLE);
            }
        } else if (JSONError == null) {
            // There were no results found given the search
            errText.setText("No results were found for the given search. Please try again.");
            errText.setVisibility(View.VISIBLE);
        } else {
            // Update the error message
            errText.setText("The server is currently down. Please try again later.");
            errText.setVisibility(View.VISIBLE);
        }

        // Make the Results text show up and update to include number of results
        resultsText.setVisibility(View.VISIBLE);
        String newText = "Results (" + employeeItems.size() + ")";
        resultsText.setText(newText);

        // Notify the adapter to update the listing
        employeeListAdapter.updateDirectoryList(employeeItems);

        // Make the directories visible
        employeeListView.setVisibility(View.VISIBLE);
    }

    private void createEmployeeList(JSONObject employeeItem) throws JSONException {
        // Get the privilege object
        JSONObject privilege = employeeItem.getJSONObject("privilege");

        // Create a new DirectoryItem
        //DirectoryItem item = new DirectoryItem(employeeItem.getInt("userid"), employeeItem.getString("firstName"), employeeItem.getString("lastName"), employeeItem.getString("email"), employeeItem.getString("phone"), employeeItem.getString("building"), privilege.getString("type"), employeeItem.getString("birthday"), (int) employeeItem.get("image"));
        DirectoryItem item = new DirectoryItem(employeeItem.getInt("userid"), employeeItem.getString("firstName"), employeeItem.getString("lastName"), employeeItem.getString("email"), employeeItem.getString("phone"), employeeItem.getString("building"), privilege.getString("type"), employeeItem.getString("birthday"), R.drawable.profile);

        // Add the item to the list of items
        employeeItems.add(item);

        // Notify the adapter of the new addition
        employeeListAdapter.notifyItemInserted(employeeItems.size());
    }

    /**
     * Used to perform various actions when one of the results is clicked
     *
     * @param position - The position of the item clicked in the recycler view
     */
    @Override
    public void onItemClick(int position) {
        // Get the employee item clicked
        DirectoryItem item = employeeItems.get(position);

        // Set up the pop up
        updatePopup(item);

        // Open the pop up menu for the employee item that was clicked
        userPopup.show();

        // Listen for the back button being pressed
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the pop up
                userPopup.hide();
            }
        });

        // Listen for the edit button being pressed
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to the edit user page
                Intent i = new Intent(ManageUsersActivity.this, EditUserActivity.class);
                i.putExtra("userInfo", currUser);
                i.putExtra("userToEdit", new User(item.getUserID(), "", item.getFirstName(), item.getLastName(), item.getBirthday(), item.getNumber(), item.getEmail(), item.getBuilding(), "", "", 0, item.getJobLevel(), new HashMap<>()));
                startActivity(i);
            }
        });

        // Listen for the delete button being pressed
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // Set up a string with intermittent bolded values
                String nonBold1 = "Are you sure you want to delete ", nonBold2 = "? This action is ", nonBold3 = ".";
                String bold1 = item.getFullName(), bold2 = "irreversible";
                String finalString = nonBold1 + bold1 + nonBold2 + bold2 + nonBold3;
                Spannable text = new SpannableString(finalString);
                text.setSpan(new StyleSpan(Typeface.BOLD), finalString.indexOf(bold1), finalString.indexOf(bold1) + bold1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new StyleSpan(Typeface.BOLD), finalString.indexOf(bold2), finalString.indexOf(bold2) + bold2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the text of the confirmation pop up
                confirmationText.setText(text, TextView.BufferType.SPANNABLE);

                // Open a new pop up to confirm the user's action
                confirmationPopup.show();

                // Listen for the no button being pressed
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Just close the dialog and do nothing else
                        confirmationPopup.hide();
                    }
                });

                // Listen for the yes button being pressed
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Close the popup
                        confirmationPopup.hide();

                        // Tell the server to delete the user
                        // Generate the path string
                        String URL = Const.serverURL + "users?userid=" + item.getUserID();

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
                                        // Success - User was added
                                        if (responseVal.equalsIgnoreCase("Success")) {
                                            // Inform the user that the user was successfully deleted, close the pop ups, and remove the user from the list of results
                                            Toast.makeText(getApplicationContext(), "Successfully deleted " + item.getFullName() + "!", Toast.LENGTH_LONG).show();
                                            userPopup.hide();

                                            // Remove the user from results
                                            employeeItems.remove(item);
                                            employeeListAdapter.updateDirectoryList(employeeItems);

                                            // Update results text
                                            String newText = "Results (" + employeeItems.size() + ")";
                                            resultsText.setText(newText);
                                        } else {
                                            // Inform the user that the user unable to be deleted and close the pop ups
                                            Toast.makeText(getApplicationContext(), "Failed to delete " + item.getFullName() + ". If the problem persists, please contact IT.", Toast.LENGTH_SHORT).show();
                                            userPopup.hide();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", "Error: " + error.getMessage());

                                // Inform the user that the user unable to be deleted and close the pop ups
                                Toast.makeText(getApplicationContext(), "Failed to delete " + item.getFullName() + ". If the problem persists, please contact IT.", Toast.LENGTH_SHORT).show();
                                userPopup.hide();
                            }
                        });

                        // Adding request to request queue
                        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
                    }
                });
            }
        });
    }

    /**
     * Used to update the pop ups information
     *
     * @param item - The directory item holding all of the information to update to
     */
    private void updatePopup(DirectoryItem item) {
        nameView.setText(item.getFullName());
        emailView.setText(String.format("Email: %s", item.getEmail()));
        numberView.setText(String.format("Phone Number: %s", item.getNumber()));
        buildingView.setText(String.format("Building: %s", item.getBuilding()));
        jobLevelView.setText(String.format("Job Level: %s", item.getJobLevel()));
        birthdayView.setText(String.format("Birthday: %s", item.getBirthday()));
        profilePicture.setImageResource(item.getProfilePic());
    }
}