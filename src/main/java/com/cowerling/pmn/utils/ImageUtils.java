package com.cowerling.pmn.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static String compress(InputStream image, int imageWidth, int imageHeight, String imageFormat, String imageLocation) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(ImageIO.read(image), 0, 0, imageWidth, imageHeight, null);

        String imageName = StringUtils.random() + "." + imageFormat;
        ImageIO.write(bufferedImage, imageFormat, new File(imageLocation + imageName));

        return imageName;
    }
}
