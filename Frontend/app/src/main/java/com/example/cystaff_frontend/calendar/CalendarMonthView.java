package com.example.cystaff_frontend.calendar;

import static com.example.cystaff_frontend.calendar.CalendarUtils.daysInMonthArray;
import static com.example.cystaff_frontend.calendar.CalendarUtils.monthYearFromDate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cystaff_frontend.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Custom view for a calendar month
 */
public class CalendarMonthView extends LinearLayout implements CalendarAdapter.OnItemListener {
    // Internal components
    private Button prevView, nextView;
    private TextView monthYearText;
    private RecyclerView calendarViewRecycler;
    private CalendarAdapter.OnItemListener onItemListener;

    /**
     * Basic constructor when only given a context
     * @param context - The context
     */
    public CalendarMonthView(Context context) {
        super(context);
        initComponents(context);
    }

    /**
     * Basic constructor when given a context and attributes
     * @param context - The context
     * @param attrs - The attribute set
     */
    public CalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponents(context);
    }

    /**
     * Basic constructor when given a context, attributes, and an attribute style
     * @param context - The context
     * @param attrs - The attribute set
     * @param defStyleAttr - The attribute style
     */
    public CalendarMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        setMonthView(context);

        // Listen for the previous month button to be clicked
        prevView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back a month
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
                setMonthView(view.getContext());
            }
        });

        // Listen for the next month button to be clicked
        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go forward a month
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
                setMonthView(view.getContext());
            }
        });
    }

    /**
     * Sets the month view -- Will update days shown, grayed out, and selected
     * @param context - The context
     */
    public void setMonthView(Context context) { // Context should be gotten via getApplicationContext
        // Set the Month Year text with the selected date
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        // Move everything up if first 7 are blank (meaning first week would be blank)
        if (daysInMonth.get(0) == null && daysInMonth.get(1) == null && daysInMonth.get(2) == null && daysInMonth.get(3) == null
            && daysInMonth.get(4) == null && daysInMonth.get(5) == null && daysInMonth.get(6) == null) {
            for(int i = 0; i < 7; i++) {
                daysInMonth.remove(0);
                daysInMonth.add(null);
            }
        }

        // Get the days in the previous month
        ArrayList<LocalDate> daysInPrevMonth = daysInMonthArray(CalendarUtils.selectedDate.minusMonths(1));

        // Find the index of the last day in the previous month array
        int lastNonNull = 0;
        for(int i = daysInPrevMonth.size() - 1; i >= 0; i--) {
            if (daysInPrevMonth.get(i) != null) {
                lastNonNull = i;
                break;
            }
        }

        // Loop through the beginning days until all nulls have been replaced with days from the previous month
        for(int i = IntStream.range(0, daysInMonth.size()).filter(a -> daysInMonth.get(a) != null).findFirst().orElse(0) - 1; i >= 0; i--) {
            daysInMonth.set(i, daysInPrevMonth.get(lastNonNull--));
        }

        // Get the days in the next month
        ArrayList<LocalDate> daysInNextMonth = daysInMonthArray(CalendarUtils.selectedDate.plusMonths(1));

        // Find the index of the last day in the current month array
        int lastCurrNonNull = 0;
        for(int i = daysInMonth.size() - 1; i >= 0; i--) {
            if (daysInMonth.get(i) != null) {
                lastCurrNonNull = ++i;
                break;
            }
        }

        // Loop through all ending days until all necessary nulls have been replaced with days from the next month
        int firstNonNull = IntStream.range(0, daysInNextMonth.size()).filter(a -> daysInNextMonth.get(a) != null).findFirst().orElse(0);
        for(int i = lastCurrNonNull; i < daysInMonth.size(); i++) {
            // Put the correct date in the current position
            daysInMonth.set(i, daysInNextMonth.get(firstNonNull++));

            // Break out of the loop if at the end of a week
            if (i % 7 == 6) {
                break;
            }
        }

        // Populate the adapter and recycler view
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, getItemListener());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 7);
        calendarViewRecycler.setLayoutManager(layoutManager);
        calendarViewRecycler.setAdapter(calendarAdapter);
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
     * When a calendar's item gets clicked
     * @param position - The position of the date clicked
     * @param date - The date clicked
     * @param context - The context of the date clicked
     */
    @Override
    public void onItemClick(int position, LocalDate date, Context context) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView(context);
        }
    }
}
