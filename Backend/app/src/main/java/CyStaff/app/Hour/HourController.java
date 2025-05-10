package CyStaff.app.Hour;

import CyStaff.app.Status.Status;
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


@Api(value = "HourController", description = "REST APIs related to Hour Entity")
@RestController
public class HourController {

    @Autowired
    HourRepository hourRepository;

    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "Gets list of all Work Hours in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Work Hours.")
    @GetMapping(path = "/hour")
    List<Hour> getAllHours() { return hourRepository.findAll(); }

    @ApiOperation(value = "Gets list of all Work Hours with specific Status in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Work Hours based on Status.")
    @GetMapping(path = "/hour/status")
    List<Hour> getAllHoursByStatus(
            @ApiParam(name = "statusid", value = "Status ID of the Work Hour")
            @RequestParam(value = "statusid") int id)
    {

        return hourRepository.findAllBystatus_statusid(id);
    }

    @ApiOperation(value = "Gets list of all Work Hours with specific User in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Work Hours based on User.")
    @GetMapping(path = "/hour/user")
    List<Hour> getAllHoursByUser(
            @ApiParam(name = "userid", value = "User ID of the Work Hour")
            @RequestParam(value = "userid") int id)
    {
        return hourRepository.findAllByuser_userid(id);
    }

    @ApiOperation(value = "Gets list of all Work Hours with specific User and Date in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Work Hours based on User and Date.")
    @GetMapping(path = "/hour/search")
    List<Hour> getAllHoursByUserandDate(
            @RequestParam(value = "userid") int userid,
            @RequestParam String date)
    {
        List<Hour> hours = hourRepository.findAllByuser_userid(userid);
        List<Hour> hoursWithDate = new ArrayList<>();

        for(int i = 0; i < hours.size(); i++)
        {
            if (hours.get(i).getDate().equals(date))
            {
                hoursWithDate.add(hours.get(i));
            }
        }

        return hoursWithDate;
    }


    @ApiOperation(value = "Creates a new Work Hour entry in the database.")
    @ApiResponse(code = 200, message = "New Hour entry created.")
    @PostMapping(path="/hour")
    Map<String,String> createHour(
            @ApiParam(name = "hour", value = "Json request body for Hour")
            @RequestBody Hour hour)
    {
        Map <String,String> response = new HashMap<>();

        if(hour == null)
        {
            response.put("Status", "Null Object");
            return response;
        }
        hourRepository.save(hour);
        response.put("Status", "Success");
        return response;
    }
}

