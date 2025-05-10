package CyStaff.app.Messaging;

import CyStaff.app.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>{

    @Query("SELECT m FROM Message m WHERE m.userName = ?1 and m.destUserName = ?2" +
                                      "or m.userName = ?2 and m.destUserName = ?1")
    List<Message> searchUserBoth(String userName, String destUserName);

    List <Message> findAllByuserName(String userName);
}

