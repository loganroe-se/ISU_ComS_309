package CyStaff.app.Meeting;

import CyStaff.app.Room.Room;
import CyStaff.app.Status.Status;
import CyStaff.app.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@ApiModel(description = "Creates a table in the database that contains details of Meetings" +
        " such as meeting id, room used, status, creator, start/end time, duration and details" +
        " of the meeting")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meetingid;

    @ManyToOne
    @JoinColumn(name = "roomid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private Room room;

    @ManyToOne
    @JoinColumn(name = "statusid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private Status status;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private User user;

    private String startTime;

    private String endTime;

    private int duration;

    private String details;

    private String recipients;

    public void setMeetingid(int meetingid) {
        this.meetingid = meetingid;
    }

    public int getMeetingid() {
        return meetingid;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getRecipients() {
        return recipients;
    }
}
