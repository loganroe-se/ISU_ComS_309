package CyStaff.app.Settings;

import CyStaff.app.User.User;
import CyStaff.app.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    @Autowired
    SettingsRepository settingsRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    Settings getSettingsById(@RequestBody Settings settings){
        Settings tempSettings = settingsRepository.findBysettingsid(settings.getSettingsid());

        return tempSettings;
    }
    @PostMapping("")
    Map<String, String> createSettings(@RequestBody Settings settings){
        Map<String, String> createStatus = new HashMap<String, String>();

        if(settings == null){
            createStatus.put("status", "failure");

            return createStatus;
        }


        if(settings.getUser() != null && settings.getUser().getUserid() > 0) {
            User tempUser = userRepository.findByuserid(settings.getUser().getUserid());
            tempUser.setSettings(settingsRepository.save(settings));
            userRepository.save(tempUser);
        }else{
            settingsRepository.save(settings);
        }


        createStatus.put("status", "success");
        createStatus.put("settingsid", String.valueOf(settings.getSettingsid()));

        return createStatus;
    }

    @PutMapping("")
    Map<String, String> changeSettings(@RequestBody Settings settings){
        Settings tempSettings = settingsRepository.findBysettingsid(settings.getSettingsid());
        Map<String, String> createStatus = new HashMap<String, String>();

        if(tempSettings == null){
            createStatus.put("status", "failure");

            return createStatus;
        }

        if(settings.getCalendarFormat() != null)
            tempSettings.setCalendarFormat(settings.getCalendarFormat());
        if(settings.getPreferredName() != null)
            tempSettings.setPreferredName(settings.getPreferredName());
        if(settings.getWeekDisplay() != null)
            tempSettings.setWeekDisplay(settings.getWeekDisplay());
        if(settings.getEventType() != null)
            tempSettings.setEventType(settings.getEventType());
        if(settings.getLocationType() != null)
            tempSettings.setLocationType(settings.getLocationType());
        if(settings.getNumberOfAnnouncements() > tempSettings.getNumberOfAnnouncements())
            tempSettings.setNumberOfAnnouncements(settings.getNumberOfAnnouncements());
        if(settings.getSortDirectoryBy() != null)
            tempSettings.setSortDirectoryBy(settings.getSortDirectoryBy());
//        if(settings.getUser() != null) {
//            tempSettings.setUser(settings.getUser());
//        }

        settingsRepository.save(tempSettings);
        createStatus.put("status", "success");

        return createStatus;
    }

    @DeleteMapping("")
    Map<String, String> deleteSettings(@RequestBody Settings settings){
        Settings tempSettings = settingsRepository.findBysettingsid(settings.getSettingsid());

        Map<String, String> createStatus = new HashMap<String, String>();

        if(tempSettings == null){
            createStatus.put("status", "failure");

            return createStatus;
        }
        if(tempSettings.getUser() != null){
            User tempUser = userRepository.findByuserid(tempSettings.getUser().getUserid());
            tempUser.setSettings(null);
            userRepository.save(tempUser);
        }

        settingsRepository.deleteBysettingsid(tempSettings.getSettingsid());

        createStatus.put("status", "success");

        return createStatus;
    }

}
