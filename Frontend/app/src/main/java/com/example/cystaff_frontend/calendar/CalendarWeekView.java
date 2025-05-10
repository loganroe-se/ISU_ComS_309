package com.example.cystaff_frontend.calendar;

import static com.example.cystaff_frontend.calendar.CalendarUtils.daysInWeekArray;
import static com.example.cystaff_frontend.calendar.CalendarUtils.monthYearFromDate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

/**
 * Custom view for a calendar week
 */
public class CalendarWeekView extends LinearLayout implements CalendarAdapter.OnItemListener {
    // Internal components
    private Button prevView, nextView;
    private TextView monthYearText;
    private RecyclerView calendarViewRecycler;
    private CalendarAdapter.OnItemListener onItemListener;

    /**
     * Basic constructor when only given a context
     * @param context - The context
     */
    public CalendarWeekView(Context context) {
        super(context);
        initComponents(context);
    }

    /**
     * Basic constructor when given a context and attributes
     * @param context - The context
     * @param attrs - The attribute set
     */
    public CalendarWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponents(context);
    }

    /**
     * Basic constructor when given a context, attributes, and an attribute style
     * @param context - The context
     * @param attrs - The attribute set
     * @param defStyleAttr - The attribute style
     */
    public CalendarWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents(context);
    }

    private void initComponents(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this);

        // Get all variables
        prevView = findViewById(R.id.previousViewBtn);
        nextView = findViewById(R.id.nextViewBtn);
        monthYearText = findViewById(R.id.monthYearText);
        calendarViewRecycler = findViewById(R.id.calendarViewRecycler);
        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView(context);

        // Listen for the previous month button to be clicked
        prevView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back a month
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
                setWeekView(view.getContext());
            }
        });

        // Listen for the next month button to be clicked
        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go forward a month
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
                setWeekView(view.getContext());
            }
        });
    }

    /**
     * Used to manually override what listener is being used for item clicks
     * @param onItemListener - The item listener to be used for this instance of the month view
     */
    public void setItemListener(CalendarAdapter.OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    // Gets the current item listener - If null, returns this
    private CalendarAdapter.OnItemListener getItemListener() {
        // If null, return this, otherwise, return the one set manually
        if (onItemListener == null) {
            return this;
        } else {
            return onItemListener;
        }
    }

    /**
     * Sets the week view -- Will update days shown, grayed out, and selected
     * @param context - The context
     */
    public void setWeekView(Context context) { // Context should be gotten via getApplicationContext
        // Set the Month Year text with the selected date
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        // Populate the adapter and recycler view
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, getItemListener());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 7);
        calendarViewRecycler.setLayoutManager(layoutManager);
        calendarViewRecycler.setAdapter(calendarAdapter);
    }

    /**
     * When a calendar's item gets clicked
     * @param position - The position of the date clicked
     * @param date - The date clicked
     * @param context - The context of the date clicked
     */
    @Override
    public void onItemClick(int position, LocalDate date, Context context) {
        CalendarUtils.selectedDate = date;
        setWeekView(context);
    }
}
