package CyStaff.app.Room;

import CyStaff.app.Building.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> searchBybuilding(Building building);

    @Transactional
    void deleteByroomid(int id);
}
