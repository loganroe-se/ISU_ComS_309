package coms309.people;


/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class Employee {

    private String firstName;

    private String lastName;

    private String address;

    private String telephone;

    private String socialSecurityNumber;

    private String position;

    private String salary;

    public Employee(){
        
    }

    public Employee(String firstName, String lastName, String address, String telephone
    ,String socialSecurityNumber, String position, String salary){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.socialSecurityNumber = socialSecurityNumber;
        this.position = position;
        this.salary = salary;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSocialSecurityNumber() {return this.socialSecurityNumber;}

    public void setSocialSecurityNumber() {this.socialSecurityNumber = socialSecurityNumber;}

    public String getPosition(){return this.position;}

    public void setPosition(){this.position = position;}

    public String getSalary(){return this.salary;}

    public void setSalary(){this.salary = salary;}

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + address + " "
               + telephone + " "
               + socialSecurityNumber + " "
               + position + " "
               + salary;
    }
}
