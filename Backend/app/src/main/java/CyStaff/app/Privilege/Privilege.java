package CyStaff.app.Privilege;

import CyStaff.app.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "Creates a table in the database that contains details of Privileges" +
        " such as privilege id, and the name of the privilege type.")
@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int privilegeid;
    private String type;

    public Privilege(int id, String type){
        this.privilegeid = id;
        this.type = type;
    }

    public Privilege(){
    }

    public int getPrivilegeid() {
        return privilegeid;
    }
    public void setPrivilegeid(int id){
        this.privilegeid = id;
    }
    public String getType(){
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
