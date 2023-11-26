package provider.SS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import provider.Canvas;

public class FileStoredPngCanvas implements Canvas {
    private File file;
    private int width;
    private int height;
    private BufferedImage image;

    public FileStoredPngCanvas(int width, int height, File fileIn) {
        this.width = width;
        this.height = height;
        this.file = fileIn;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public BufferedImage getImage() {
        loadImageIfNecessary();
        return image;
    }

    private void loadImageIfNecessary() {
        if (image == null) {
            try {
                image = ImageIO.read(file);
                width = image.getWidth();
                height = image.getHeight();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}