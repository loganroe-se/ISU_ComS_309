package com.example.cystaff_frontend.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Holds utilities to hide the android soft keyboard
 */
public class GeneralUtilFuncs {
    /**
     * Hides the android soft keyboard
     * @param activity - The current activity
     */
    public static void hideKeyboard(Activity activity) {
        // Create an input method manager
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one
        if (view == null) {
            view = new View(activity);
        }
        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
