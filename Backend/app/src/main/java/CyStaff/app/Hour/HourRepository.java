package CyStaff.app.Hour;

import CyStaff.app.Status.Status;
import CyStaff.app.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HourRepository extends JpaRepository<Hour, Long> {
    Hour findByhourid(int id);

    List<Hour> findAllBystatus_statusid(int stautsid);

    List<Hour> findAllByuser_userid(int userid);

    List<Hour> findAllByuser_useridAndDate(int userid, String date);

    @Query("SELECT h FROM Hour h WHERE h.user LIKE ?1" +
                                  "AND h.date = '?2'")
    List<Hour> searchHour(User user, String date);

    @Transactional
    void deleteByhourid(int id);
}

