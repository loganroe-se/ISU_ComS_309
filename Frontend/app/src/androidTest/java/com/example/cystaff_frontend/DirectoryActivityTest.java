package com.example.cystaff_frontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

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
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DirectoryActivityTest {
    private static final int SIMULATED_DELAY_MS = 500;
    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);
    private Instrumentation instrumentation;
    private Instrumentation.ActivityMonitor monitor;

    @Before
    public void setUp() throws InterruptedException {
        // Log in
        onView(withId(R.id.emailEditText)).perform(typeText("lroe@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("lroe"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Navigate to the directory page
        instrumentation = getInstrumentation();
        monitor = instrumentation.addMonitor(DirectoryActivity.class.getName(), null, false);
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_directory));
    }

    @Test
    public void testInput() throws InterruptedException {
        typeValue("Logan");
    }

    @Test
    public void testEmptyInput() throws InterruptedException {
        // Press the enter key
        onView(withId(R.id.searchTextInput)).perform(typeText("\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Confirm views are not shown
        onView(withId(R.id.results)).check(matches(not(isDisplayed())));
        onView(withId(R.id.sortTextLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.dirErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())));
        // Check search box error text
        onView(withId(R.id.searchTextInputLayout)).check(matches(hasTextInputLayoutErrorText("You need to enter a search query.")));
    }

    @Test
    public void testSort() throws InterruptedException {
        // Type a
        typeValue("a");
        // Open the drop down
        ViewInteraction checkableImageButton = onView(
                allOf(withId(com.google.android.material.R.id.text_input_end_icon), withContentDescription("Show dropdown menu"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.EndCompoundLayout")),
                                        1),
                                0),
                        isDisplayed()));
        checkableImageButton.perform(click());

        closeSoftKeyboard();

        // Ensure that the recycler, results, and sort option are still visible
        // And that the error box is not shown
        onView(withId(R.id.results)).check(matches(isDisplayed()));
        onView(withId(R.id.sortTextLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.dirErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testFilter() throws InterruptedException {
        // Press the end icon
        ViewInteraction checkableImageButton = onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon), childAtPosition( childAtPosition(withClassName(is("com.google.android.material.textfield.EndCompoundLayout")),1),0),isDisplayed()));
        checkableImageButton.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Check that the dialog popped up
        ViewInteraction viewGroup = onView(allOf(withParent(allOf(withId(android.R.id.content), withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))), isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        // Click the reset filters button
        ViewInteraction materialButton = onView(allOf(withId(R.id.resetBtn), withText("Reset Filters"), childAtPosition(childAtPosition( withId(android.R.id.content), 0),1),isDisplayed()));
        materialButton.perform(click());
        // Click apply
        ViewInteraction materialButton2 = onView(allOf(withId(R.id.applyBtn), withText("Apply"),childAtPosition(childAtPosition( withId(android.R.id.content), 0), 2), isDisplayed()));
        materialButton2.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Press the end icon
        ViewInteraction checkableImageButton2 = onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon), childAtPosition( childAtPosition(withClassName(is("com.google.android.material.textfield.EndCompoundLayout")),1),0),isDisplayed()));
        checkableImageButton2.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Click the X button
        ViewInteraction materialTextView = onView(allOf(withId(R.id.closeBtn), withText("X"),childAtPosition(childAtPosition(withId(android.R.id.content),0),0),isDisplayed()));
        materialTextView.perform(click());
        // Press the end icon
        ViewInteraction checkableImageButton3 = onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon), childAtPosition( childAtPosition(withClassName(is("com.google.android.material.textfield.EndCompoundLayout")),1),0),isDisplayed()));
        checkableImageButton3.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Open the Building drop down
        onView(allOf(withId(R.id.listHeader), withText("Building"))).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(allOf(withId(R.id.dirFilterName), withText("Agronomy"))).check(matches(isDisplayed()));
        Thread.sleep(SIMULATED_DELAY_MS);
        // Choose the Coover option for building filter
        onData(anything()).atPosition(10).perform(click());
        onData(anything()).atPosition(0).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Open the Job Level drop down
        onView(allOf(withId(R.id.listHeader), withText("Job Level"))).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Choose the System Admin option
        onData(anything()).atPosition(2).perform(click());
        // Click the apply button
        onView(withId(R.id.applyBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Search with these filters
        onView(withId(R.id.searchTextInput)).perform(typeText("a\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Confirm views are shown
        onView(withId(R.id.results)).check(matches(isDisplayed()));
        onView(withId(R.id.sortTextLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.dirErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) { }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                String hint = error.toString();

                return expectedErrorText.equals(hint);
            }
        };
    }

    private void typeValue(String value) throws InterruptedException {
        onView(withId(R.id.searchTextInput)).perform(typeText(value), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Enter key hasn't been pressed yet, expect nothing to have changed
        onView(withId(R.id.results)).check(matches(not(isDisplayed())));
        onView(withId(R.id.sortTextLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.dirErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())));
        // Press the enter key
        onView(withId(R.id.searchTextInput)).perform(typeText("\n"), closeSoftKeyboard());
        // Confirm views are shown - Can't test amounts as it is not guaranteed to stay the same
        onView(withId(R.id.results)).check(matches(isDisplayed()));
        onView(withId(R.id.sortTextLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.dirErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
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
