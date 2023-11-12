package zevaguillo.lambda.generarMin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import software.amazon.awssdk.services.s3.S3Client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LambdaHandler implements RequestHandler<S3Event, String> {

    // Gson se utiliza para convertir objetos a formato JSON
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public final String REGEX = ".*\\.([^\\.]*)";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Prueba inicial de lambda");

        try {
            // Registrar información sobre el evento S3
            logger.log("EVENT: " + gson.toJson(s3Event));

            // Obtener el primer registro del evento S3
            S3EventNotificationRecord record = s3Event.getRecords().get(0);

            // Obtener información del bucket y la clave del objeto S3
            String srcBucket = record.getS3().getBucket().getName();
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            // Configurar el bucket y la clave para la miniatura redimensionada
            String dstKey = "resized-" + srcKey;

            // Inferir el tipo de imagen
            Matcher matcher = Pattern.compile(REGEX).matcher(srcKey);
            if (!matcher.matches()) {
                logger.log("No se puede inferir el tipo de imagen para la clave " + srcKey);
                return "";
            }
            String imageType = matcher.group(1);
            if (!(S3Service.JPG_TYPE.equals(imageType)) && !(S3Service.PNG_TYPE.equals(imageType))) {
                logger.log("Saltando archivo no de imagen " + srcKey);
                return "";
            }

            // Descargar la imagen de S3 a un flujo de entrada
            S3Client s3Client = S3Client.builder().build();
            InputStream s3Object = S3Service.getObject(s3Client, srcBucket, srcKey);

            // Leer la imagen de origen y redimensionarla
            BufferedImage srcImage = ImageIO.read(s3Object);
            BufferedImage newImage = Resize.resizeImage(srcImage);

            // Volver a codificar la imagen al formato de destino
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(newImage, imageType, outputStream);

            // Subir la nueva imagen a S3
            S3Service.putObject(s3Client, outputStream, srcBucket, dstKey, imageType, logger);

            logger.log("Imagen redimensionada con éxito " + srcBucket + "/"
                    + srcKey + " y subida a " + srcBucket + "/" + dstKey);

            return "Ok";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}