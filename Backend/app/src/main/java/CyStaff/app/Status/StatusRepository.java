package CyStaff.app.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findBystatusid(int id);
    @Transactional
    void deleteBystatusid(int id);
}
