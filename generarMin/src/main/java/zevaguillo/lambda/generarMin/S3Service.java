package zevaguillo.lambda.generarMin;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class S3Service {
    public static final String JPG_TYPE = "jpg";
    public static final String JPG_MIME = "image/jpeg";
    public static final String PNG_TYPE = "png";
    public static final String PNG_MIME = "image/png";


    // Método para obtener un objeto de S3
    public static InputStream getObject(S3Client s3Client, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    // Método para subir un objeto a S3
    public static void putObject(S3Client s3Client, ByteArrayOutputStream outputStream,
                           String bucket, String key, String imageType, LambdaLogger logger) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Integer.toString(outputStream.size()));

        String contentType = "";

        if (JPG_TYPE.equals(imageType)) {
            contentType = JPG_MIME;
        } else if (PNG_TYPE.equals(imageType)) {
            contentType = PNG_MIME;
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .contentType(contentType)
                .build();

        // Subir a S3
        logger.log("Escribiendo en: " + bucket + "/" + key);
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(outputStream.toByteArray()));
        } catch (AwsServiceException e) {
            logger.log(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
