package com.cs309.tutorial.tests;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

@RestController
public class TestController {

	ArrayList<Animal> animalList = new ArrayList<>();

	// Get a single animal based on their name
	@GetMapping("/getAnimal")
	public Animal getTest(@RequestParam(value = "name") String name) {

		return findAnimalName(name);
	}

	// Search for a specific animal based on category and data
	@GetMapping("/getSpecific")
	public Animal getSpecific(@RequestParam(value = "category") String category,
							  @RequestParam(value = "data") String data)
	{
		if(category.equals("name"))
		{
			return findAnimalName(data);
		}
		else if(category.equals("species"))
		{
			return findAnimalSpecies(data);
		}
		else if (category.equals("age"))
		{
			int age = Integer.parseInt(data);
			return findAnimalAge(age);
		}
		else if (category.equals("attack"))
		{
			return findAnimalAttack(data);
		}

		return null;
	}

	// Get all the animals (returned in a list)
	@GetMapping("/getAll")
	public ArrayList getAll()
	{
		return animalList;
	}

	// Creates a new animal in the list
	@PostMapping("/postAnimal")
	public String postTest2(@RequestBody Animal animal) {

		animalList.add(animal);

		return String.format("Welcome to the jungle, %s!", animal.getName());
	}


	// Deletes an animal on the list based on their name
	@DeleteMapping("/deleteAnimal")
	public ArrayList deleteTest(@RequestParam(value = "name") String name) {

		animalList.remove(findAnimalName(name));

		return animalList;
	}

	// Deletes all the animals on the list.
	@DeleteMapping("/deleteAll")
	public ArrayList deleteAll()
	{
		animalList.clear();

		return animalList;
	}

	// Updates the data of animals in the list based on their name
	@PutMapping("/putAnimal")
	public ArrayList putTest(@RequestParam(value = "name") String name, @RequestBody Animal animal) {

		animalList.remove(findAnimalName(name));
		animalList.add(animal);

		return animalList;
	}

	// Private helper method to help find animals using their name.
	private Animal findAnimalName(String name)
	{
		for(int i = 0; i < animalList.size(); i++)
		{
			if(animalList.get(i).getName().equals(name))
			{
				return animalList.get(i);
			}
		}
		return null;
	}

	// Private helper method to help find animals using their species.
	private Animal findAnimalSpecies(String species)
	{
		for(int i = 0; i < animalList.size(); i++)
		{
			if(animalList.get(i).getSpecies().equals(species))
			{
				return animalList.get(i);
			}
		}
		return null;
	}

	// Private helper method to help find animals using their age.
	private Animal findAnimalAge(int age)
	{
		for(int i = 0; i < animalList.size(); i++)
		{
			if(animalList.get(i).getAge() == (age))
			{
				return animalList.get(i);
			}
		}
		return null;
	}

	// Private helper method to help find animals using their attack.
	private Animal findAnimalAttack(String attack)
	{
		for(int i = 0; i < animalList.size(); i++)
		{
			if(animalList.get(i).getAttack().equals(attack))
			{
				return animalList.get(i);
			}
		}
		return null;
	}
}
