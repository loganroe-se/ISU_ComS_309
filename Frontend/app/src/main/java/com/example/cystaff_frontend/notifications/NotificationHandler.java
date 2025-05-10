package com.example.cystaff_frontend.notifications;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.announcements.CreateAnnouncementActivity;

import org.java_websocket.handshake.ServerHandshake;

/**
 * A Public Class for handling the incoming Notifications from the server websocket.
 */
public class NotificationHandler extends AppCompatActivity implements NotificationSocketListener {

    // Stores the message to display as a notification
    private String message;

    //Stores the activity to display a notification at
    private Activity activity;

    /**
     * A setter for the activity private field.
     *
     * @param activity - the activity to save into the private field
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * This Method creates a popup window for a global notification sent over a websocket
     * at the current View. The popup window will have a title, the sent message, and an "Ok"
     * button to close the popup.
     *
     * @param view - the view to inflate the popup at
     */
    public void createNotification(View view) {
        //Create the LayoutInflater instance to inflate the popup
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the popup
        View popupView = inflater.inflate(R.layout.notification_popup, null);

        //Set the message text in the notification
        TextView text = popupView.findViewById(R.id.notificationText);
        text.setText(message);

        //Get the properties for setting the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        //Create the popup window instance
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Create a custom touch interceptor to test if the touch was inside the popup
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            /**
             * Tests if the touch was within the bounds of the notification popup.
             * This is to prevent the popup from closing if a user interacts outside of it.
             * The popup must be closed using the confirmation button.
             *
             * @param v The view the touch event has been dispatched to.
             * @param event The MotionEvent object containing full information about
             *        the event.
             * @return true if the touch was within the popup, false otherwise
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Return true if the touch was inside the popupView
                if (event.getX() < 0 || event.getX() > popupView.getWidth()) {
                    return true;
                } else if (event.getY() < 0 || event.getY() > popupView.getHeight()) {
                    return true;
                }

                //Otherwise, the touch was outside of the popupView
                return false;
            }
        });

        //Show the popup
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Find the dismiss button instance
        Button dismissBtn = popupView.findViewById(R.id.notificationConfirm);

        //Set a click listener on the dismiss button. Dismisses when clicked
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismiss the popup when clicked
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("Websocket", "Opened");
    }

    /**
     * Overwritten Method for handling a message received from the websocket.
     * When a message is received and the user is on a valid activity for receiving notifications
     * (logged in to the application), a notification is displayed using the UI Thread.
     *
     * @param message The received WebSocket message.
     */
    @Override
    public void onWebSocketMessage(String message) {
        //Get the message
        this.message = message;

        //Checking if there is a root
        if (activity != null) {
            Log.d("Websocket", "In");
            runOnUiThread(new Runnable() {
                /**
                 * Calls the createNotification Method using a UI Thread to create a notification
                 * on the current screen.
                 */
                @Override
                public void run() {
                    createNotification(activity.findViewById(android.R.id.content).getRootView());
                }
            });

        } else {

        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.d("WebSocket", ex.getMessage());
    }
}
