package CyStaff.app.Privilege;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByprivilegeid(int id);

    @Transactional
    void deleteByprivilegeid(int id);
}
