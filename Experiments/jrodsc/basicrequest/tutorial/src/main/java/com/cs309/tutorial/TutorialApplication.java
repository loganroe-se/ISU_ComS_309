package com.cs309.tutorial;

import com.cs309.tutorial.tests.TestData;
import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class TutorialApplication {

	public static ArrayList<TestData> Usernames;

	public static void main(String[] args) {
		Usernames = new ArrayList<>();
		SpringApplication.run(TutorialApplication.class, args);
	}

}
