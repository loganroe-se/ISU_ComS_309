package CyStaff.app.User;

import CyStaff.app.ImageData.ImageData;
import CyStaff.app.Privilege.Privilege;
import CyStaff.app.Settings.Settings;
import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

@ApiModel(description = "Creates a table in the database that contains details of Users" +
        " such as user id, password, first and last name, email, etc")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userid;
    private String password;
    private String firstName;
    private String lastName;
    private String birthday;
    private String phone;
    private String email;
    private String building;
    private String active;
    private String lastLogin;

    @ManyToOne
    @JoinColumn(name = "privilegeid", nullable = false)
    @JsonIgnore
    @JsonProperty
    private Privilege privilege;

    //Keep working in this implementation as we need
    @ManyToOne
    @JoinColumn(name = "imageid")
    @JsonIgnore
    @JsonProperty
    private ImageData profilePicture;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settingsid", referencedColumnName = "settingsid")
    private Settings settings;

    public User(String password, String firstName, String lastName, String birthday, String phone,
                String email, String building, String active, String lastLogin)
    {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.building = building;
        this.active = active;
        this.lastLogin = lastLogin;
    }

    public User()
    {
    }

    public int getUserid() { return userid; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    public Privilege getPrivilege() { return privilege; }
    public void setPrivilege(Privilege privilege) { this.privilege = privilege; }
    public ImageData getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(ImageData profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
//        settings.setUser(this);
    }

    public Settings getSettings() {
        return settings;
    }
}
