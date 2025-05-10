package com.example.cystaff_frontend.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.util.ArrayList;

/**
 * Adapter for event users to be displayed
 */
public class EventUserAdapter extends RecyclerView.Adapter<EventUserAdapter.EventUserHolder> {

    // The interface for the recycler view
    private final EventUserInterface eventUserInterface;

    // Create a context and ArrayList of event user items to display
    private Context context;
    private ArrayList<EventUser> eventUsers;

    /**
     * A constructor for the EventUserAdapter class - Initializes private variables
     * @param context - The context for this class
     * @param eventUserItems - The items to be listed in the event page
     */
    public EventUserAdapter(Context context, ArrayList<EventUser> eventUserItems, EventUserInterface eventUserInterface) {
        this.context = context;
        this.eventUsers = eventUserItems;
        this.eventUserInterface = eventUserInterface;
    }

    /**
     * Creates a new EventUserHolder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return The new EventUserHolder
     */
    @NonNull
    @Override
    public EventUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventUserHolder(LayoutInflater.from(context).inflate(R.layout.event_user_item, parent, false), eventUserInterface);
    }

    /**
     * Sets the event user info for a given event
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventUserHolder holder, int position) {
        holder.setEventUserInfo(eventUsers.get(position));
    }

    /**
     * Updates the user list to a new list
     * @param newList - The list to update to
     */
    public void updateUserList(ArrayList<EventUser> newList) {
        this.eventUsers.clear();
        this.eventUsers.addAll(newList);
        this.notifyDataSetChanged();
    }

    /**
     * Gets the item count for the current event users
     * @return The item count as an int
     */
    @Override
    public int getItemCount() {
        return eventUsers.size();
    }

    /**
     * A holder class for event users
     */
    public static class EventUserHolder extends RecyclerView.ViewHolder {

        // Create variables for the views
        private ImageView eventUserProfilePic;
        private TextView nameView, emailView;

        /**
         * Constructor for the EventUserHolder class - Initializes variables
         * @param itemView
         * @param eventUserInterface
         */
        public EventUserHolder(@NonNull View itemView, EventUserInterface eventUserInterface) {
            super(itemView);
            eventUserProfilePic = itemView.findViewById(R.id.eventUserPic);
            nameView = itemView.findViewById(R.id.nameView);
            emailView = itemView.findViewById(R.id.emailView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventUserInterface != null) {
                        // Get the adapter's position and ensure validity
                        int pos = getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            eventUserInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }

        /**
         * Can be used to quickly set all of the event user info for a given user item
         * @param eventUser - The event user item containing all necessary information to set the info to
         */
        public void setEventUserInfo(EventUser eventUser) {
            // Set all of the event user info with the given eventUser information
            nameView.setText(eventUser.getName());
            emailView.setText(eventUser.getEmail());
            eventUserProfilePic.setImageResource(eventUser.getProfilePicture());
        }
    }
}
