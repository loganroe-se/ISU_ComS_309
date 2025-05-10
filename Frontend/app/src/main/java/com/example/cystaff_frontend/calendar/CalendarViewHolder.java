package com.example.cystaff_frontend.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A base level calendar view holder
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ArrayList<LocalDate> days;
    public final TextView dayOfMonth, eventBannerOne, eventBannerTwo, extraEvents;
    public final View calendarCell;
    private final CalendarAdapter.OnItemListener onItemListener;

    /**
     * Constructor to initialize all private variables
     * @param days - The days in the current view
     * @param itemView - The view
     * @param onItemListener - The listener used
     */
    public CalendarViewHolder(ArrayList<LocalDate> days, @NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        this.days = days;
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        eventBannerOne = itemView.findViewById(R.id.eventBannerOne);
        eventBannerTwo = itemView.findViewById(R.id.eventBannerTwo);
        extraEvents = itemView.findViewById(R.id.extraEvents);
        calendarCell = itemView.findViewById(R.id.calendarCell);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    /**
     * An on item click method that triggers when a day is clicked
     * @param view - The view
     */
    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getBindingAdapterPosition(), days.get(getBindingAdapterPosition()), view.getContext());
    }
}
