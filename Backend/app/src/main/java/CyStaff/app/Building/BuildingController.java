package CyStaff.app.Building;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(value = "BuildingController", description = "REST APIs related to Building Entity")
@RestController
public class BuildingController {
    @Autowired
    BuildingRepository buildingRepository;

    @ApiOperation(value = "Gets list of all Buildings in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Buildings.")
    @GetMapping(path="/building")
    List<Building> getAllBuildings()
    {
        return buildingRepository.findAll();
    }

    @ApiOperation(value = "Creates a new Building entry in the database.")
    @ApiResponse(code = 200, message = "New Building entry created.")
    @PostMapping(path="/building")
    String createBuilding(
            @ApiParam(name = "building", value = "Json request body for building")
            @RequestBody Building building){
        if(building == null)
        {
            return "Null object";
        }
        buildingRepository.save(building);

        return "Success";
    }

}
