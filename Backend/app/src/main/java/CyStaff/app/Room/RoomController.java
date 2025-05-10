package CyStaff.app.Room;

import CyStaff.app.Building.Building;
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

@Api(value = "RoomController", description = "REST APIs related to Room Entity")
@RestController
public class RoomController {
    @Autowired
    RoomRepository roomRepository;

    @ApiOperation(value = "Gets list of all Rooms in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Rooms.")
    @GetMapping(path="/room")
    List<Room> getAllRooms()
    {
        return roomRepository.findAll();
    }

    @ApiOperation(value = "Gets the list of rooms based on building.")
    @ApiResponse(code = 200, message = "Returns list of rooms based on building.")
    @GetMapping(path="/room/building")
    List<Room> getRooms(
            @ApiParam(name = "building", value = "Json request body for building")
            @RequestBody Building building)
    {
        return roomRepository.searchBybuilding(building);
    }

    @ApiOperation(value = "Creates a new Room entry in the database.")
    @ApiResponse(code = 200, message = "New Room entry created.")
    @PostMapping(path="/room")
    String createBuilding(
            @ApiParam(name = "room", value = "Json request body for room")
            @RequestBody Room room)
    {
        if(room == null)
        {
            return "Null object";
        }
        roomRepository.save(room);

        return "Success";
    }

}
