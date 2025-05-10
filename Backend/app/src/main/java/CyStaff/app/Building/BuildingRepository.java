package CyStaff.app.Building;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Transactional
    void deleteBybuildingid(int id);
}

