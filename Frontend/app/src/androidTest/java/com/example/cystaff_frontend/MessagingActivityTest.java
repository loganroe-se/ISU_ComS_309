package com.example.cystaff_frontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
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

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessagingActivityTest {
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
        // Navigate to the messaging page
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_message));
    }

    @Test
    public void testUserManipulation() throws InterruptedException {
        // Check that the starting screen is right
        ViewInteraction textView = onView(allOf(withId(R.id.messageHeaderText), withText("Andrew Sand"),withParent(withParent(withId(R.id.messageHeader))),isDisplayed()));
        textView.check(matches(withText("Andrew Sand")));
        onView(withId(R.id.helpText)).check(matches(not(isDisplayed())));
        // Open the navigation bar and go to the second person on it
        ViewInteraction appCompatImageButton2 = onView(allOf(withContentDescription("Open navigation drawer"),childAtPosition(allOf(withId(R.id.messageHeader),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),0)),1),isDisplayed()));
        appCompatImageButton2.perform(click());
        ViewInteraction navigationMenuItemView2 = onView(allOf(childAtPosition(allOf(withId(com.google.android.material.R.id.design_navigation_view),childAtPosition(withId(R.id.nav_messageView),0)),2),isDisplayed()));
        navigationMenuItemView2.perform(click());
        // Confirm the header text is right
        ViewInteraction textView2 = onView(allOf(withId(R.id.messageHeaderText), withText("Rodrigo Santamaria"),withParent(withParent(withId(R.id.messageHeader))),isDisplayed()));
        textView2.check(matches(withText("Rodrigo Santamaria")));
        // Open the navigation drawer and try to add a user
        ViewInteraction appCompatImageButton3 = onView(allOf(withContentDescription("Open navigation drawer"),childAtPosition(allOf(withId(R.id.messageHeader),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),0)),1),isDisplayed()));
        appCompatImageButton3.perform(click());
        ViewInteraction materialButton = onView(allOf(withId(R.id.addUserToMessages),childAtPosition(childAtPosition(withId(com.google.android.material.R.id.navigation_header_container),0),1),isDisplayed()));
        materialButton.perform(click());
        // Confirm the bottom sheet dialog appeared and enter "yi"
        ViewInteraction viewGroup = onView(allOf(withId(R.id.addUserContainer),withParent(allOf(withId(com.google.android.material.R.id.design_bottom_sheet),withParent(withId(com.google.android.material.R.id.coordinator)))),isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        onView(withId(R.id.searchTextInput)).perform(typeText("yi\n"), closeSoftKeyboard());
        // Select the top result
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recyclerView),childAtPosition(withId(R.id.addUserContainer),2)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(SIMULATED_DELAY_MS);
        // Ensure the center of the screen has text saying there are no messages currently with this user
        ViewInteraction textView3 = onView(allOf(withId(R.id.helpText), withText("There is currently no message history with this user. Send a message below!"),withParent(withParent(withId(R.id.messageDrawer))),isDisplayed()));
        textView3.check(matches(withText("There is currently no message history with this user. Send a message below!")));
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
}
