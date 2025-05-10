package CyStaff.app.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{

    User findByuserid(int id);

    User findByemail(String email);

    User findByfirstName(String firstName);

    List <User> findAllByfirstName(String firstName);

    List <User> findAllBylastName(String lastName);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% and u.lastName LIKE %?2%" +
                                   "or u.firstName LIKE %?2% and u.lastName LIKE %?1%")
    List<User> searchUserBoth(String name1, String name2);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1%" +
                                    "or u.lastName LIKE %?1%" +
                                    "or u.email LIKE %?1%")
    List<User> searchUserSingle(String name);

    @Transactional
    void deleteByuserid(int id);
}
