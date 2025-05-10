package CyStaff.app.Announcement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findByannouncementid(int id);

    @Transactional
    void deleteByannouncementid(int id);
}
