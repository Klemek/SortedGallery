package fr.klemek.sortedgallery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Image {

    final long lastModified;
    private final String path;
    private final String name;
    private int score;

    Image(String path, int score, long lastModified) {
        this.name = new File(path).getName();
        this.path = path;
        this.score = score;
        this.lastModified = lastModified;
    }

    ImageIcon getScaledImage(int winWidth, int winHeight) throws IOException {
        if(this.path.endsWith(".gif"))
            return new ImageIcon(this.path);
        else
            return new ImageIcon( Utils.scaleImage(ImageIO.read(new File(this.path)), winWidth, winHeight));
    }

    int getScore() {
        return this.score;
    }

    String setScore(int score) {
        if (score < Utils.getInt("minLevel"))
            return "Already minimum score";
        if (score > Utils.getInt("maxLevel"))
            return "Already maximum score";
        if (!Utils.moveImage(this.score, score, this.name))
            return "Cannot move image " + this.score + '/' + this.name;
        this.score = score;
        return null;
    }

    @Override
    public String toString() {
        return "Image{" +
                "name='" + this.name + '\'' +
                ", score=" + this.score +
                ", lastModified=" + this.lastModified +
                '}';
    }
}
