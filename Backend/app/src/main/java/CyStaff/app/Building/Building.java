package CyStaff.app.Building;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Buildings" +
        " such as building id, name of the building")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int buildingid;
    private String name;

    public int getBuildingid()
    {
        return buildingid;
    }
    public void setBuildingid(int id){
        this.buildingid = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}


