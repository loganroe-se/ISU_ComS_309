package CyStaff.app.Meeting;

import CyStaff.app.Hour.Hour;
import CyStaff.app.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Meeting findBymeetingid(int id);

    List<Meeting> findAllByuser(User user);

    @Query("SELECT m FROM Meeting m WHERE m.recipients LIKE :userid")
    List<Meeting> searchMeetings(String userid);

    @Transactional
    void deleteBymeetingid(int id);
}

