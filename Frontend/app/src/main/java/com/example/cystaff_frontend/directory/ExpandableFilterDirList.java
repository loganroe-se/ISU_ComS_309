package com.example.cystaff_frontend.directory;

import com.example.cystaff_frontend.utils.Const;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to populate the filter expandable list in the employee directory screen
 */
public class ExpandableFilterDirList {
    /**
     * Creates a HashMap of directory entries from a given filter
     *
     * @return itemsList - the list of filtered directory items
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static HashMap<String, List<String>> getFilterItems() {
        // Create a hash map for the list of items
        HashMap<String, List<String>> itemsList = new HashMap<String, List<String>>();

        // Create and populate the job level list
        List<String> jobLevel = new ArrayList<String>(Arrays.asList(Const.privilegeTypes.keySet().toArray(new String[0])));

        // Create and populate the building list
        List<String> buildingList = new ArrayList<String>(Arrays.asList(Const.buildings));

        // Put the lists in their corresponding groups
        itemsList.put("Job Level", jobLevel);
        itemsList.put("Building", buildingList);

        return itemsList;
    }
}
