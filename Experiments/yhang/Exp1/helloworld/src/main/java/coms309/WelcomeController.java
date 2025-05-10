package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {


    // Some sketchy looking login page
    @GetMapping("/")
    public String welcome() {

        String header = "<h1> Welcome to <font color = \"red\"> Iowa State University</font>!</h1>";

        String isuId = "Please enter your <font color = \"red\"> ISU</font> ID." +
                "<br><br> <label for = \"pwd\"> <font color = \"red\">ISU </font> ID: </label>" +
                "<input type = \"password\" id =\"pwd\" name=\"pwd\" required>";

        String button = "<br><br><pre>                                  " +
                "<button type = \"button\"> Enter </button>";

        return header + isuId + button;
    }

    // Page prints out something different based on specific id
    @GetMapping("/{id}")
    public String welcome(@PathVariable String id) {

        if(id.equals("762345646"))
        {
            return "<h1>Welcome Back Fellow <font color = \"gold\"> Cyclone!</font></h1>";
        }

        return "<h1>Hello Stranger.</h1>";
    }
}
