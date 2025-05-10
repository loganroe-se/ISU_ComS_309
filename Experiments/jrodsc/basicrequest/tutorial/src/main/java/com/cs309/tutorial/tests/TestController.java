package com.cs309.tutorial.tests;

import com.cs309.tutorial.TutorialApplication;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

	@GetMapping("/")
	public String welcome() {
		return "Username: <input id = \"usernameTextBox\" size = \"6/\"><br>" + "Password: <input id = \"passwordTextBox\" size = \"6/\">";
	}

	@GetMapping ("/error")
	public String showError(){
		return "Error";
	}

	@GetMapping("/getTest")
	public String getTest(@RequestParam(value = "username", defaultValue = "World") String message) {
		return String.format("Hello, %s! You sent a get request with a parameter!", message);
	}
	
	@PostMapping("/postTest1")
	public String postTest1(@RequestParam(value = "username", defaultValue = "World") String message) {
		TestData newUser = new TestData(message);

		TutorialApplication.Usernames.add(newUser);

		for (TestData user : TutorialApplication.Usernames
		) {
			System.out.println(user.getMessage());
		}
		return String.format("Hello, %s! You sent a post request with a parameter!", message);
	}

	@DeleteMapping("/deleteTest")
	public void deleteTest() {
		//TODO
	}
	
	@PutMapping("/putTest")
	public void putTest() {
		//TODO
	}
}
