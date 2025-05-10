package com.example.cystaff_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cystaff_frontend.directory.CustomFilterDirListAdapter;
import com.example.cystaff_frontend.directory.DirectoryItem;
import com.example.cystaff_frontend.directory.DirectoryItemSorting;
import com.example.cystaff_frontend.directory.DirectoryViewAdapter;
import com.example.cystaff_frontend.directory.DirectoryViewInterface;
import com.example.cystaff_frontend.directory.ExpandableFilterDirList;
import com.example.cystaff_frontend.header.Header;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.Const;
import com.example.cystaff_frontend.app.AppController;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Class defining the behaviour of the directory page
 */
@SuppressWarnings("SpellCheckingInspection")
public class DirectoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DirectoryViewInterface {

    // Create variable for search bar text layout
    private TextInputLayout searchInput, sortLayout;

    // Create variable for search bar text input and results text
    private TextView searchText, resultsText, headerTxt;

    // Create variables for sort by selection
    private final String[] sortItems = {"Alphabetical", "Reverse Alphabetical"};
    private String defaultSort = "Alphabetical";
    private String currentSort = defaultSort;
    private AutoCompleteTextView sortByAutoComplete;
    private ArrayAdapter<String> sortByAdapter;

    // Create string for search bar input
    private String searchValue;

    // Create dialog box and buttons for popup
    private Dialog filterDialog;
    private TextView closeBtn;
    private Button applyBtn, resetBtn;
    private ExpandableListView filterListView;
    private CustomFilterDirListAdapter filterListAdapter;
    private List<String> titleList;
    private HashMap<String, List<String>> currItemList = new HashMap<String, List<String>>();
    private HashMap<String, List<View>> currItemViews = new HashMap<String, List<View>>();
    private HashMap<String, List<String>> detailList;

    // Create variables for the recycler view
    private RecyclerView directoryView;
    private ArrayList<DirectoryItem> directoryItems;
    private ArrayList<DirectoryItem> directoryItemsPriv;
    private DirectoryViewAdapter directoryAdapter;

    // Set up the progress bar
    private ProgressBar mProgressBar;

    // Create error text text view
    private TextView errText;

    // Create a flag for if a filter has been added before results displayed
    private boolean filterFlag;

    // Create the response JSON object
    private JSONArray JSONResponse = null;

    // Create the error for the JSON response
    private VolleyError JSONError = null;

    // Holds the DrawerLayout used to create the navbar
    private DrawerLayout drwLayout;

    // Holds the NavigationView used by the header/navbar
    private NavigationView navView;

    // Holds the Toolbar instance used on the page
    private Toolbar toolbar;

    //Holds the reference to the header's profile button
    private Spinner profile;

    // Hold the user object for the current user
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        //Set the notification handler's root View
        NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
        temp.setActivity(this);

        // Get the user
        currUser = getIntent().getParcelableExtra("userInfo", User.class);

