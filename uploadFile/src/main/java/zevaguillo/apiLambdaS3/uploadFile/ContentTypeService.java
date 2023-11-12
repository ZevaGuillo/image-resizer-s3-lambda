package zevaguillo.apiLambdaS3.uploadFile;

public class ContentTypeService {

    public static String detectContentType(String base64Image) {
        // Obtener los primeros bytes (hasta 8) de la cadena Base64
        String prefix = base64Image.substring(0, Math.min(base64Image.length(), 8));

        // Identificar el tipo de contenido basándose en los primeros bytes
        if (prefix.startsWith("/9j/") || prefix.startsWith("data:image/jpeg")) {
            return "image/jpeg";
        } else if (prefix.startsWith("iVBORw") || prefix.startsWith("data:image/png")) {
            return "image/png";
        } else if (prefix.startsWith("R0lGOD") || prefix.startsWith("data:image/gif")) {
            return "image/gif";
        } else {
            // Puedes agregar más comprobaciones según los formatos que necesites
            return "application/octet-stream"; // Tipo de contenido por defecto si no se puede determinar
        }
    }

    public static String extractExtension(String base64Image) {
        // Obtener los primeros bytes (hasta 8) de la cadena Base64
        String prefix = base64Image.substring(0, Math.min(base64Image.length(), 8));

        // Identificar el tipo de contenido basándose en los primeros bytes
        if (prefix.startsWith("/9j/") || prefix.startsWith("data:image/jpeg")) {
            return "jpg";
        } else if (prefix.startsWith("iVBORw") || prefix.startsWith("data:image/png")) {
            return "png";
        } else if (prefix.startsWith("R0lGOD") || prefix.startsWith("data:image/gif")) {
            return "gif";
        } else {
            // Puedes agregar más comprobaciones según los formatos que necesites
            return "unknown"; // Extensión desconocida si no se puede determinar
        }
    }

}
