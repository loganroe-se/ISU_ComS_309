package CyStaff.app.Privilege;

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

@Api(value = "Privilege Controller", description = "REST APIs related to Privilege Entity")
@RestController
public class PrivilegeController {
    @Autowired
    PrivilegeRepository privilegeRepository;

    @ApiOperation(value = "Gets list of all Privileges in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Privileges.")
    @GetMapping(path="/privilege")
    List<Privilege> getAllPrivileges(){
        return privilegeRepository.findAll();
    }

    @ApiOperation(value = "Creates a new Privilege entry in the database.")
    @ApiResponse(code = 200, message = "New Privilege entry created.")
    @PostMapping(path="/privilege")
    String createPrivilege(
            @ApiParam(name = "privilege", value = "Json request body for Privilege")
            @RequestBody Privilege privilege){
        if(privilege == null){
            return "Null object";
        }
        privilegeRepository.save(privilege);
        return "success";
    }

}
