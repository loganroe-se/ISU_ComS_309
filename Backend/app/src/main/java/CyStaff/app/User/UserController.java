package CyStaff.app.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "UserController", description = "REST APIs related to User Entity.")
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "Gets list of all Users in the database.")
    @ApiResponse(code = 200, message = "Returns list of all Users.")
    @GetMapping(path = "/users")
    List<User> getAllUsers() { return userRepository.findAll(); }

    @ApiOperation(value = "Searches for a list of Users in the database based on " +
                          "firstName and/or lastName provided.")
    @ApiResponse(code = 200, message = "Returns list of Users which matches description.")
    @GetMapping(path = "/searchUser")
    List <User> findOneUsers(
            @ApiParam(name = "name", value = "First name and/or last name of user")
            @RequestParam (value = "name") String name)
    {
        if(name.isEmpty())
        {
            return null;
        }

        if(name.contains(" "))
        {
            String [] names = name.split(" ");

            String name1 = names[0];
            String name2 = names[1];

            return userRepository.searchUserBoth(name1, name2);
        }
        else
        {
            return userRepository.searchUserSingle(name);
        }

    }

    @ApiOperation(value = "Returns a user's full name given an userid")
    @ApiResponse(code = 200, message = "Returns User's name if valid")
    @GetMapping("/getUsersName")
    Map<String, String> getUsersName(@RequestBody User user){
        Map<String, String> getUserStatus = new HashMap<>();

        if(user == null){
            return null;
        }

        User tempUser = userRepository.findByuserid(user.getUserid());

        getUserStatus.put("Name", tempUser.getFirstName() + tempUser.getLastName());
        return getUserStatus;
    }

    @ApiOperation(value = "Verifies User login information based on the " +
                          "email and password provided.")
    @ApiResponse(code = 200, message = "Returns User information if valid.")
    @PostMapping(path = "/login")
    User verifyLogin(
            @ApiParam(name = "loginInfo", value = "Email and password of User")
            @RequestBody Map<String, String> loginInfo)
    {
        String email, password;
        if(loginInfo.containsKey("email") && loginInfo.containsKey("password")) {
            email = loginInfo.get("email");
            password = loginInfo.get("password");
        }else{
            return null;
        }
        System.out.println(email + password);
        User temp_user = userRepository.findByemail(email);

        if(temp_user == null || !(temp_user.getPassword().equals(password)))
        {
            return null;
        }

        return temp_user;
    }

    @ApiOperation(value = "Creates a new User entry in the database.")
    @ApiResponse(code = 200, message = "New User entry created.")
    @PostMapping(path = "/users")
    Map<String, String> createUser(
            @ApiParam(name = "user", value = "Json request body for User")
            @RequestBody User user) {
        Map<String, String> createStatus = new HashMap<>();

        if (user == null) {
            createStatus.put("status", "failure");
            return createStatus;
        }

        if (userRepository.findByemail(user.getEmail()) != null) {
            createStatus.put("status", "Invalid Email");
            return createStatus;

        }

        userRepository.save(user);

        createStatus.put("status", "Success");
        return createStatus;
    }

    @ApiOperation(value = "Deletes a User entry from the database based on userid.")
    @ApiResponse(code = 200, message = "User entry deleted.")
    @DeleteMapping(path = "/users")
    Map<String, String> deleteUser (
            @ApiParam(name = "userid", value = "User ID of User")
            @RequestParam(value = "userid") int id)
    {
        Map<String, String> createStatus = new HashMap<>();
        if (userRepository.findByuserid(id) == null) {
            createStatus.put("status", "failure");
            return createStatus;
        }

        userRepository.deleteByuserid(id);

        createStatus.put("status", "Success");
        return createStatus;
    }

    @ApiOperation(value = "Updates User information in the database based on the new" +
                          "User information provided.")
    @ApiResponse(code = 200, message = "Returns the updated User.")
    @PutMapping(path = "/users")
    public ResponseEntity<?> editUser (
            @ApiParam(name = "user", value = "Json request body for User")
            @RequestBody User user)
    {
        User temp = userRepository.findByuserid(user.getUserid());

        Map<String, String> tempResponse = new HashMap<>();
        if (temp == null)
        {
            tempResponse.put("status", "failure");
            return ResponseEntity.status(HttpStatus.OK).body(tempResponse);
        }

        if(temp.getEmail() != null && user.getEmail() != null && !user.getEmail().equals(temp.getEmail()) && userRepository.findByemail(user.getEmail()) != null){
            tempResponse.put("status", "Invalid Email");
            return ResponseEntity.status(HttpStatus.OK).body(tempResponse);
        }

        temp.setActive( (user.getActive() == null) ? temp.getActive() : user.getActive());
        temp.setBirthday((user.getBirthday() == null) ? temp.getBirthday() : user.getBirthday());
        temp.setBuilding((user.getBuilding() == null) ? temp.getBuilding() : user.getBuilding());
        temp.setEmail((user.getEmail() == null) ? temp.getEmail() : user.getEmail());
        temp.setPassword((user.getPassword() == null) ? temp.getPassword() : user.getPassword());
        temp.setFirstName((user.getFirstName() == null) ? temp.getFirstName() : user.getFirstName());
        temp.setLastName((user.getLastName() == null) ? temp.getLastName() : user.getLastName());
        temp.setPhone((user.getPhone() == null) ? temp.getPhone() : user.getPhone());
        temp.setLastLogin((user.getLastLogin() == null) ? temp.getLastLogin() : user.getLastLogin());
        temp.setPrivilege((user.getPrivilege() == null) ? temp.getPrivilege() : user.getPrivilege());
        temp.setSettings((user.getSettings() == null) ? temp.getSettings() : user.getSettings());

        userRepository.save(temp);

        tempResponse.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(tempResponse);
    }
}
