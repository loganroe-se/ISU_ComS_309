package com.example.cystaff_frontend;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTests {
    private static final int SIMULATED_DELAY_MS = 500;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void noEmail() throws InterruptedException {
        // No email
        onView(withId(R.id.passwordEditText)).perform(typeText("lroe"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        Thread.sleep(SIMULATED_DELAY_MS);

        // Make sure the activity did not change and the error text is proper
        onView(withId(R.id.emailTextInput)).check(matches(hasTextInputLayoutErrorText("You need to enter an email address.")));
    }

    @Test
    public void noPassword() throws InterruptedException {
        // No email
        onView(withId(R.id.emailEditText)).perform(typeText("lroe@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        Thread.sleep(SIMULATED_DELAY_MS);

        // Make sure the activity did not change and the error text is proper
        onView(withId(R.id.passwordTextInput)).check(matches(hasTextInputLayoutErrorText("You need to enter a password.")));
    }

    @Test
    public void wrongEmail() throws InterruptedException {
        // Incorrect email but correct password
        onView(withId(R.id.emailEditText)).perform(typeText("lro@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("lroe"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        Thread.sleep(SIMULATED_DELAY_MS);

        // Make sure the activity did not change and the error text is proper
        onView(withId(R.id.errTextBox)).check(matches(isDisplayed()));
        onView(withId(R.id.errTextBox)).check(matches(withText("Invalid email/password. Please try again.")));
    }

    @Test
    public void wrongPassword() throws InterruptedException {
        // Correct email but incorrect password
        onView(withId(R.id.emailEditText)).perform(typeText("lroe@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("lro"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        Thread.sleep(SIMULATED_DELAY_MS);

        // Make sure the activity did not change and the error text is proper
        onView(withId(R.id.errTextBox)).check(matches(isDisplayed()));
        onView(withId(R.id.errTextBox)).check(matches(withText("Invalid email/password. Please try again.")));
    }

    @Test
    public void correctLogin() throws InterruptedException {
        // Correct email & correct password
        onView(withId(R.id.emailEditText)).perform(typeText("lroe@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("lroe"), closeSoftKeyboard());
        Thread.sleep(SIMULATED_DELAY_MS);
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(HomeActivity.class.getName(), null, false);
        onView(withId(R.id.loginButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        Thread.sleep(SIMULATED_DELAY_MS);

        // Make sure the activity did changed
        Activity activity = instrumentation.waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(activity);
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
}
