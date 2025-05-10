package CyStaff.app.HourType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface HourTypeRepository extends JpaRepository<HourType, Long> {
    HourType findByhourtypeid(int id);
    @Transactional
    void deleteByhourtypeid(int id);
}

