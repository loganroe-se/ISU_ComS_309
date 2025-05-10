package com.example.cystaff_frontend.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Adapter for the displaying of events in the calendar page
 */
public class EventDisplayAdapter extends RecyclerView.Adapter<EventDisplayAdapter.EventDisplayHolder> {
    // The interface for the recycler view
    private final EventDisplayInterface eventDisplayInterface;

    // Create a context and ArrayList of events to display
    private Context context;
    private ArrayList<EventItem> eventItems;

    /**
     * A constructor for the EventDisplayAdapter class - Initializes private variables
     * @param context - The context for this class
     * @param eventItems - The items to be listed in the event display on the calendar page
     * @param eventDisplayInterface - The interface used
     */
    public EventDisplayAdapter(Context context, ArrayList<EventItem> eventItems, EventDisplayInterface eventDisplayInterface) {
        this.context = context;
        this.eventItems = eventItems;
        this.eventDisplayInterface = eventDisplayInterface;
    }

    /**
     * Creates a new event display holder for a given view
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return Event display holder
     */
    @NonNull
    @Override
    public EventDisplayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventDisplayHolder(LayoutInflater.from(context).inflate(R.layout.event_display_layout, parent, false), eventDisplayInterface);
    }

    /**
     * Sets the info for a given event
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventDisplayHolder holder, int position) {
        holder.setEventItemInfo(eventItems.get(position));
    }

    /**
     * Updates the event list with a new ArrayList
     * @param newList - The list to update to
     */
    public void updateEventList(ArrayList<EventItem> newList) {
        this.eventItems.clear();
        this.eventItems.addAll(newList);
        this.notifyDataSetChanged();
    }

    /**
     * Gets the number of event items for the current display
     * @return The size of the events array as an int
     */
    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    /**
     * Holder class for the event display
     */
    public static class EventDisplayHolder extends RecyclerView.ViewHolder {

        // Create variables for the views
        private TextView title, date, eventType, members, locationType, buildingRoom, creator;
        private Button deleteBtn;

        /**
         * Constructor for the EventUserHolder class - Initializes variables
         * @param itemView - The item view
         * @param eventDisplayInterface - The interface
         */
        public EventDisplayHolder(@NonNull View itemView, EventDisplayInterface eventDisplayInterface) {
            super(itemView);
            title = itemView.findViewById(R.id.titleView);
            date = itemView.findViewById(R.id.date);
            eventType = itemView.findViewById(R.id.eventType);
            members = itemView.findViewById(R.id.membersText);
            locationType = itemView.findViewById(R.id.locationType);
            buildingRoom = itemView.findViewById(R.id.buildingRoomText);
            creator = itemView.findViewById(R.id.creatorText);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventDisplayInterface != null) {
                        // Get the adapter's position and ensure validity
                        int pos = getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            eventDisplayInterface.onEndItemClick(pos);
                        }
                    }
                }
            });
        }

        /**
         * Can be used to quickly set all of the event info for a given item
         * @param eventItem - The event item containing all necessary information to set the info to
         */
        @SuppressLint("SetTextI18n")
        public void setEventItemInfo(EventItem eventItem) {
            // Set all of the event info with the given event information
            title.setText(eventItem.getTitle());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM d");
            date.setText(eventItem.getStartDay().format(dtf) + ", " + eventItem.getStartTime() + " - " + eventItem.getEndDay().format(dtf) + ", " + eventItem.getEndTime());
            eventType.setText(eventItem.getEventType());
            if (eventItem.getEventType().equals("Meeting")) {
                members.setText("Members: " + String.join(", ", eventItem.getRecipientNames()));
                if (eventItem.getRecipientNames().length != 0) {
                    members.setVisibility(View.VISIBLE);
                } else {
                    members.setVisibility(View.GONE);
                }
            } else {
                members.setVisibility(View.GONE);
            }
            locationType.setText(eventItem.getLocationType());
            if (eventItem.getLocationType().equals("Online")) {
                buildingRoom.setVisibility(View.GONE);
            } else {
                buildingRoom.setVisibility(View.VISIBLE);
                buildingRoom.setText(CalendarUtils.findBuildingRoom(eventItem.getRoomID()));
            }
            creator.setText("Created By: " + eventItem.getCreatorName());
        }
    }
}
