package CyStaff.app.Announcement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "AnnouncementController", description = "REST APIs related to Announcement Entity")
@RestController
public class AnnouncementController {

    @Autowired
    AnnouncementRepository announcementRepository;

    @ApiOperation(value = "Gets list of all Announcements in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Announcements.")
    @GetMapping(path = "/announcement")
    List<Announcement> getAllAnnouncements() { return announcementRepository.findAll(); }

    @ApiOperation(value = "Creates a new Announcement entry in the database.")
    @ApiResponse(code = 200, message = "New Announcement entry created.")
    @PostMapping(path="/announcement")
    public ResponseEntity<?> createAnnouncement(
            @ApiParam(name = "announcement", value = "Json request body for Announcement")
            @RequestBody Announcement announcement)
    {
        Map<String, String> tempResponse = new HashMap<>();
        if(announcement == null)
        {
            tempResponse.put("status", "failure");
            return ResponseEntity.status(HttpStatus.OK).body(tempResponse);
        }
        announcementRepository.save(announcement);
        tempResponse.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(tempResponse);
    }
}
