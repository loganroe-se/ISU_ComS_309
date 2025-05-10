package com.example.cystaff_frontend.calendar;

import static com.example.cystaff_frontend.calendar.CalendarUtils.findEventsByDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

/**
 * Calendar adapter that handles the base level of the calendar views
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    /**
     * Creates a new calendar view holder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A calendar view holder
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        // Multiply by 1/6th
        layoutParams.height = (int) (parent.getHeight() * 0.166666666666666666);

        return new CalendarViewHolder(days, view, onItemListener);
    }

    /**
     * Sets the necessary views for a given item
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = days.get(position);
        // Ensure the date is not null
        if (date == null) {
            holder.dayOfMonth.setText("");
        } else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(CalendarUtils.selectedDate)) {
                holder.calendarCell.setBackgroundColor(Color.LTGRAY);
            }

            // Make the numbers gray if not from the current month
            if (!YearMonth.from(date).equals(YearMonth.from(CalendarUtils.selectedDate))) {
                holder.dayOfMonth.setTextColor(Color.LTGRAY);
            }

            if (!onItemListener.getClass().toString().contains("EventActivity")) {
                // Make the banners gray if not from the current month
                if (!YearMonth.from(date).equals(YearMonth.from(CalendarUtils.selectedDate))) {
                    holder.eventBannerOne.setTextColor(Color.parseColor("#737373"));
                    holder.eventBannerTwo.setTextColor(Color.parseColor("#737373"));
                    holder.extraEvents.setTextColor(Color.parseColor("#737373"));
                }

                // Call a helper to get the list of events on a given date
                ArrayList<EventItem> events = findEventsByDate(date);
                int eventsLength = events.size();

                // For all events on this date, hide/show banners accordingly
                if (eventsLength == 0) {
                    // All banners and extra events text should be hidden
                    holder.eventBannerOne.setVisibility(View.INVISIBLE);
                    holder.eventBannerTwo.setVisibility(View.INVISIBLE);
                    holder.extraEvents.setVisibility(View.INVISIBLE);
                } else if (eventsLength == 1) {
                    // Banner one should be shown and the rest hidden
                    holder.eventBannerOne.setVisibility(View.VISIBLE);
                    holder.eventBannerTwo.setVisibility(View.INVISIBLE);
                    holder.extraEvents.setVisibility(View.INVISIBLE);
                    // Set banner one text
                    holder.eventBannerOne.setText(events.get(0).getTitle());
                } else if (eventsLength == 2) {
                    // Banners one and two should be shown and the rest hidden
                    holder.eventBannerOne.setVisibility(View.VISIBLE);
                    holder.eventBannerTwo.setVisibility(View.VISIBLE);
                    holder.extraEvents.setVisibility(View.INVISIBLE);
                    // Set banner one and two text
                    holder.eventBannerOne.setText(events.get(0).getTitle());
                    holder.eventBannerTwo.setText(events.get(1).getTitle());
                } else {
                    // All banners and extra events text should be shown
                    holder.eventBannerOne.setVisibility(View.VISIBLE);
                    holder.eventBannerTwo.setVisibility(View.VISIBLE);
                    holder.extraEvents.setVisibility(View.VISIBLE);
                    // Set the extraEvents text appropriately
                    holder.extraEvents.setText("+" + (eventsLength - 2));
                    // Set banner one and two text
                    holder.eventBannerOne.setText(events.get(0).getTitle());
                    holder.eventBannerTwo.setText(events.get(1).getTitle());
                }
            }
        }
    }

    /**
     * Gets the number of days
     * @return The number of days
     */
    @Override
    public int getItemCount() {
        return days.size();
    }

    /**
     * OnItemListener interface to allow other classes to perform actions on item click
     */
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date, Context context);
    }
}
