package com.example.cystaff_frontend.directory;

import java.util.Comparator;

/**
 * A Class defining the behaviour of various directory sorting methods.
 */
public class DirectoryItemSorting {

    /**
     * Class used in order to sort directory items by last name, alphabetically
     */
    public static class SortByLastName implements Comparator<DirectoryItem> {
        @Override
        public int compare(DirectoryItem directoryItem, DirectoryItem directoryItem2) {
//            return directoryItem.getJobLevel().compareTo(directoryItem2.getJobLevel());
            return directoryItem.getLastName().compareTo(directoryItem2.getLastName());
        }
    }

    /**
     * Class used in order to sort directory items by last name in reverse alphabetical order
     */
    public static class ReverseSortByLastName implements Comparator<DirectoryItem> {
        @Override
        public int compare(DirectoryItem directoryItem, DirectoryItem directoryItem2) {
//            return directoryItem2.getJobLevel().compareTo(directoryItem.getJobLevel());
            return directoryItem2.getLastName().compareTo(directoryItem.getLastName());
        }
    }
}
