package org.hotal.lightpvp.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static List<BufferedImage> splitImage(BufferedImage image, int rows, int columns) {

        final List<BufferedImage> response = new ArrayList<>();

        int chunkWidth = image.getWidth() / columns; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                int imageType = image.getType();
                if (imageType == 0) {
                    imageType = 5;
                }

                BufferedImage splitImage = new BufferedImage(chunkWidth, chunkHeight, imageType);

                Graphics2D gr = splitImage.createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();

                response.add(splitImage);
            }
        }

        return response;

    }

}
