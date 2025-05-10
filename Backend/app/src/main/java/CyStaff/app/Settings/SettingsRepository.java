package CyStaff.app.Settings;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface SettingsRepository extends JpaRepository<Settings,Long> {
    Settings findBysettingsid(Long id);

    @Transactional
    void deleteBysettingsid(Long id);
}
