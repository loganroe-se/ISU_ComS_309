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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import static kotlin.jvm.internal.Intrinsics.checkNotNull;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseExpandableListAdapter;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {
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
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
    }

    @Test
    public void changeDefaultSettings() throws InterruptedException {
        // Open the default settings expandable list
        onView(allOf(withId(R.id.settingsListTitle), withText("Default Settings"))).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(allOf(withId(R.id.settingsExpandedListItem), withText("Calendar Format"))).check(matches(isDisplayed()));
        Thread.sleep(SIMULATED_DELAY_MS);
        // Choose the week option for calendar format
        ViewInteraction textView = onView(allOf(withId(android.R.id.text1), withText("Month"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView.perform(click());
        onView(withText("Week")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose the reminder event type
        ViewInteraction textView2 = onView(allOf(withId(android.R.id.text1), withText("Meeting"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView2.perform(click());
        onView(withText("Reminder")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose the In-Person location type
        ViewInteraction textView3 = onView(allOf(withId(android.R.id.text1), withText("Online"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView3.perform(click());
        onView(withText("In-Person")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Enter 15 for number of announcements to display
        ViewInteraction editText = onView(allOf(withId(R.id.intEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText.perform(replaceText("15"), closeSoftKeyboard());
        // Choose the reverse alphabetical sort type
        ViewInteraction textView4 = onView(allOf(withId(android.R.id.text1), withText("A to Z"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView4.perform(click());
        onView(withText("Z to A")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Open the profile settings expandable list
        onView(allOf(withId(R.id.settingsListTitle), withText("Profile Settings"))).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        onView(allOf(withId(R.id.settingsExpandedListItem), withText(startsWith("Preferred Display Name")))).check(matches(isDisplayed()));
        Thread.sleep(SIMULATED_DELAY_MS);
        // Enter Log for preferred display name
        ViewInteraction editText2 = onView(allOf(withId(R.id.textEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText2.perform(replaceText("Log"), closeSoftKeyboard());
        // Enter 290-123-4567 for the phone number
        ViewInteraction editText3 = onView(allOf(withId(R.id.phoneNumberEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText3.perform(replaceText("290-123-4567"), closeSoftKeyboard());
        // Press the change password button
        ViewInteraction button = onView(allOf(withId(R.id.changePasswordBtn),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        button.perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Check that the dialog popped up
        ViewInteraction viewGroup = onView(allOf(withParent(allOf(withId(android.R.id.content), withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))), isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        // Enter the current password, but wrong and try to change password
        onView(withId(R.id.currentPasswordEditText)).perform(replaceText("lroe2"), closeSoftKeyboard());
        onView(withId((R.id.confirmPasswordChange))).perform(click());
        onView(withId(R.id.changePasswordErrText)).check(matches(isDisplayed()));
        // Enter the current password correctly and try to change password - Note: no new passwords have been entered
        onView(withId(R.id.currentPasswordEditText)).perform(replaceText("lroe"), closeSoftKeyboard());
        onView(withId((R.id.confirmPasswordChange))).perform(click());
        onView(withId(R.id.changePasswordErrText)).check(matches(isDisplayed()));
        // Enter the current password correctly and try to add a new password (but don't repeat it)
        onView(withId(R.id.newPasswordEditText)).perform(replaceText("lroe"), closeSoftKeyboard());
        onView(withId((R.id.confirmPasswordChange))).perform(click());
        onView(withId(R.id.changePasswordErrText)).check(matches(isDisplayed()));
        // Enter the current password correctly and try to add a new password and repeat it, but it is too short
        onView(withId(R.id.newPasswordRepeatedEditText)).perform(replaceText("lroe"), closeSoftKeyboard());
        onView(withId((R.id.confirmPasswordChange))).perform(click());
        onView(withId(R.id.changePasswordErrText)).check(matches(isDisplayed()));
        // Cancel changing password
        onView(withId(R.id.cancelPasswordChange)).perform(click());
        Thread.sleep(SIMULATED_DELAY_MS);
        // Apply changes
        onView(withId(R.id.settingsApplyBtn)).perform(click());
        onView(withId(R.id.settingsErrText)).check(matches(not(isDisplayed())));
        // Revert back to what it was previously
        // Choose the Month option for calendar format
        ViewInteraction textView5 = onView(allOf(withId(android.R.id.text1), withText("Week"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView5.perform(click());
        onView(withText("Month")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose the Meeting event type
        ViewInteraction textView6 = onView(allOf(withId(android.R.id.text1), withText("Reminder"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView6.perform(click());
        onView(withText("Meeting")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose the Online location type
        ViewInteraction textView7 = onView(allOf(withId(android.R.id.text1), withText("In-Person"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView7.perform(click());
        onView(withText("Online")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Enter 10 for number of announcements to display
        ViewInteraction editText4 = onView(allOf(withId(R.id.intEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText4.perform(replaceText("10"), closeSoftKeyboard());
        // Choose the reverse alphabetical sort type
        ViewInteraction textView8 = onView(allOf(withId(android.R.id.text1), withText("Z to A"),withParent(allOf(withId(R.id.optionSelect),withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),isDisplayed()));
        textView8.perform(click());
        onView(withText("A to Z")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Enter Logan for preferred display name
        ViewInteraction editText5 = onView(allOf(withId(R.id.textEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText5.perform(replaceText("Logan"), closeSoftKeyboard());
        // Enter 290-123-4567 for the phone number
        ViewInteraction editText6 = onView(allOf(withId(R.id.phoneNumberEntry),withParent(withParent(withId(R.id.settingsListView))),isDisplayed()));
        editText6.perform(replaceText("292-125-4567"), closeSoftKeyboard());
        // Apply changes
        onView(withId(R.id.settingsApplyBtn)).perform(click());
        onView(withId(R.id.settingsErrText)).check(matches(not(isDisplayed())));
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

//    private static Matcher<Object> withChildName(String name) {
//        checkNotNull(name);
//        return withChildName(equalTo(name));
//    }
//
//    private static Matcher<Object> withChildName(final Matcher<String> name) {
//        checkNotNull(name);
//        // ChildStruct is the Class returned by BaseExpandableListAdapter.getChild()
//        return new BoundedMatcher<Object, ChildStruct>(ChildStruct.class){
//
//            @Override
//            public boolean matchesSafely (final ChildStruct childStruct){
//                return name.matches(childStruct.name);
//            }
//
//            @Override
//            public void describeTo (Description description){
//                name.describeTo(description);
//            }
//        } ;
//    }
}
