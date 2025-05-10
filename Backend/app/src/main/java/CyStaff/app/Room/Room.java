package CyStaff.app.Room;

import CyStaff.app.Building.Building;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Rooms" +
        " such as room id, building id of the room, and name of the room")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomid;

    private int roomNumber;

    @ManyToOne
    @JoinColumn(name = "buildingid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private Building building;

    public int getRoomid()
    {
        return roomid;
    }
    public void setRoomid(int id){
        this.roomid = id;
    }
    public int getRoomNumber(){
        return roomNumber;
    }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
}

