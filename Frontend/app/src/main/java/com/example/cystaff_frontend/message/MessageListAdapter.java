package com.example.cystaff_frontend.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;
import com.example.cystaff_frontend.utils.User;

import java.util.ArrayList;

/**
 * A Public Class describing the behaviour of the adapter used for the list of messages.
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    // Holds integers to determine if the message was sent/received
    private static final int MESSAGE_SENT = 1, MESSAGE_RECEIVED = 2;

    // Variables to hold the context, the list of messages, and the current user
    private Context mContext;
    private ArrayList<Message> messageList;
    private User currUser;

    /**
     * Constructor for the message list adapter
     *
     * @param context     - The context
     * @param messageList - The list of messages
     */
    public MessageListAdapter(Context context, ArrayList<Message> messageList, User currUser) {
        mContext = context;
        this.messageList = messageList;
        this.currUser = currUser;
    }

    /**
     * Retrieves the number of messages
     *
     * @return - Returns the number of messages total
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * Returns the item view type of the current message
     *
     * @param pos - position to query
     * @return - Returns the view type as an integer
     */
    @Override
    public int getItemViewType(int pos) {
        // Get the message object
        Message message = messageList.get(pos);

        // Depending on who sent the message, return the corresponding integer
        if (message.getSender().getUserID() == currUser.getUserID()) {
            return MESSAGE_SENT;
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    /**
     * Add a new item to the list of messages
     *
     * @param message - The message to add
     */
    public void addItem(Message message) {
        messageList.add(message);
        this.notifyItemInserted(messageList.size() - 1);
    }

    /**
     * Clears the list of messages
     */
    public void clearItems() {
        messageList.clear();
    }

    /**
     * Inflates the layout that is correct depending on viewType
     *
     * @param parent   - Parent view
     * @param viewType - Integer that determines if the message was sent or received
     * @return - Returns the view that should be used for this message
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a variable for the view that will be returned
        View view;

        // Depending on if the message was sent or received, return a new holder of that type
        if (viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_chat_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        // The message is neither sent nor received
        return null;
    }

    /**
     * Gives the message object to a view holder
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param pos    The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        // Get the message to bind
        Message message = messageList.get(pos);

        if (holder.getItemViewType() == MESSAGE_SENT) {
            // Bind the message to a sent message holder
            ((SentMessageHolder) holder).bind(message);
        } else {
            // Bind the message to a received message holder
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    // Holder class for the received message
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        // Variables to hold the received message information
        private TextView messageText, timeText, nameText;
        private ImageView profilePicture;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textChatMessageOther);
            timeText = itemView.findViewById(R.id.textChatTimestampOther);
            nameText = itemView.findViewById(R.id.textChatUserOther);
            profilePicture = itemView.findViewById(R.id.chatProfileImageOther);
        }

        void bind(Message message) {
            // Set the text, time, user name, and user profile picture of the message
            messageText.setText(message.getMessage());
            timeText.setText(message.getDate());
            nameText.setText(message.getSender().getName());
            profilePicture.setImageResource(message.getSender().getProfilePicture());
        }
    }

    // Holder class for the sent message
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        // Variables to hold the received message information
        private TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textChatMessageMe);
            timeText = itemView.findViewById(R.id.textChatTimestampMe);
        }

        void bind(Message message) {
            // Set the text, time, user name, and user profile picture of the message
            messageText.setText(message.getMessage());
            timeText.setText(message.getDate());
        }
    }
}
