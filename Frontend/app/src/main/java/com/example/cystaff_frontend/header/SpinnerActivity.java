package com.example.cystaff_frontend.header;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cystaff_frontend.MainActivity;
import com.example.cystaff_frontend.SettingsActivity;
import com.example.cystaff_frontend.notifications.NotificationHandler;
import com.example.cystaff_frontend.notifications.NotificationSocketManager;
import com.example.cystaff_frontend.utils.User;

/**
 * This Class defines the behaviour of the SpinnerActivity that is used in the header (The dropdown
 * that contains the Profile Settings and Logout options).
 */
public class SpinnerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Stores the current active activity
    public Activity curr;

    //Stores the current User instance
    private User currUser;

    //Initialisation value to bypass the default of Spinners
    private int init = 0;

    /**
     * This Method manages the selection of items in the Spinner dropdown menu.
     * It determines which option was selected before executing the code
     * respective to the option. The Method also contains a safeguard from
     * running on initialisation. In Android, Spinners are given a default selection,
     * so the Method is called when the Spinner is instantiated. As this is behaviour
     * our program does not utilise, a simple test is included to ensure that the Method
     * is inert on first execution.
     *
     * @param parent   - The AdapterView where the selection happened
     * @param view     - The view within the AdapterView that was clicked
     * @param position - The position of the view in the adapter
     * @param id       - The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Testing if this is the first call after initialisation
        if (init != 0) {
            //Variable declaration
            Intent i;

            //Get the name of the selected item
            String item = (String) parent.getItemAtPosition(position);

            Log.d("MY_ID", "" + id);

            //Testing which item was selected from the menu
            if (item.compareTo("Logout") == 0) {
                i = new Intent(curr, MainActivity.class);
                NotificationHandler temp = (NotificationHandler) NotificationSocketManager.getInstance().getWebSocketListener();
                temp.setActivity(null);
                NotificationSocketManager.getInstance().disconnectWebSocket();
            } else {
                i = new Intent(curr, SettingsActivity.class);
                i.putExtra("userInfo", currUser);
            }

            //Start the activity (Moves pages)
            curr.startActivity(i);
        } else {
            //No longer the first call after initialisation, Method can proceed next time
            init++;
        }
    }

    /**
     * Method that dictates Spinner's behaviour when nothing is selected. For this
     * implementation, nothing occurs when selected, so the Method is purely to
     * meet specifications and exist in case it is needed in the future.
     *
     * @param parent - The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Setter for setting the current activity and User.
     *
     * @param activity - The activity to set curr to
     * @param currUser - The User to set currUser to
     */
    public void setCurr(Activity activity, User currUser) {
        curr = activity;
        this.currUser = currUser;
    }
}
