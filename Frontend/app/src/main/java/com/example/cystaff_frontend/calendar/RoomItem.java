package com.example.cystaff_frontend.calendar;

/**
 * Holds the building, the room, and the roomID for all rooms
 */
public class RoomItem {
    // Variables for all RoomItem information
    private final int roomID, room;
    private final String building;

    /**
     * Constructor for the room item
     * @param roomID - The ID for the given building/room combination
     * @param building - The building
     * @param room - The room
     */
    public RoomItem(int roomID, String building, int room) {
        this.roomID = roomID;
        this.building = building;
        this.room = room;
    }

    /**
     * Getter for the room ID
     * @return - The room ID as an int
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Getter for the building
     * @return - The building name
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Getter for the room
     * @return - The room number
     */
    public int getRoom() {
        return room;
    }
}
