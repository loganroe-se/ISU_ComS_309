package com.example.cystaff_frontend.utils;

import java.util.HashMap;

/**
 * A public Class that stores various constant values that are used throughout the application.
 */
@SuppressWarnings("SpellCheckingInspection")
public class Const {
    /**
     * The base URL for the server
     */
    public static final String serverURL = "http://coms-309-010.class.las.iastate.edu:8080/";

    /**
     * Defines all privilege levels with their associated number values
     */
    public static final HashMap<String, Integer> privilegeTypes = new HashMap<String, Integer>() {{
        put("System Admin", 1);
        put("Building Admin", 2);
        put("Normal User", 3);
    }};

    /**
     * Defines a list of all buildings
     */
    public static final String[] buildings = {"Agronomy", "Armory", "Atanasoff",
            "Beardshear", "Bessey", "Beyer", "Black Engineering",
            "Carver", "Catt", "Coover", "Curtiss",
            "Durham",
            "East", "Elings",
            "Food Sciences", "Friley Residence",
            "Gerdin", "Gilman",
            "Hach", "Hamilton", "Heady", "Helser Residence", "Hoover", "Horticulture", "Howe",
            "Jischke",
            "Kildee",
            "Lagomarcino", "LeBaron",
            "Parks Library",
            "MacKay", "Marston", "Memorial Union", "Morrill", "Music",
            "Olsen",
            "Pearson", "Physics",
            "Ross",
            "Scheman", "Science", "Seed Science", "Snedecor", "Spedding", "State Gym", "Sukup", "Sweeney",
            "Town Engineering", "Troxel",
            "Wilhelm"};

    // Define all rooms
    public static final String[] rooms = {"101", "102"};
}
