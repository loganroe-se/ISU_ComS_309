package com.example.cystaff_frontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
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
public class ManageUsersActivityTest {
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
        // Navigate to the manage users page
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_manage));
        Thread.sleep(SIMULATED_DELAY_MS);
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
        onView(withId(R.id.manErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())));
        // Check search box error text
        onView(withId(R.id.searchTextInputLayout)).check(matches(hasTextInputLayoutErrorText("You need to enter a search query.")));
    }

    @Test
    public void testFAB() throws InterruptedException {
        // Press the FAB
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.addUserFAB)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Ensure that one of the views exists
        onView(withId(R.id.newUserText)).check(matches(isDisplayed()));
        // Ensure that pressing the save button with no text entry won't work
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.errText)).check(matches(isDisplayed()));
        // Press the cancel button and make sure it returns to the previous page
        onView(withId(R.id.cancelBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.addUserFAB)).check(matches(isDisplayed()));
    }

    @Test
    public void testUsers() throws InterruptedException {
        // Press the FAB
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(withId(R.id.addUserFAB)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Ensure that one of the views exists
        onView(withId(R.id.newUserText)).check(matches(isDisplayed()));
        // Fill in the fields and add the user
        onView(withId(R.id.firstNameTextInput)).perform(typeText("First"), closeSoftKeyboard());
        onView(withId(R.id.lastNameTextInput)).perform(typeText("Last"), closeSoftKeyboard());
        onView(withId(R.id.passwordTextInput)).perform(typeText("firstLast"), closeSoftKeyboard());
        onView(withId(R.id.emailTextInput)).perform(typeText("firstLastTestUsers@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.numberTextInput)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.privilegeTypeLayout)).perform(click());
        onView(withText("Normal User")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.buildingInput)).perform(typeText("Agronomy"), closeSoftKeyboard());
        onView(withId(R.id.birthdayTextInput)).perform(typeText("12052023"), closeSoftKeyboard());
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS*2);
        // Search for the new user
        onView(withId(R.id.searchTextInput)).perform(typeText("firstLastTestUsers\n"), closeSoftKeyboard());
        // Click on the first result
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recyclerView),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),  6)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(SIMULATED_DELAY_MS);
        // Click on the edit button
        ViewInteraction materialButton = onView( allOf(withId(R.id.editBtn), withText("Edit User"),childAtPosition( childAtPosition(withId(android.R.id.content),0),12),isDisplayed()));
        materialButton.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Ensure that the new page has opened
        onView(withId(R.id.tempPasswordBtn)).check(matches(isDisplayed()));
        // Add a temporary password
        onView(withId(R.id.tempPasswordBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Check that the dialog popped up
        ViewInteraction viewGroup = onView(allOf(withParent(allOf(withId(android.R.id.content), withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))), isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        // Enter a temp password
        onView(withId(R.id.passwordTextInput)).perform(typeText("tempFirstLast"), closeSoftKeyboard());
        // Click save
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Edit the birthday
        onView(withId(R.id.birthdayTextInput)).perform(typeText("12052022"), closeSoftKeyboard());
        // Hit the save button
        onView(withId(R.id.saveBtn)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS*2);
        // Check that we are back to the main manage users page
        onView(withId(R.id.searchTextInputLayout)).check(matches(isDisplayed()));
        // Search for the user
        onView(withId(R.id.searchTextInput)).perform(typeText("firstLastTestUsers\n"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Delete the user
        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recyclerView),childAtPosition(withClassName(Matchers.is("androidx.constraintlayout.widget.ConstraintLayout")),  6)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        ViewInteraction viewGroup2 = onView(allOf(withParent(allOf(withId(android.R.id.content), withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))), isDisplayed()));
        viewGroup2.check(matches(isDisplayed()));
        ViewInteraction materialButton2 = onView( allOf(withId(R.id.deleteBtn), withText("Delete User"),childAtPosition( childAtPosition(withId(android.R.id.content),0),11),isDisplayed()));
        materialButton2.perform(click());
        ViewInteraction viewGroup3 = onView(allOf(withParent(allOf(withId(android.R.id.content), withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))), isDisplayed()));
        viewGroup3.check(matches(isDisplayed()));
        ViewInteraction materialButton3 = onView( allOf(withId(R.id.yesBtn), withText("Yes"),childAtPosition( childAtPosition(withId(android.R.id.content),0),2),isDisplayed()));
        materialButton3.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Check that the results are empty
        onView(withId(R.id.results)).check(matches(withText("Results (0)")));
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
        onView(withId(R.id.manErrText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())));
        // Press the enter key
        onView(withId(R.id.searchTextInput)).perform(typeText("\n"), closeSoftKeyboard());
        // Confirm views are shown - Can't test amounts as it is not guaranteed to stay the same
        onView(withId(R.id.results)).check(matches(isDisplayed()));
        onView(withId(R.id.manErrText)).check(matches(not(isDisplayed())));
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
