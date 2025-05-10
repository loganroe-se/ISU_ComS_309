package CyStaff.app.Announcement;

import CyStaff.app.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of announcements" +
                        " such as announcement id, message, data and time of creation.")
@Entity
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int announcementid;

    private String message;

    private String date;

    private String time;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private User user;

    public Announcement(String message, String date, String time)
    {
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public Announcement()
    {
    }

    public int getAnnouncementid() { return announcementid; }
    public String getMessage(){ return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user;}

}
