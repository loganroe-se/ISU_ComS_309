package CyStaff.app.Hour;

import CyStaff.app.HourType.HourType;
import CyStaff.app.Status.Status;
import CyStaff.app.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Work Hours" +
        " during a shift by a user such as Hour id, Hour type id, user id and status id.")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hourid;

    private int hoursNum;

    private String date;

    @ManyToOne
    @JoinColumn(name = "hourtypeid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private HourType hourType;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private User user;

    @ManyToOne
    @JoinColumn(name = "statusid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private Status status;

    public int getHourid() { return hourid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user;}

    public HourType getHourType() { return hourType; }
    public void setHourType(HourType hourType) { this.hourType = hourType;}

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getHoursNum() { return hoursNum; }

    public void setHoursNum(int hoursNum) { this.hoursNum = hoursNum; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

