package com.example.cystaff_frontend.header;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cystaff_frontend.AnnouncementsActivity;
import com.example.cystaff_frontend.CalendarActivity;
import com.example.cystaff_frontend.DirectoryActivity;
import com.example.cystaff_frontend.HomeActivity;
import com.example.cystaff_frontend.MessagingActivity;
import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.SettingsActivity;
import com.example.cystaff_frontend.TimeTrackingActivity;
import com.example.cystaff_frontend.ManageUsersActivity;
import com.example.cystaff_frontend.utils.User;
import com.google.android.material.navigation.NavigationView;

/**
 * A Class used for handling common functionality with the page Header
 */
public class Header extends AppCompatActivity {

    /**
     * A Method to initialise the header for the current activity.
     * It initialises the NavigationView, sets the correct header text, and
     * sets up the profile Spinner menu while overriding its DropDownView.
     *
     * @param activity  - the current activity
     * @param navView   - the activity's NavigationView
     * @param drwLayout - the activity's DrawerLayout
     * @param toolbar   - the activity's Toolbar
     * @param headerTxt - the activity's instance of the headerTxt TextView
     * @param profile   - the activity's instance of the profile Spinner Object
     * @param privilege - the current user's privilege id
     */
    public static void initialise(@NonNull Activity activity, NavigationView navView, DrawerLayout drwLayout,
                                  Toolbar toolbar, TextView headerTxt, Spinner profile, int privilege, User currUser) {
        //Declaring and initialising variables
        int checkedItemId, name;
        String activityName = activity.getClass().getSimpleName();
        String[] items = new String[]{"", "Profile Settings", "Logout"};
        ArrayAdapter<String> adapterTest = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, items) {
            /**
             * Overriding the default behaviour of Android's Dropdown View. It completely removes
             * the first entry from the View, such that only the Profile Settings and Logout
             * options are visible. This is necessary because on initialisation, the first option
             * in a Spinner is selected automatically, which causes issues if the first option is
             * a button that takes the user to a different page.
             *
             * @param position index of the item whose view we want.
             * @param convertView the old view to reuse, if possible. Note: You should
             *        check that this view is non-null and of an appropriate type before
             *        using. If it is not possible to convert this view to display the
             *        correct data, this method can create a new view.
             * @param parent the parent that this view will eventually be attached to
             * @return the new Dropdown View
             */
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                //Variable declaration and initialisation
                View v;

                //Testing if the position is the first entry
                if (position == 0) {
                    //Hide the first entry
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                } else {
                    //Otherwise, the dropdown menu operates as normal
                    v = super.getDropDownView(position, null, parent);
                }

                //Disable the scroll bar and return
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        //Instantiate a new SpinnerActivity (Profile Button)
        SpinnerActivity spinAct = new SpinnerActivity();

        //Determining which item should be marked as selected in the menu
        if (activityName.compareTo("AnnouncementsActivity") == 0) {
            checkedItemId = R.id.nav_announcements;
            name = R.string.announcements_name;
        } else if (activityName.compareTo("SettingsActivity") == 0) {
            checkedItemId = R.id.nav_settings;
            name = R.string.settings_name;
        } else if (activityName.compareTo("CalendarActivity") == 0) {
            checkedItemId = R.id.nav_calendar;
            name = R.string.calendar_name;
        } else if (activityName.compareTo("EventActivity") == 0) {
            checkedItemId = R.id.nav_calendar;
            name = R.string.event_name;
        } else if (activityName.compareTo("TimeTrackingActivity") == 0) {
            checkedItemId = R.id.nav_time;
            name = R.string.time_name;
        } else if (activityName.compareTo("DirectoryActivity") == 0) {
            checkedItemId = R.id.nav_directory;
            name = R.string.directory_name;
        } else if (activityName.compareTo("ManageUsersActivity") == 0) {
            checkedItemId = R.id.nav_manage;
            name = R.string.manage_name;
        } else if (activityName.compareTo("AddUserActivity") == 0) {
            checkedItemId = R.id.nav_manage;
            name = R.string.add_user_name;
        } else if (activityName.compareTo("CreateAnnouncementActivity") == 0) {
            checkedItemId = R.id.nav_create_announcement;
            name = R.string.create_announcement_name;
        } else if (activityName.compareTo("EditUserActivity") == 0) {
            checkedItemId = R.id.nav_manage;
            name = R.string.edit_user_name;
        } else if (activityName.compareTo("CheckingActivity") == 0) {
            checkedItemId = R.id.nav_time;
            name = R.string.checking_name;
        } else if (activityName.compareTo("MessagingActivity") == 0) {
            checkedItemId = R.id.nav_message;
            name = R.string.message_name;
        } else {
            checkedItemId = R.id.nav_home;
            name = R.string.home_name;
        }

        //Set up the navigation menu
        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drwLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drwLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) activity);
        navView.setCheckedItem(checkedItemId);

        // Depending on the user's privilege level, show/hide certain pages
        if (privilege == 1) {
            // Set building admin visibility
            navView.getMenu().findItem(R.id.nav_manage).setVisible(true);
        } else if (privilege == 2) {
            // Set system admin visibility
            navView.getMenu().findItem(R.id.nav_manage).setVisible(true);
        }

        //Set the header's title
        headerTxt.setText(name);

        //Set the ArrayAdapter to the Spinner
        profile.setAdapter(adapterTest);

        //Set the profile's ItemSelectedListener
        profile.setOnItemSelectedListener(spinAct);

        //Update the current activity and user in SpinnerActivity
        spinAct.setCurr(activity, currUser);
    }

    /**
     * A Helper Method to assist with determining which item was selected in the navbar.
     * It finds which button was pressed and then starts the respective Activity.
     * Mainly, this Method was created to capitalise on Object Oriented design,
     * reducing overall code.
     *
     * @param item      - The selected MenuItem
     * @param drwLayout - The activity's DrawerLayout
     * @param activity  - The current activity
     * @param userObj   - The user information
     */
    public static void selectHelper(MenuItem item, DrawerLayout drwLayout, Activity
            activity, User userObj) {
        //Getting the id of the button pressed
        int id = item.getItemId();
        Intent i;
        String activityName = activity.getClass().getSimpleName();

        //Checking which button was pressed
        if (id == R.id.nav_calendar && activityName.compareTo("CalendarActivity") != 0) {
            i = new Intent(activity, CalendarActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_home && activityName.compareTo("HomeActivity") != 0) {
            i = new Intent(activity, HomeActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_directory && activityName.compareTo("DirectoryActivity") != 0) {
            i = new Intent(activity, DirectoryActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_time && activityName.compareTo("TimeTrackingActivity") != 0) {
            i = new Intent(activity, TimeTrackingActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_settings && activityName.compareTo("SettingsActivity") != 0) {
            i = new Intent(activity, SettingsActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_announcements && activityName.compareTo("AnnouncementsActivity") != 0) {
            i = new Intent(activity, AnnouncementsActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_manage && activityName.compareTo("ManageUsersActivity") != 0) {
            i = new Intent(activity, ManageUsersActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        } else if (id == R.id.nav_message && activityName.compareTo("MessagingActivity") != 0) {
            i = new Intent(activity, MessagingActivity.class);
            i.putExtra("userInfo", userObj);
            activity.startActivity(i);
        }

        //Close the navigation drawer
        drwLayout.closeDrawer(GravityCompat.START);
    }
}
