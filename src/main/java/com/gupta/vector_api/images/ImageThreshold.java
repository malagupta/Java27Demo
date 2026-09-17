package com.gupta.vector_api.images;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageThreshold {
    private static final VectorSpecies<Integer> SPECIES =
            IntVector.SPECIES_PREFERRED;

    static int[] blackAndWhite(int[] pixels) {
        int[] output = new int[pixels.length];
        var black = IntVector.zero(SPECIES);
        var white = IntVector.broadcast(SPECIES, 255);
        int bound = SPECIES.loopBound(pixels.length);
        int i = 0;

        for (; i < bound; i += SPECIES.length()) {
            var gray = IntVector.fromArray(SPECIES, pixels, i);
            var light = gray.compare(VectorOperators.GE, 128);
            black.blend(white, light).intoArray(output, i);
        }

        for (; i < pixels.length; i++) {
            output[i] = pixels[i] >= 128 ? 255 : 0;
        }
        return output;
    }
    public static void main(String[] args) throws IOException {
        //BufferedImage input = ImageIO.read(new File("resources/grayscale.png"));

        URL resource = ImageThreshold.class.getResource("/com.gupta.vector_api.images/grayscale.png");
        if (resource == null) {
            throw new IOException("Resource not found on classpath: /com.gupta.vector_api.images/grayscale.png");
        }
        BufferedImage input = ImageIO.read(resource);
        
        if (input == null) throw new IOException("Cannot decode the input image");
        int width = input.getWidth();
        int height = input.getHeight();
        int[] pixels = input.getRGB(0, 0, width, height, null, 0, width);
        
        for (int i = 0; i < pixels.length; i++) {
            int rgb = pixels[i];
            int red = (rgb >>> 16) & 255;
            int green = (rgb >>> 8) & 255;
            int blue = rgb & 255;
            pixels[i] = (red + green + blue) / 3;
        }
        int[] result = blackAndWhite(pixels);
        
        for (int i = 0; i < result.length; i++) {
            int gray = result[i];
            result[i] = (gray << 16) | (gray << 8) | gray;
        }
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        output.setRGB(0, 0, width, height, result, 0, width);
        
        File folder = new File("output");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IOException("Cannot create output folder");
        }
        File target = new File(folder, "black-and-white.png");
        if (!ImageIO.write(output, "png", target)) {
            throw new IOException("No PNG writer available");
        }
        System.out.println("Compare resources/grayscale.png with " + target);
    }
}