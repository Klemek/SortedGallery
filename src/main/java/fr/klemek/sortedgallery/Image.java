package fr.klemek.sortedgallery;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Image {

    final long lastModified;
    private String path;
    private final String name;
    private int score;

    Image(String path, int score, long lastModified) {
        this.name = new File(path).getName();
        this.path = path;
        this.score = score;
        this.lastModified = lastModified;
    }

    boolean isGif(){
        return this.path.endsWith(".gif");
    }

    ImageIcon getScaledImage(int winWidth, int winHeight) throws IOException {
        return this.isGif() ?
                new ImageIcon(this.path) :
                new ImageIcon(Utils.scaleImage(ImageIO.read(new File(this.path)), winWidth, winHeight));
    }

    int getScore() {
        return this.score;
    }

    String setScore(int score) {
        if (score < Utils.getInt("minLevel"))
            return "Already minimum score";
        if (score > Utils.getInt("maxLevel"))
            return "Already maximum score";
        String newPath = Utils.moveImage(this.score, score, this.name);
        if (newPath == null)
            return "Cannot move image " + this.score + '/' + this.name;
        this.score = score;
        this.path = newPath;
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