        // Assign screen elements
        searchInput = findViewById(R.id.searchTextInputLayout);
        searchText = findViewById(R.id.searchTextInput);
        mProgressBar = findViewById(R.id.progressBar);
        resultsText = findViewById(R.id.results);
        sortLayout = findViewById(R.id.sortTextLayout);
        sortByAutoComplete = findViewById(R.id.sortAutoComplete);
        errText = findViewById(R.id.dirErrText);
        headerTxt = findViewById(R.id.headerText);
        drwLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.header);
        profile = findViewById(R.id.profile);

        //Initialise the page's header
        Header.initialise(this, navView, drwLayout, toolbar, headerTxt, profile, currUser.getPrivilegeID(), currUser);

        // Update sort type
        defaultSort = currUser.getSettings().get("sortDirectoryBy");
        currentSort = defaultSort;

        // Set up sort by menu and set a default
        sortByAdapter = new ArrayAdapter<String>(this, R.layout.list_dir_item, sortItems);
        sortByAutoComplete.setAdapter(sortByAdapter);
        sortByAutoComplete.setText(defaultSort);

        // Set the dialog with the wanted pop up activity
        filterDialog = new Dialog(this);
        filterDialog.setContentView(R.layout.activity_directory_pop_up);
        Objects.requireNonNull(filterDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Define filter dialog buttons
        closeBtn = filterDialog.findViewById(R.id.closeBtn);
        applyBtn = filterDialog.findViewById(R.id.applyBtn);
        resetBtn = filterDialog.findViewById(R.id.resetBtn);

        // Set up the filter list view
        filterListView = filterDialog.findViewById(R.id.filterList);
        detailList = ExpandableFilterDirList.getFilterItems();
        titleList = new ArrayList<String>(detailList.keySet());
        filterListAdapter = new CustomFilterDirListAdapter(this, titleList, detailList);
        filterListView.setAdapter(filterListAdapter);

        // Initialize the hashmap used to keep track of filters chosen
        for (int i = 0; i < titleList.size(); i++) {
            currItemList.put(titleList.get(i), new ArrayList<String>());
            currItemViews.put(titleList.get(i), new ArrayList<View>());
        }

        // Set up the recycler view
        directoryView = findViewById(R.id.recyclerView);
        directoryItems = new ArrayList<DirectoryItem>();
        directoryItemsPriv = new ArrayList<DirectoryItem>();
        directoryView.setLayoutManager(new LinearLayoutManager(this));
        directoryAdapter = new DirectoryViewAdapter(getApplicationContext(), directoryItems, this);
        directoryView.setAdapter(directoryAdapter);

        // Perform actions when the enter key is pressed
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                // Check if the event is an enter key
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Read text input
                    searchValue = Objects.requireNonNull(searchInput.getEditText()).getText().toString();

                    // Clear all previous results
                    directoryItems = new ArrayList<DirectoryItem>();
                    directoryItemsPriv = new ArrayList<DirectoryItem>();

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

        // When the filter button is clicked, open another menu to apply filters
        searchInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog box
                filterDialog.show();

                // If the close button is pressed, close the dialog box
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterDialog.dismiss();
                    }
                });

                // If any children in the list have been clicked, apply the check mark and store the values
                filterListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View view, int titlePos, int childPos, long id) {
                        // Get the clicked child's title
                        String title = titleList.get(titlePos);
                        // Get the clicked the child's string
                        String child = Objects.requireNonNull(detailList.get(title)).get(childPos);
                        // Get the current list of items for the current title
                        List<String> currList = currItemList.get(title);
                        // Get the current list of views for the current title
                        List<View> currViews = currItemViews.get(title);

                        // Change the current checkbox value to the opposite based on if it is in the list or not
                        if (currList != null && currList.contains(child)) {
                            // Remove the item just clicked from the list and uncheck the check box
                            currList.remove(child);
                            currItemList.put(title, currList);
                            currViews.remove(view);
                            currItemViews.put(title, currViews);
                            filterListAdapter.setChildCheckBox(titlePos, childPos, view, false);
                        } else {
                            // Add the item just clicked to the list and check the check box
                            currList.add(child);
                            currItemList.put(title, currList);
                            currViews.add(view);
                            currItemViews.put(title, currViews);
                            filterListAdapter.setChildCheckBox(titlePos, childPos, view, true);
                        }

                        return false;
                    }
                });

                // If the apply button is pressed, close the popup and update the list
                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If the results haven't been displayed yet, update the flag value
                        // Otherwise, call the update list method
                        if (directoryView.getVisibility() == View.INVISIBLE || directoryView.getVisibility() == View.GONE) {
                            filterFlag = true;
                        } else {
                            updateDirectoryItemList("Filter", currentSort);
                        }

                        // Close the popup window
                        filterDialog.dismiss();
                    }
                });

                // If the reset button is pressed, simply remove all check marks
                resetBtn.setOnClickListener(new View.OnClickListener() {
                    // Loop through all currently applied filters and remove them from the list and uncheck their box
                    @Override
                    public void onClick(View view) {
                        // Define temporary variables
                        List<String> currList;
                        List<View> currViews;
                        String currTitle;

                        // Loop through the titles
                        for (int i = 0; i < currItemList.size(); i++) {
                            // Get the current title
                            currTitle = titleList.get(i);
                            // Get the list for the current title
                            currList = currItemList.get(currTitle);
                            // Get the list of views for the current title
                            currViews = currItemViews.get(currTitle);
                            // Loop through the current list of filters
                            for (int j = 0; j < currList.size(); j++) {
                                // Uncheck the child's check box
                                filterListAdapter.setChildCheckBox(i, detailList.get(currTitle).indexOf(currList.get(j)), currViews.get(j), false);
                            }

                            // Remove all items from currList and update currItemList
                            currList = new ArrayList<String>();
                            currItemList.put(currTitle, currList);

                            // Remove all items from currViews and update currItemViews
                            currViews = new ArrayList<View>();
                            currItemViews.put(currTitle, currViews);
                        }
                    }
                });
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
            // Read the employee directory information
            try {
                // Iterate through the employee directory information in the JSON Array
                for (int i = 0; i < JSONResponse.length(); i++) {
                    createDirectoryItemList(JSONResponse.getJSONObject(i));
                }

                // Ensure the filters and sorting has been applied
                updateDirectoryItemList("Filter", currentSort);
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

        // If the filter has been applied, call the update method with filter tag
        if (filterFlag) {
            updateDirectoryItemList("Filter", defaultSort);
        } else {
            updateDirectoryItemList("Sorting", defaultSort);
        }

        // Make the Results text show up and update to include number of results
        resultsText.setVisibility(View.VISIBLE);
        String newText = "Results (" + directoryItems.size() + ")";
        resultsText.setText(newText);

        // Make the sort by menu show up
        sortLayout.setVisibility(View.VISIBLE);

        // Make the directories visible
        directoryView.setVisibility(View.VISIBLE);

        // Set a listener for the sort by button
        sortByAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the chosen item and call an update for the list
                String sortItem = adapterView.getItemAtPosition(i).toString();
                currentSort = sortItem;
                updateDirectoryItemList("Sorting", sortItem);
            }
        });
    }

    private void createDirectoryItemList(JSONObject directoryItem) throws JSONException {
        // Get the privilege object
        JSONObject priv = directoryItem.getJSONObject("privilege");

        // Create a new DirectoryItem
        //DirectoryItem item = new DirectoryItem(directoryItem.getInt("userid"), directoryItem.getString("firstName"), directoryItem.getString("lastName"), directoryItem.getString("email"), directoryItem.getString("phone"), directoryItem.getString("building"), priv.getString("type"), directoryItem.getString("birthday"), (int) directoryItem.get("image"));
        DirectoryItem item = new DirectoryItem(directoryItem.getInt("userid"), directoryItem.getString("firstName"), directoryItem.getString("lastName"), directoryItem.getString("email"), directoryItem.getString("phone"), directoryItem.getString("building"), priv.getString("type"), directoryItem.getString("birthday"), R.drawable.profile);

        // Add the item to the lists of items
        directoryItemsPriv.add(item);
        directoryItems.add(item);

        // Notify the adapter of the new addition
        directoryAdapter.notifyItemInserted(directoryItems.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDirectoryItemList(String callFromLoc, String sortItem) {
        // Create temporary variables
        ArrayList<DirectoryItem> currDirectoryItems = new ArrayList<DirectoryItem>();
        ArrayList<DirectoryItem> tempDirectoryItems = new ArrayList<DirectoryItem>();
        List<String> currList;
        DirectoryItem currItem, tempItem;
        int totalFilters = 0;

        // If this was called from the filter button, update the list
        // Otherwise, make a deep copy of the current directory items to avoid overlap
        if (callFromLoc.equals("Filter")) {
            // Loop through the items chosen for filter from currItemList and apply them
            // Note: All items in currItemList are currently 'chosen' items
            for (int i = 0; i < currItemList.size(); i++) {
                // Get the current title
                String title = titleList.get(i);
                // Create a new list and assign it the values for the current title
                currList = currItemList.get(title);

                // Loop through all of the items for this current list
                for (int j = 0; j < Objects.requireNonNull(currList).size(); j++) {
                    // Increment the total number of filters selected
                    totalFilters++;

                    // Loop through all possible directory items
                    for (int k = 0; k < directoryItemsPriv.size(); k++) {
                        // Get the current item
                        currItem = directoryItemsPriv.get(k);

                        // Check if the item matches the current filter
                        switch (title) {
                            case "Building":
                                if (currItem.getBuilding().equalsIgnoreCase(currList.get(j)) && !tempDirectoryItems.contains(currItem)) {
                                    tempDirectoryItems.add(currItem);
                                }
                                break;
                            case "Job Level":
                                if (currItem.getJobLevel().equalsIgnoreCase(currList.get(j)) && !tempDirectoryItems.contains(currItem)) {
                                    tempDirectoryItems.add(currItem);
                                }
                                break;
                        }
                    }
                }
            }

            // If no filters were applied, add all items
            if (totalFilters == 0) {
                tempDirectoryItems = directoryItemsPriv;
            }

            // Set the temporary directory items to the current ones
            currDirectoryItems = tempDirectoryItems;
        } else {
            // Deep copy items from directoryItems
            for (int i = 0; i < directoryItems.size(); i++) {
                currItem = directoryItems.get(i);
                tempItem = new DirectoryItem(currItem.getUserID(), currItem.getFirstName(), currItem.getLastName(), currItem.getEmail(), currItem.getNumber(), currItem.getBuilding(), currItem.getJobLevel(), currItem.getBirthday(), currItem.getProfilePic());
                currDirectoryItems.add(tempItem);
            }
        }

        // Perform sorting based on what is currently chosen
        // Note: Alphabetical order is based on last name
        switch (sortItem) {
            case "Alphabetical":
                currDirectoryItems.sort(new DirectoryItemSorting.SortByLastName());
                break;
            case "Reverse Alphabetical":
                currDirectoryItems.sort(new DirectoryItemSorting.ReverseSortByLastName());
                break;
        }

        // Update results text
        String newText = "Results (" + currDirectoryItems.size() + ")";
        resultsText.setText(newText);

        // Update directory items
        directoryItems = currDirectoryItems;

        // Update the listing
        directoryAdapter.updateDirectoryList(currDirectoryItems);
    }

    /**
     * Used to perform various actions when one of the results is clicked
     *
     * @param position - The position of the item clicked in the recycler view
     */
    @Override
    public void onItemClick(int position) {
        Toast.makeText(getApplicationContext(), directoryItems.get(position).getFullName(), Toast.LENGTH_SHORT).show();
    }
}