package com.example.cystaff_frontend.message;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;

import com.example.cystaff_frontend.MessagingActivity;

import java.util.Arrays;

/**
 * A Class describing code for handling messages from the server websocket.
 */
public class MessageHandler extends AppCompatActivity implements MessageWebSocketListener {

    @Override
    public void onWebSocketMessage(String message, MessagingActivity activity) {
        // Call the sendMessage method
//        sendMessage(findViewById(android.R.id.content).getRootView()); // Should this call a method in the messagingActivity instead? ***********************

        // Call the message method within MessagingActivity
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.handleNewMessage(message);
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("MessageWebSocket", reason);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata, MessagingActivity activity) {
        // Call the method within MessagingActivity to populate the history, if any
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateHistory(Arrays.toString(handshakedata.getContent()));
            }
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("MessageWebSocket", ex.getMessage());
    }
}