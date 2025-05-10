package com.example.cystaff_frontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.cystaff_frontend.calendar.CalendarMonthView;
import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.intellij.lang.annotations.JdkConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CalendarActivityTest {
    private static final int SIMULATED_DELAY_MS = 500;
    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        // Log in
        onView(withId(R.id.emailEditText)).perform(typeText("lroe@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("lroe"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Navigate to the calendar page
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_calendar));
    }

    @Test
    public void testBasicCalendarFunctionality() throws InterruptedException {
        // Confirm the month view is showing up (default)
        onView(withId(R.id.mainCalendarMonthView)).check(matches(isDisplayed()));
        // Click on a location with no events
        ViewInteraction recyclerView = onView(allOf(withId(R.id.calendarViewRecycler), withParent(withParent(withParent(withId(R.id.mainCalendarMonthView)))), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(8, click()));
        Thread.sleep(SIMULATED_DELAY_MS*2);
        // Confirm the recycler view is hidden
        onView(withId(R.id.eventDisplayRecycler)).check(matches(not(isDisplayed())));
        // Select a location with events
        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.calendarViewRecycler),withParent(withParent(withParent(withId(R.id.mainCalendarMonthView)))), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(7, click()));
        // Confirm that the event text is shown
        ViewInteraction textView = onView(allOf(withId(R.id.eventDisplayText), withText("Dec 3, 2023"),withParent(allOf(withId(R.id.scrollViewConstraintLayout),withParent(withId(R.id.calendarScrollView)))),isDisplayed()));
        textView.check(matches(isDisplayed()));
        // Click on the week display
        ViewInteraction materialButton = onView(allOf(withId(R.id.weekSelection), withText("Week"),childAtPosition(allOf(withId(R.id.calendarViewSelection),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),2)),0),isDisplayed()));
        materialButton.perform(click());
        // Confirm the week view is shown
        onView(withId(R.id.mainCalendarWeekView)).check(matches(isDisplayed()));
        // Confirm the text is the same as it was on the month view
        ViewInteraction textView2 = onView(allOf(withId(R.id.eventDisplayText), withText("Dec 3, 2023"),withParent(allOf(withId(R.id.scrollViewConstraintLayout),withParent(withId(R.id.calendarScrollView)))),isDisplayed()));
        textView2.check(matches(withText("Dec 3, 2023")));
        // Go back to the month view
        ViewInteraction materialButton2 = onView(allOf(withId(R.id.monthSelection), withText("Month"), childAtPosition( allOf(withId(R.id.calendarViewSelection),childAtPosition( withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),2)),1),isDisplayed()));
        materialButton2.perform(click());
        // Confirm the text on the month view
        ViewInteraction textView3 = onView(allOf(withId(R.id.monthYearText), withText("December 2023"),withParent(withParent(withId(R.id.calendarView))),isDisplayed()));
        textView3.check(matches(withText("December 2023")));
        // Go to the left (previous month)
        ViewInteraction materialButton3 = onView(allOf(withId(R.id.previousViewBtn), withText("<"),childAtPosition(childAtPosition(withId(R.id.calendarView),0), 0),isDisplayed()));
        materialButton3.perform(click());
        // Go to the right (next month)
        ViewInteraction materialButton4 = onView(allOf(withId(R.id.nextViewBtn), withText(">"),childAtPosition(childAtPosition(withId(R.id.calendarView),0),2), isDisplayed()));
        materialButton4.perform(click());
    }

    @Test
    public void testNewEvent() throws InterruptedException {
        // Open the new event page
        onView(withId(R.id.newEventBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Cancel the addition of an event
        onView(withId(R.id.cancelBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Open the new event page
        onView(withId(R.id.newEventBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Confirm the new page was opened
        onView(withId(R.id.startDay)).check(matches(isDisplayed()));
        // Confirm the date buttons work
        onView(withId(R.id.startDay)).perform(click());
        onView(withId(R.id.calendarMonthViewSelect)).check(matches(isDisplayed()));
        // Click on a location with no events
        ViewInteraction recyclerView = onView(allOf(withId(R.id.calendarViewRecycler), withParent(withParent(withParent(withId(R.id.calendarMonthViewSelect)))), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(9, click()));
        Thread.sleep(SIMULATED_DELAY_MS*2);
        // Continue confirming date buttons
        onView(withId(R.id.startDay)).perform(click());
        onView(withId(R.id.calendarMonthViewSelect)).check(matches(not(isDisplayed())));
        onView(withId(R.id.endDay)).perform(click());
        onView(withId(R.id.calendarMonthViewSelect)).check(matches(isDisplayed()));
        onView(withId(R.id.endDay)).perform(click());
        onView(withId(R.id.calendarMonthViewSelect)).check(matches(not(isDisplayed())));
        onView(withId(R.id.startTime)).perform(click());
        onView(withId(R.id.timePicker)).check(matches(isDisplayed()));
        onView(withId(R.id.startTime)).perform(click());
        onView(withId(R.id.timePicker)).check(matches(not(isDisplayed())));
        onView(withId(R.id.endTime)).perform(click());
        onView(withId(R.id.timePicker)).check(matches(isDisplayed()));
        onView(withId(R.id.endTime)).perform(click());
        onView(withId(R.id.timePicker)).check(matches(not(isDisplayed())));
        // Click on reminder event type
        onView(withId(R.id.reminderSelection)).perform(click());
        onView(withId(R.id.currentMemberText)).check(matches(not(isDisplayed())));
        // Click on the meeting event type
        onView(withId(R.id.meetingSelection)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.currentMemberText)).check(matches(isDisplayed()));
        // Click on online location type
        onView(withId(R.id.onlineSelection)).perform(click());
        onView(withId(R.id.buildingTextLayout)).check(matches(not(isDisplayed())));
        // Click on the in-person location type
        onView(withId(R.id.inPersonSelection)).perform(click());
        onView(withId(R.id.buildingTextLayout)).check(matches(isDisplayed()));
        // Search for someone to invite
        onView(withId(R.id.searchTextInput)).perform(typeText("a\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Click on them to invite them
        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.inviteeRecycler), childAtPosition(withId(R.id.constraintLayout),16)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        // Show the current invitees
        onView(withId(R.id.showHideCurrMembers)).perform(click());
        // Delete the member who was just invited
        ViewInteraction button = onView(allOf(withId(R.id.deleteBtn),childAtPosition(childAtPosition(withClassName(Matchers.is("androidx.cardview.widget.CardView")),0),3),isDisplayed()));
        button.perform(click());
        // Click on the save button
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Confirm it didn't save due to missing a title
        onView(withId(R.id.errText)).check(matches(isDisplayed()));
        // Add more users, including myself (shouldn't work)
        onView(withId(R.id.searchTextInput)).perform(typeText("a\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        ViewInteraction recyclerView3 = onView(allOf(withId(R.id.inviteeRecycler), childAtPosition(withId(R.id.constraintLayout),16)));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.searchTextInput)).perform(typeText("a\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        recyclerView3.perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.searchTextInput)).perform(typeText("lroe@iastate.edu\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        recyclerView3.perform(actionOnItemAtPosition(0, click()));
        // Hide the current invitees
        onView(withId(R.id.showHideCurrMembers)).perform(click());
        // Add a title
        onView(withId(R.id.titleTextInput)).perform(replaceText("automatedTest"));
        // Add a building
        onView(withId(R.id.buildingInput)).perform(replaceText("Agronomy"));
        // Add a room
        onView(withId(R.id.roomInput)).perform(replaceText("101"));
        // Click on the save button
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Confirm we are back on the calendar page
        onView(withId(R.id.newEventBtn)).check(matches(isDisplayed()));
        // Click on a location with no events - Same as where it was created
        ViewInteraction recyclerView4 = onView(allOf(withId(R.id.calendarViewRecycler), withParent(withParent(withParent(withId(R.id.mainCalendarMonthView)))), isDisplayed()));
        recyclerView4.perform(actionOnItemAtPosition(9, click()));
        Thread.sleep(SIMULATED_DELAY_MS*2);
        // Go to week calendar
        ViewInteraction materialButton = onView(allOf(withId(R.id.weekSelection), withText("Week"),childAtPosition(allOf(withId(R.id.calendarViewSelection),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),2)),0),isDisplayed()));
        materialButton.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private static ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp = (TimePicker) view;
                tp.setHour(hour);
                tp.setMinute(minute);
            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }
}
