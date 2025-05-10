package com.example.cystaff_frontend.message;

/**
 * A Class that defines a user message with its attribute fields.
 */
public class Message {
    // Variables for all message information
    private final String message, date;
    private final MessageUser sender;

    /**
     * Constructor for the message item
     *
     * @param message - The message being sent/received
     * @param date - The date the message was sent
     * @param sender - The user who sent the message
     */
    public Message(String message, String date, MessageUser sender) {
        this.message = message;
        this.date = date;
        this.sender = sender;
    }

    /**
     * Getter for the message sent/received
     *
     * @return - The message sent/received
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the date the message was sent/received
     *
     * @return - The date the message was sent/received
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for the user who sent the message
     *
     * @return - The user who sent the message
     */
    public MessageUser getSender() {
        return sender;
    }
}
