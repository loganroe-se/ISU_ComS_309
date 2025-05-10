package coms309.people;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class EmployeeController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Employee> employeeList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/employee")
    public @ResponseBody HashMap<String,Employee> getAllPersons() {
        return employeeList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // in this case because of @ResponseBody
    // Note: To CREATE we use POST method

    @PostMapping("/employee")
    public @ResponseBody String createPerson(@RequestBody Employee employee) {
        System.out.println(employee);
        employeeList.put(employee.getFirstName(), employee);
        return "New employee "+ employee.getFirstName() + " Saved";
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // in this case because of @ResponseBody
    // Note: To READ we use GET method
    @GetMapping("/employee/{firstName}")
    public @ResponseBody Employee getPerson(@PathVariable String firstName) {
        Employee e = employeeList.get(firstName);
        return e;
    }

    //look up employee info by their last name.
    @GetMapping("/employee/lastname/{lastName}")
    public @ResponseBody Employee getFirstName(@PathVariable String lastName) {

        ArrayList<Employee> arrayList = new ArrayList<>(employeeList.values());
        Employee e = null;

        for (Employee employee : arrayList) {
            if ((employee.getLastName()).equals(lastName)) {
                e = employee;
            }
        }

        return e;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // in this case because of @ResponseBody
    // Note: To UPDATE we use PUT method
    @PutMapping("/employee/{firstName}")
    public @ResponseBody Employee updatePerson(@PathVariable String firstName, @RequestBody Employee e) {
        employeeList.replace(firstName, e);
        return employeeList.get(firstName);
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // in this case because of @ResponseBody
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/employee/{firstName}")
    public @ResponseBody HashMap<String, Employee> deletePerson(@PathVariable String firstName) {
        employeeList.remove(firstName);
        return employeeList;
    }

    //Deletes everything from the employeeList hashmap.
    @DeleteMapping("/employee/deleteAll")
    public @ResponseBody HashMap<String, Employee> deleteAll()
    {
        employeeList.clear();
        return employeeList;
    }

}

