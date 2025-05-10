package CyStaff.app.Meeting;

import CyStaff.app.Building.Building;
import CyStaff.app.User.User;
import CyStaff.app.User.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "MeetingController", description = "REST APIs related to Meeting Entity")
@RestController
public class MeetingController {
    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "Gets list of all Meetings in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Meetings.")
    @GetMapping(path="/meeting")
    List<Meeting> getAllMeetings()
    {
        return meetingRepository.findAll();
    }

    @ApiOperation(value = "Gets list of all Meetings based on userid in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Meetings based on userid.")
    @GetMapping(path="/searchMeeting")
    List<Meeting> getMeetingsByID(
            @ApiParam(name = "userid", value = "First name and/or last name of user")
            @RequestParam(value = "userid") int userid)
    {
        User user = userRepository.findByuserid(userid);

        String stringid = String.valueOf(userid);

        stringid = "%&" + stringid + ":%";

        List<Meeting> meetings = new ArrayList<>();

        meetings = meetingRepository.searchMeetings(stringid);
        meetings.addAll(meetingRepository.findAllByuser(user));

        return meetings;
    }

    @ApiOperation(value = "Creates a new Meeting entry in the database.")
    @ApiResponse(code = 200, message = "New Meeting entry created.")
    @PostMapping(path="/meeting")
    Map<String, String> createMeeting(
            @ApiParam(name = "meeting", value = "Json request body for meeting")
            @RequestBody Meeting meeting)
    {
        Map<String, String> createStatus = new HashMap<>();

        if(meeting == null)
        {
            createStatus.put("status", "failure");
            return createStatus;
        }

        meetingRepository.save(meeting);

        createStatus.put("status", "Success");
        return createStatus;
    }

    @ApiOperation(value = "Deletes a Meeting entry from the database based on meetingid.")
    @ApiResponse(code = 200, message = "Meeting entry deleted.")
    @DeleteMapping(path = "/meeting")
    Map<String, String> deleteMeeting (
            @ApiParam(name = "meetingid", value = "ID of meeting")
            @RequestParam(value = "meetingid") int id)
    {
        Map<String, String> createStatus = new HashMap<>();
        if (meetingRepository.findBymeetingid(id) == null) {
            createStatus.put("status", "Failure");
            return createStatus;
        }

        meetingRepository.deleteBymeetingid(id);

        createStatus.put("status", "Success");
        return createStatus;
    }

}
