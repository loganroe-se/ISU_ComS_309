package CyStaff.app.Settings;

import CyStaff.app.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingsid;
    private String calendarFormat;
    private String preferredName;
    private String weekDisplay;
    private String eventType;
    private String locationType;
    private int numberOfAnnouncements;
    private String sortDirectoryBy;

    @JsonBackReference
    @OneToOne(mappedBy = "settings")
    private User user;


    public void setCalendarFormat(String calendarFormat) {
        this.calendarFormat = calendarFormat;
    }
    public String getCalendarFormat() {
        return calendarFormat;
    }
    public String getPreferredName() {
        return preferredName;
    }
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
    public void setWeekDisplay(String weekDisplay) {
        this.weekDisplay = weekDisplay;
    }
    public String getWeekDisplay() {
        return weekDisplay;
    }
    public void setSettingsid(Long settingsid) {
        this.settingsid = settingsid;
    }
    public Long getSettingsid() {
        return settingsid;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setNumberOfAnnouncements(int numberOfAnnouncements) {
        this.numberOfAnnouncements = numberOfAnnouncements;
    }

    public int getNumberOfAnnouncements() {
        return numberOfAnnouncements;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setSortDirectoryBy(String sortDirectoryBy) {
        this.sortDirectoryBy = sortDirectoryBy;
    }

    public String getSortDirectoryBy() {
        return sortDirectoryBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
