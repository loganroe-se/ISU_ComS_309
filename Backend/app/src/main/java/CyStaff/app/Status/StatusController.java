package CyStaff.app.Status;

import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "StatusController", description = "REST APIs related to Status Entity")
@RestController
public class StatusController {
    @Autowired
    StatusRepository statusRepository;

    @ApiOperation(value = "Gets list of all status in the database.")
    @ApiResponse(code = 200, message = "Returns list of all status.")
    @GetMapping(path="/status")
    List<Status> getAllStatus()
    {
        return statusRepository.findAll();
    }

    @ApiOperation(value = "Creates a new status entry in the database.")
    @ApiResponse(code = 200, message = "New status entry created.")
    @PostMapping(path="/status")
    String createStatus(
            @ApiParam(name = "status", value = "Json request body for Status")
            @RequestBody Status status){
        if(status == null)
        {
            return "Null object";
        }
        statusRepository.save(status);

        return "Success";
    }

}