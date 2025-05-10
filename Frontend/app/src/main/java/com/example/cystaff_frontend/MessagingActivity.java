package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.directory.DirectoryItem;
import com.example.cystaff_frontend.directory.DirectoryViewAdapter;
import com.example.cystaff_frontend.directory.DirectoryViewInterface;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.message.Message;
import com.example.cystaff_frontend.message.MessageHandler;
import com.example.cystaff_frontend.message.MessageListAdapter;
import com.example.cystaff_frontend.message.MessageUser;
import com.example.cystaff_frontend.message.MessageWebSocketManager;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class defining the behaviour of the messaging page
 */
public class MessagingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DirectoryViewInterface {

    // Holds the TextViews used on the page
    private TextView headerTxt, messageHeaderText;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout, messageDrwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView, messageNavView;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar, messageToolbar;

    //Holds the reference to the header's profile button
    private Spinner profile;

    // Array to hold all ID values for the main header's items
    private Integer[] headerIDs;

    // Button for adding new users to message
    private Button addUser;

    // Add user variables
    private BottomSheetDialog addUserDialog;
    private View addUserView;
    private TextInputLayout addUserTIL;
    private TextView addUserTV;
    private String addUserValue;
    private ProgressBar mProgressBar;
    private RecyclerView addUserRecycler;
    private ArrayList<DirectoryItem> addUserItems;
    private DirectoryViewAdapter addUserAdapter;

    // Text view for when no user is chosen for messaging
    private TextView helpText;

    // Create error text textview
    private TextView addUserErrText;

    // Variable for the menu
    private Menu messageMenu;

    // Variables for messaging
    private TextInputLayout messageTIL;
    private TextView messageTV;
    private String messageValue;
    private RecyclerView messageRecycler;
    private MessageListAdapter messageAdapter;
    private MessageUser mainUser;
    private MessageUser otherUser;

    // Partial URL for web socket (without name/id)
    private final String partialWebSocketURL = Const.serverURL + "chat/"; // ***************** IS THIS THE RIGHT URL? ***********************

    // Variable for the web socket URL
    private String webSocketURL = null;

    // Variable to hold the response
    private JSONArray pastUsers = null;

    // Variable to hold the error
    private VolleyError pastUsersError = null;

    // Create the response JSON object
    private JSONArray JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //Set the notification handler's root View
        NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
        temp.setActivity(this);

        // Get the user
        currUser = getIntent().getParcelableExtra("userInfo", User.class);

        // Populate the mainUser value -- Profile picture is given default value since it is never used
        mainUser = new MessageUser(currUser.getUserID(), currUser.getFullName(), R.drawable.profile);

