package zevaguillo.lambda.generarMin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;

@Data
@AllArgsConstructor
public class Resize {

    // Constantes para el tamaño máximo
    private static final float MAX_DIMENSION = 100;

    // Método para redimensionar una imagen
    public static BufferedImage resizeImage(BufferedImage srcImage) {
        int srcHeight = srcImage.getHeight();
        int srcWidth = srcImage.getWidth();

        // Inferir el factor de escala para evitar estirar la imagen de manera innatural
        float scalingFactor = Math.min(
                MAX_DIMENSION / srcWidth, MAX_DIMENSION / srcHeight);

        int width = (int) (scalingFactor * srcWidth);
        int height = (int) (scalingFactor * srcHeight);

        BufferedImage resizedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();

        // Rellenar con blanco antes de aplicar imágenes semitransparentes (alfa)
        graphics.setPaint(Color.white);
        graphics.fillRect(0, 0, width, height);

        // Redimensionamiento bilineal simple
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        graphics.dispose();

        return resizedImage;
    }
}
