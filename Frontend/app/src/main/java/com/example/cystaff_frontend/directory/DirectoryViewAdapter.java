package com.example.cystaff_frontend.directory;

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
 * Public Class defining the ViewAdapter used for the Directory page.
 */
public class DirectoryViewAdapter extends RecyclerView.Adapter<DirectoryViewAdapter.DirectoryViewHolder> {

    // The interface for the recycler view
    private final DirectoryViewInterface directoryViewInterface;

    // Create a context and ArrayList of directory items to display
    private Context context;
    private ArrayList<DirectoryItem> directoryItems;

    /**
     * A constructor for the DirectoryViewAdapter class - Initializes private variables
     *
     * @param context        - The context for this class
     * @param directoryItems - The items to be listed in the directory page
     */
    public DirectoryViewAdapter(Context context, ArrayList<DirectoryItem> directoryItems, DirectoryViewInterface directoryViewInterface) {
        this.context = context;
        this.directoryItems = directoryItems;
        this.directoryViewInterface = directoryViewInterface;
    }

    @NonNull
    @Override
    public DirectoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DirectoryViewHolder(LayoutInflater.from(context).inflate(R.layout.directory_layout_item, parent, false), directoryViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectoryViewHolder holder, int position) {
        holder.setDirectoryInfo(directoryItems.get(position));
    }

    public void updateDirectoryList(ArrayList<DirectoryItem> newList) {
        this.directoryItems.clear();
        this.directoryItems.addAll(newList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return directoryItems.size();
    }

    public static class DirectoryViewHolder extends RecyclerView.ViewHolder {

        // Create variables for the views
        private ImageView directoryProfilePic;
        private TextView fullNameView, emailView, numberView, buildingView, jobLevelView;

        /**
         * Constructor for the DirectoryViewHolder class - Initializes variables
         *
         * @param itemView
         * @param directoryViewInterface
         */
        public DirectoryViewHolder(@NonNull View itemView, DirectoryViewInterface directoryViewInterface) {
            super(itemView);
            directoryProfilePic = itemView.findViewById(R.id.directoryProfilePic);
            fullNameView = itemView.findViewById(R.id.fullNameView);
            emailView = itemView.findViewById(R.id.emailView);
            numberView = itemView.findViewById(R.id.numberView);
            buildingView = itemView.findViewById(R.id.buildingView);
            jobLevelView = itemView.findViewById(R.id.jobLevelView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (directoryViewInterface != null) {
                        // Get the adapter's position and ensure validity
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            directoryViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }

        /**
         * Can be used to quickly set all of the directory info for a given directory item
         *
         * @param directoryItem - The directory item containing all necessary information to set the info to
         */
        public void setDirectoryInfo(DirectoryItem directoryItem) {
            // Set all of the directory info with the given directoryItem information
            fullNameView.setText(directoryItem.getFullName());
            emailView.setText(directoryItem.getEmail());
            numberView.setText(directoryItem.getNumber());
            buildingView.setText(directoryItem.getBuilding());
            jobLevelView.setText(directoryItem.getJobLevel());
            directoryProfilePic.setImageResource(directoryItem.getProfilePic());
        }
    }
}
