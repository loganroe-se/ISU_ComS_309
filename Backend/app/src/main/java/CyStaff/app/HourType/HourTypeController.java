package CyStaff.app.HourType;

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

@Api(value = "HourTypeController", description = "REST APIs related to HourType Entity")
@RestController
public class HourTypeController {
    @Autowired
    HourTypeRepository hourTypeRepository;

    @ApiOperation(value = "Gets list of all HourTypes in the database.")
    @ApiResponse(code = 200, message = "Returns list of all HourTypes.")
    @GetMapping(path="/hourType")
    List<HourType> getAllHourTypes()
    {
        return hourTypeRepository.findAll();
    }

    @ApiOperation(value = "Creates a new HourType entry in the database.")
    @ApiResponse(code = 200, message = "New HourType entry created.")
    @PostMapping(path="/hourType")
    String createHourTypes(
            @ApiParam(name = "hourType", value = "Json request body for HourType")
            @RequestBody HourType hourType){
        if(hourType == null)
        {
            return "Null object";
        }
        hourTypeRepository.save(hourType);

        return "Success";
    }

}
