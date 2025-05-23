package coms309.people;

import coms309.ExceptionController;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class PeopleController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Person> peopleList = new  HashMap<>();
    ExceptionController exceptions;

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public @ResponseBody HashMap<String,Person> getAllPersons() {
        return peopleList;
    }

    @GetMapping("/peoplel")
    public @ResponseBody HashMap<String, Person> getPersonsbyLastName(@RequestParam(value="lastname", defaultValue = "Doe") String lastname){
        HashMap<String, Person> tempList = new  HashMap<>();
        for(HashMap.Entry<String, Person> element : peopleList.entrySet()){
            System.out.println(element.getValue().getLastName());
            if(lastname.equals(element.getValue().getLastName())){
                tempList.put(element.getValue().getFirstName(), element.getValue());
            }
        }
        return tempList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // in this case because of @ResponseBody
    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public @ResponseBody String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        return "New person "+ person.getFirstName() + " Saved";
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // in this case because of @ResponseBody
    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public @ResponseBody Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // in this case because of @ResponseBody
    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public @ResponseBody Person updatePersonFirstName(@PathVariable String firstName, @RequestParam(value = "address", defaultValue = "") String address, @RequestBody Person p) {
        if(!address.equals("")) {
            if (peopleList.containsKey(firstName)) {
                peopleList.get(firstName).setAddress(address);
            } else {
                exceptions.triggerException();
            }
            return peopleList.get(firstName);
        }

        if(null != p) {
            peopleList.remove(firstName);
        }else{
            exceptions.triggerException();
        }
        peopleList.put(p.getFirstName(), p);
        return peopleList.get(p.getFirstName());
    }

    GetMapping(path = "/login")


    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // in this case because of @ResponseBody
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public @ResponseBody HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }
}