        //Find screen element(s)
        headerTxt = findViewById(R.id.headerText);
        drwLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.header);
        profile = findViewById(R.id.profile);
        messageHeaderText = findViewById(R.id.messageHeaderText);
        messageDrwLayout = findViewById(R.id.messageDrawer);
        messageNavView = findViewById(R.id.nav_messageView);
        messageToolbar = findViewById(R.id.messageHeader);
        messageMenu = messageNavView.getMenu();
        helpText = findViewById(R.id.helpText);
        messageTIL = findViewById(R.id.messageTextInputLayout);
        messageTV = findViewById(R.id.messageTextInput);
        messageRecycler = findViewById(R.id.messageRecyclerView);
        addUser = messageNavView.getHeaderView(0).findViewById(R.id.addUserToMessages);

        // Set up the message adapter
        messageAdapter = new MessageListAdapter(this, new ArrayList<Message>(), currUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(messageAdapter);

        // Create the bottom sheet dialog and get the view
        addUserDialog = new BottomSheetDialog(MessagingActivity.this, R.style.AddUserDialogTheme);
        addUserView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_user_layout, (ConstraintLayout) findViewById(R.id.addUserContainer));
        addUserDialog.setContentView(addUserView);
        addUserTIL = addUserView.findViewById(R.id.searchTextInputLayout);
        addUserTV = addUserView.findViewById(R.id.searchTextInput);
        mProgressBar = addUserView.findViewById(R.id.progressBar);
        addUserErrText = addUserView.findViewById(R.id.errText);

        // Set up the add user recycler
        addUserRecycler = addUserView.findViewById(R.id.recyclerView);
        addUserItems = new ArrayList<DirectoryItem>();
        addUserRecycler.setLayoutManager(new LinearLayoutManager(this));
        addUserAdapter = new DirectoryViewAdapter(getApplicationContext(), addUserItems, this);
        addUserRecycler.setAdapter(addUserAdapter);

        // Get the list of all item ID values in the main header
        Menu navViewMenu = navView.getMenu();
        headerIDs = new Integer[navViewMenu.size()];
        for (int i = 0; i < navViewMenu.size(); i++) {
            headerIDs[i] = navViewMenu.getItem(i).getItemId();
        }

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Get the past users and set up the header
        getPastUsersRequest();

        // If the add user button is pressed, then open a slide up menu from the bottom to search
        // for users to message
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the navigation drawer
                messageDrwLayout.closeDrawer(GravityCompat.START);

                // Open the dialog
                addUserDialog.show();
            }
        });

        // Perform actions when the enter key is pressed
        addUserTV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                // Check if the event is an enter key
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Read text input
                    addUserValue = Objects.requireNonNull(addUserTIL.getEditText()).getText().toString();

                    // Clear all previous results
                    addUserItems = new ArrayList<DirectoryItem>();

                    // If inputs are empty, provide an error, otherwise, clear errors and call the server
                    if (addUserValue.equals("")) {
                        addUserTIL.setError("You need to enter a search query.");
                    } else {
                        addUserTIL.setError(null);
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
        addUserTIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserTV.setText("");
            }
        });

        // When the enter button is pressed, call a helper to deal with the message
        messageTV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                // Check if the event is an enter key
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    handleMessageSent();
                    return true;
                }
                return false;
            }
        });

        // When the end icon is pressed, call a helper to deal with the message
        messageTIL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMessageSent();
            }
        });
    }

    /**
     * Used for setting up the buttons in the navigation menu.
     * Will either call the header's select helper or the message header's select helper, depending
     * on the type of item clicked on.
     *
     * @param item - The selected item
     * @return true upon successful execution
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // If the item id is part of the list of items in the main header, call the main header's
        // select helper, otherwise, call the message header's select helper
        if (Arrays.asList(headerIDs).contains(item.getItemId())) {
            Header.selectHelper(item, drwLayout, this, currUser);
        } else {
            selectHelper(item);
        }

        return true;
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        // Generate the path string
        String URL = Const.serverURL + "searchUser?name=" + addUserValue;

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
        addUserErrText.setVisibility(View.GONE);

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
                addUserErrText.setText("Error: There was an issue when reading the employee directory information.");
                addUserErrText.setVisibility(View.VISIBLE);
            }
        } else if (JSONError == null) {
            // There were no results found given the search
            addUserErrText.setText("No results were found for the given search. Please try again.");
            addUserErrText.setVisibility(View.VISIBLE);
        } else {
            // Update the error message
            addUserErrText.setText("The server is currently down. Please try again later.");
            addUserErrText.setVisibility(View.VISIBLE);
        }

        // Notify the adapter to update the listing
        addUserAdapter.updateDirectoryList(addUserItems);

        // Make the results visible
        addUserRecycler.setVisibility(View.VISIBLE);
    }

    private void createEmployeeList(JSONObject employeeItem) throws JSONException {
        // Get the privilege object
        JSONObject privilege = employeeItem.getJSONObject("privilege");

        // Create a new DirectoryItem
        //DirectoryItem item = new DirectoryItem(employeeItem.getInt("userid"), employeeItem.getString("firstName"), employeeItem.getString("lastName"), employeeItem.getString("email"), employeeItem.getString("phone"), employeeItem.getString("building"), privilege.getString("type"), employeeItem.getString("birthday"), (int) employeeItem.get("image"));
        DirectoryItem item = new DirectoryItem(employeeItem.getInt("userid"), employeeItem.getString("firstName"), employeeItem.getString("lastName"), employeeItem.getString("email"), employeeItem.getString("phone"), employeeItem.getString("building"), privilege.getString("type"), employeeItem.getString("birthday"), R.drawable.profile);

        // Add the item to the list of items
        addUserItems.add(item);

        // Notify the adapter of the new addition
        addUserAdapter.notifyItemInserted(addUserItems.size());
    }

    /**
     * When an item is clicked from the add user search, it will add the user to the list of users
     * to message, if it is not already there, and then will focus that user as the current messages.
     *
     * @param position - The position of the item clicked
     */
    @Override
    public void onItemClick(int position) {
        // Get the item at the position clicked
        DirectoryItem item = addUserItems.get(position);
        MenuItem mItem = null;

        // Declare a variable to determine if the item is already in the menu
        boolean itemExists = false;

        // If the user is not already in the menu for users to message, add the user
        for (int i = 0; i < messageMenu.size(); i++) {
            if (Objects.equals(messageMenu.getItem(i).getItemId(), item.getUserID())) {
                itemExists = true;
                mItem = messageMenu.getItem(i);
            }
        }
        if (!itemExists) {
            mItem = messageMenu.add(R.id.messageMenu, item.getUserID(), 0, item.getFullName()).setIcon(item.getProfilePic());
            // Update the checkable values
            messageMenu.setGroupCheckable(R.id.messageMenu, true, true);
        }

        // Close the bottom sheet dialog
        addUserDialog.hide();

        // Call the select helper from the message header to do the necessary updates
        selectHelper(mItem);
    }

    /**
     * A Method to initialize the header for the current activity.
     * It initializes the NavigationView, sets the correct header text, and
     * sets up the profile Spinner menu while overriding its DropDownView.
     */
    public void initialize() throws JSONException {
        // Activity
        Activity activity = this;

        // Declaring and initializing variables (default to top user and Name for the name)
        int checkedItemId = 0;
        String name = "Name";

        // If there was an error, inform the user and ignore generating the menu, otherwise, populate
        // the menu with past users messaged
        if (pastUsersError != null) {
            // There was an error so inform the user that the header will be generated without past users
            Toast.makeText(getApplicationContext(), "There was an error when generating list of past users messaged.\nThe header has still been generated, but without a list of past users messaged.", Toast.LENGTH_LONG).show();
        } else {
            // Loop through the JSON object and add all of the past users to the menu
            for (int i = 0; i < pastUsers.length(); i++) {
                String currString = pastUsers.getString(i);
                messageMenu.add(R.id.messageMenu, Integer.parseInt(currString.substring(currString.indexOf("*!:") + 3)), 0, currString.substring(0, currString.indexOf("*!:")).replace("!*:", " ")).setIcon(R.drawable.profile);
            }

            // Update the checkable values
            messageMenu.setGroupCheckable(R.id.messageMenu, true, true);
        }

        // Set up the navigation menu
        messageNavView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, messageDrwLayout, messageToolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        messageDrwLayout.addDrawerListener(toggle);
        toggle.syncState();
        messageNavView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) activity);

        // If the menu has values in it, call the select helper and give it the top value, else,
        // use default values
        if (messageMenu.size() != 0) {
            selectHelper(messageMenu.getItem(0));
        } else {
            messageNavView.setCheckedItem(checkedItemId);
            messageHeaderText.setText(name);
        }
    }

    /**
     * A Helper Method to assist with determining which item was selected in the navbar.
     * It finds which button was pressed and then updates the screen accordingly.
     *
     * @param item - The selected MenuItem
     */
    public void selectHelper(MenuItem item) {
        // Hide the help text
        if (helpText.getVisibility() == View.VISIBLE) {
            helpText.setVisibility(View.INVISIBLE);
        }

        // Show the message text input and the recycler view
        if (messageTIL.getVisibility() == View.INVISIBLE) {
            messageTIL.setVisibility(View.VISIBLE);
            messageRecycler.setVisibility(View.VISIBLE);
        }

        // Set the checked item on the nav view
        item.setChecked(true);

        // Getting the full name of the user pressed
        String fullName = (String) item.getTitle();

        // Set the header value
        messageHeaderText.setText(fullName);

        // Getting the id of the button pressed (also is the userID)
        int id = item.getItemId();

        // If the URL is null, no websocket has been opened yet
        // If it is not null, check the name and, if it is different, open a new websocket
        if (webSocketURL == null) {
            // Create the second user
            otherUser = new MessageUser(id, fullName, R.drawable.profile); // Use default profile for now ******************************************************

            webSocketURL = partialWebSocketURL + currUser.getFullName().replaceAll("\\s+", "!*:") + "*!:" + currUser.getUserID() + "/" + fullName.replaceAll("\\s+", "!*:") + "*!:" + id;
            MessageWebSocketManager.getInstance().connectWebSocket(this, webSocketURL);
            MessageWebSocketManager.getInstance().setWebSocketListener(new MessageHandler());
        } else if (!webSocketURL.equals(partialWebSocketURL + currUser.getFullName().replaceAll("\\s+", "!*:") + "*!:" + currUser.getUserID() + "/" + fullName.replaceAll("\\s+", "!*:") + "*!:" + id)) {
            // Create the second user
            otherUser = new MessageUser(id, fullName, R.drawable.profile); // Use default profile for now ******************************************************

            // Reset the recycler and adapter
            messageAdapter = new MessageListAdapter(this, new ArrayList<Message>(), currUser);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            messageRecycler.setLayoutManager(linearLayoutManager);
            messageRecycler.setAdapter(messageAdapter);

            MessageWebSocketManager.getInstance().disconnectWebSocket();
            webSocketURL = partialWebSocketURL + currUser.getFullName().replaceAll("\\s+", "!*:") + "*!:" + currUser.getUserID() + "/" + fullName.replaceAll("\\s+", "!*:") + "*!:" + id;
            MessageWebSocketManager.getInstance().connectWebSocket(this, webSocketURL);
            MessageWebSocketManager.getInstance().setWebSocketListener(new MessageHandler());
        }

        // Close the navigation drawer
        messageDrwLayout.closeDrawer(GravityCompat.START);
    }

    // Handles the message the user is attempting to send
    private void handleMessageSent() {
        // Hide the help text
        if (helpText.getVisibility() == View.VISIBLE) {
            helpText.setVisibility(View.INVISIBLE);
        }

        // Read text input
        messageValue = Objects.requireNonNull(messageTIL.getEditText()).getText().toString();

        // If inputs are empty, provide an error, otherwise, clear errors and call the server
        if (messageValue.equals("")) {
            messageTIL.setError("You need to enter a message value.");
        } else {
            messageTIL.setError(null);
            // Sends the message to the web socket manager
            MessageWebSocketManager.getInstance().sendMessage(messageValue);
        }
    }

    /**
     * Adds a new message to the recycler view
     *
     * @param receivedMessage - The message to add including the user that sent it
     */
    public void handleNewMessage(String receivedMessage) {
        // Parse the message if it is not empty
        if (receivedMessage.length() != 0 && (receivedMessage.length() - receivedMessage.replaceAll("!\\*:", "").length()) == 3) { // Second check is temporary (so is else if) to ensure that the first message (the chat history) is not included ************************************
            // Hide the help text and show the recycler view
            if (helpText.getVisibility() == View.VISIBLE) {
                helpText.setVisibility(View.INVISIBLE);
            }
            if (messageRecycler.getVisibility() == View.INVISIBLE || messageRecycler.getVisibility() == View.GONE) {
                messageRecycler.setVisibility(View.VISIBLE);
            }

            // Call a helper to parse the message and add it to the message adapter
            int userID = addMessage(receivedMessage);

            // Delete the text in the message bar
            if (userID == mainUser.getUserID()) {
                messageTV.setText("");
            }
        } else if (receivedMessage.length() != 0) {
            populateHistory(receivedMessage);
        }
    }

    /**
     * Populates the recycler view with the history of the chat
     *
     * @param message - The chat history
     */
    public void populateHistory(String message) {
        // Check if the message is null
        if (message.equals("null")) {
            // There is no message history so tell the user
            helpText.setText("There is currently no message history with this user. Send a message below!");
            helpText.setVisibility(View.VISIBLE);
        } else if (message.contains("%!:")) {
            // Hide the help text and show the recycler view
            if (helpText.getVisibility() == View.VISIBLE) {
                helpText.setVisibility(View.INVISIBLE);
            }
            if (messageRecycler.getVisibility() == View.INVISIBLE || messageRecycler.getVisibility() == View.GONE) {
                messageRecycler.setVisibility(View.VISIBLE);
            }

            // Call a helper for all the messages to add them to the chat history
            while (message.contains("%!:")) {
                addMessage(message.substring(0, message.indexOf("%!:")));
                message = message.substring(message.indexOf("%!:") + 3);
            }
        }
    }

    private int addMessage(String receivedMessage) {
        // Parse message
        int idx1 = receivedMessage.indexOf("!*:"), idx2 = receivedMessage.indexOf("*!:"), idx3 = receivedMessage.indexOf("!&:");
        String fullName = receivedMessage.substring(0, idx1) + " " + receivedMessage.substring(idx1 + 3, idx2);
        int userID = Integer.parseInt(receivedMessage.substring(idx2 + 3, idx3));
        String strippedMessage = receivedMessage.substring(idx3 + 3);
        String fullDate = strippedMessage.substring(0, strippedMessage.indexOf(": "));
        String message;
        if (strippedMessage.contains("%!:")) {
            message = strippedMessage.substring(strippedMessage.indexOf(": ") + 2, strippedMessage.indexOf("%!:"));
        } else {
            message = strippedMessage.substring(strippedMessage.indexOf(": ") + 2);
        }
        String time = fullDate.substring(fullDate.indexOf(" ") + 1, fullDate.lastIndexOf(":"));

        // Determine which user it is based on the userID
        MessageUser user;
        if (userID == mainUser.getUserID()) {
            user = mainUser;
        } else {
            user = otherUser;
        }

        // Add the message to the list
        messageAdapter.addItem(new Message(message, time, user));

        // Scroll to the bottom
        messageRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);

        return userID;
    }

    // Gets the list of past users messaged for the current user
    private void getPastUsersRequest() {
        // Generate the path string
        String URL = Const.serverURL + "message/destUserNames?userName=" + currUser.getFullName().replaceAll("\\s+", "!*:") + "*!:" + currUser.getUserID();

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        pastUsers = response;
                        try {
                            initialize();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                pastUsersError = error;
                try {
                    initialize();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}