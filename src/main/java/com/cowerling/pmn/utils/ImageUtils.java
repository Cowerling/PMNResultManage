package com.cowerling.pmn.utils;

import org.apache.commons.text.RandomStringGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

public class ImageUtils {
    public static String compress(InputStream image, int imageWidth, int imageHeight, String imageFormat, String imageLocation) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(ImageIO.read(image), 0, 0, imageWidth, imageHeight, null);

        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build();
        String imageName = randomStringGenerator.generate(20) + "." + imageFormat;
        ImageIO.write(bufferedImage, imageFormat, new File(imageLocation + imageName));

        return imageName;
    }
}
