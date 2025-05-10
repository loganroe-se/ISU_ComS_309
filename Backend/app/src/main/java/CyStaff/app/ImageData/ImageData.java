package CyStaff.app.ImageData;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@ApiModel(description = "Stores the image information in the database, using an unique id, name, type and the " +
        "image information.")
@Entity
@Table(name = "imageData")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageid;
    private String name;
    private String type;

    @Lob
    @Column(name = "imageData", length = 1000)
    private byte[] imageData;

    public byte[] getImageData() {
        return imageData;
    }
    public Long getImageid() {
        return imageid;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
    public void setImageid(Long id) {
        this.imageid = id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
