package zevaguillo.apiLambdaS3.uploadFile;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        LambdaLogger logger = context.getLogger();
        logger.log("Prueba inicial de lambda");

        try{
            // obtener el body de la peticio
            String requestBody = request.getBody();
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            FileDTO file = new Gson().fromJson(jsonObject, FileDTO.class);

            // trasformar la String de image en un tipo byte
            byte[] imageBytes = Base64.getDecoder().decode(file.getBase64Image());
            InputStream fileInputStream = new ByteArrayInputStream(imageBytes);


            Region region = Region.US_EAST_1;

//            Crear un cliente de S3
            S3Client s3 = S3Client.builder()
                    .region(region)
                    .build();

//            Creacion de un objecto request para hacer put
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket("api-s3-miniatura")
                    .key("images/"+generateUniqueObjectName("file") +"."+ ContentTypeService.extractExtension(file.getBase64Image()))
                    .contentLength((long) imageBytes.length)
                    .contentType(ContentTypeService.detectContentType(file.getBase64Image()))
                    .build();

//            Upload the object to S3
            s3.putObject(putOb, RequestBody.fromInputStream(fileInputStream, fileInputStream.available()));

            logger.log("Put object in S3");

            logger.log(jsonObject.toString());

            response.setStatusCode(200);
            response.setBody("Solicitud procesada con éxito");

        }catch (Exception e){
            logger.log("Error::" + e.getMessage());
            response.setStatusCode(500);
            response.setBody("Error al procesar la solicitud: " + e.getMessage());

        }

        return response;
    }

    public static String generateUniqueObjectName(String prefix) {
        // Concatenar el prefijo con un identificador único generado por UUID
        String uniqueId = UUID.randomUUID().toString();
        return prefix + "-" + uniqueId;
    }

}
