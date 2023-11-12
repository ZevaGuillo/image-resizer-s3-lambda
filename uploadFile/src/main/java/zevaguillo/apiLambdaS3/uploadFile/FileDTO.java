package zevaguillo.apiLambdaS3.uploadFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDTO {
    private String base64Image;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String imageByte) {
        this.base64Image = imageByte;
    }
}
