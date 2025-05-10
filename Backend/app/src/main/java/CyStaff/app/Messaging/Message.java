package CyStaff.app.Messaging;

import java.util.Date;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Creates a table in the database that contains details of Messages" +
        " such as message id, user, message content and date sent.")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageid;

    private String userName;

    private String destUserName;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sent = new Date();

    public int getMessageid() {
        return messageid;
    }
    public void setMessageid(int messageid) {
        this.messageid = messageid;
    }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDestUserName() { return destUserName; }
    public void setDestUserName(String destUserName) { this.destUserName = destUserName; }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getSent() {
        return sent;
    }
    public void setSent(Date sent) {
        this.sent = sent;
    }

}

