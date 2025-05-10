package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Username: <input id = \"usernameTextBox\" size = \"6/\"><br>" + "Password: <input id = \"passwordTextBox\" size = \"6/\">";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        String message = "Hello " + name + " what would you like to do?";
        String options = "<ul>" +
                "<li>>Track working hours</li>" +
                "<li>Make an announcement</li>" +
                "<li>Schedule meetings</li>" +
                "<li>Tasks</li>" +
                "<li>Documents</li>" +
                "<li>Directory</li>" +
                "</ul>";

        return message + options;
    }
}
