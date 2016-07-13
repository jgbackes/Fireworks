package com.guidewire.devtraining.fireworks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by jbackes on 7/12/2016
 */
public class Utilities {
    private static final HashMap<String, BufferedImage> _cache = new HashMap<>();
    private static final int ICON_SIZE = 16;

    public static BufferedImage getBufferedImageFromRelativePathToClass(String relativePath, Class srcClass) {
        BufferedImage bufferedImage = _cache.get(relativePath);
        if (bufferedImage == null) {
            URL url = srcClass.getResource(relativePath);
            try {
                bufferedImage = ImageIO.read(url);
                _cache.put(relativePath, bufferedImage);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                bufferedImage = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bufferedImage;
    }

    public static ImageIcon getScaledImageIcon(String iconName, Class clazz) {
        ImageIcon result;

        result = new ImageIcon(Utilities.getBufferedImageFromRelativePathToClass(iconName, clazz));
        result = new ImageIcon(getScaledImage(result.getImage(), ICON_SIZE, ICON_SIZE));

        return result;
    }

    //Scales an image to fit inside the Buttons
    private static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
