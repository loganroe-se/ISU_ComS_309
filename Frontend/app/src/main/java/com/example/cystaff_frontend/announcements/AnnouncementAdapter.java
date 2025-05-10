package com.example.cystaff_frontend.announcements;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cystaff_frontend.R;

import java.util.ArrayList;

/**
 * Public Class that defines the behaviour of the AnnouncementAdapter used for creating
 * announcement listings on the Announcements page.
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementHolder> {

    //Holds the announcement's Context and the ArrayList of announcements
    private Context context;
    private ArrayList<Announcement> announcements;

    /**
     * Constructor for the AnnouncementAdapter
     *
     * @param context       - The announcement's Context Object
     * @param announcements - The ArrayList of all announcements
     */
    public AnnouncementAdapter(Context context, ArrayList<Announcement> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public AnnouncementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the announcement item
        View view = LayoutInflater.from(context).inflate(R.layout.announcement_layout_item, parent, false);
        return new AnnouncementHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.AnnouncementHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.SetDetails(announcement);
    }

    @Override
    public int getItemCount() {
        //Return the size of the announcements ArrayList
        return announcements.size();
    }

    class AnnouncementHolder extends RecyclerView.ViewHolder {

        //Holds each TextView for the announcement
        private TextView txtTitle, txtMessage, txtMetaData;

        /**
         * Constructor for the AnnouncementHolder
         *
         * @param itemView - the itemView for the AnnouncementHolder
         */
        public AnnouncementHolder(@NonNull View itemView) {
            super(itemView);
            //Find the TextView elements in the View
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMetaData = itemView.findViewById(R.id.txtMetaData);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }

        /**
         * Method used to set the details of an announcement.
         * It calls the setText Method on each TextView for the announcement.
         *
         * @param announcement - The announcement Object to set the details for
         */
        void SetDetails(Announcement announcement) {
            //Set the announcement's text using setText Method
            txtTitle.setText(announcement.getTitle());
            txtMetaData.setText("Sent by: " + announcement.getFirstName() + " " +
                    announcement.getLastName() + " at " + announcement.getTime() + " on " +
                    announcement.getDate());
            txtMessage.setText(announcement.getMessage());
        }
    }

}
