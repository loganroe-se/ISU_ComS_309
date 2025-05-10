package CyStaff.app.Messaging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}/{destusername}")  // this is Websocket url
public class MessagingSocket {

    // cannot autowire static directly (instead we do it by the below
    // method
    private static MessageRepository msgRepo;

    /*
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     */
    @Autowired
    public void setMessageRepository(MessageRepository repo)
    {
        msgRepo = repo;  // we are setting the static variable
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(MessagingSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username,
                                        @PathParam("destusername") String destusername)
                                        throws IOException
    {
        logger.info("Entered into Open");

        // store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        //Send chat history to the newly connected user
        sendMessageToParticularUser(username, getChatHistory(username, destusername));

    }


    @OnMessage
    public void onMessage(Session session, String message,
                          @PathParam("destusername") String destusername) throws IOException {

        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String formatDate = dateFormat.format(date);

//        sendMessageToParticularUser(destusername, username + ": " + message);
//        sendMessageToParticularUser(username, username + ": " + message);

        sendMessageToParticularUser(destusername, username + "!&:"+ formatDate +": " + message );
        sendMessageToParticularUser(username, username + "!&:"+ formatDate +": " + message );

        // Saving chat history to repository
        msgRepo.save(Message.builder()
                .userName(username)
                .destUserName(destusername)
                .content(message)
                .sent(date)
                .build());

//        // Direct message to a user using the format "@username <message>"
//        if (message.startsWith("@")) {
//            String destUsername = message.split(" ")[0].substring(1);
//
//            // send the message to the sender and receiver
//            sendMessageToParticularUser(destUsername, "[DM] " + username + ": " + message);
//            sendMessageToParticularUser(username, "[DM] " + username + ": " + message);
//
//        }
//        else { // broadcast
//            broadcast(username + ": " + message);
//        }
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        // remove the user connection information
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToParticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }


    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            }
            catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }

        });

    }


    // Gets the Chat history from the repository
    private String getChatHistory(String username, String destusername) {
        List<Message> messages = msgRepo.searchUserBoth(username, destusername);

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if(messages != null && messages.size() != 0) {
            for (Message message : messages) {
                String date = dateFormat.format(message.getSent());
                sb.append(message.getUserName() + "!&:"+ date +": " + message.getContent() + "%!:");
            }
        }
        return sb.toString();
    }

} // end of Class

