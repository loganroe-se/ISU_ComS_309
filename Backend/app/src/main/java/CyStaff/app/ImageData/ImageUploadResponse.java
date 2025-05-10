package CyStaff.app.ImageData;

public class ImageUploadResponse {
    private String status;

    ImageUploadResponse(String value){
        this.status = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
