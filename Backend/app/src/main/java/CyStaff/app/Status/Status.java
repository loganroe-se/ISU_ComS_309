package CyStaff.app.Status;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Status" +
        " such as status id, and name of the status like approved and rejected etc.")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int statusid;
    private String name;


    public int getStatusid() { return statusid; }
    public void setStatusid(int id){
        this.statusid = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
}
