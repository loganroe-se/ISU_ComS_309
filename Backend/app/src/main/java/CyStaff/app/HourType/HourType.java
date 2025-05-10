package CyStaff.app.HourType;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Hour Types" +
        " such as hour type id, and name of the hour type.")
@Entity
public class HourType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hourtypeid;
    private String type;

    public HourType(int id, String type){
        this.hourtypeid = id;
        this.type = type;
    }

    public HourType(){
    }

    public int getHourtypeid() {
        return hourtypeid;
    }
    public void setHourtypeid(int id){
        this.hourtypeid = id;
    }
    public String getType(){
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}

