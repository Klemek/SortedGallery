package fr.klemek.sortedgallery;

import java.io.File;

import javax.swing.*;

public class Image {

    private final String name;
    private final ImageIcon srcImage;
    private ImageIcon scaledImage;
    private int score;
    final long lastModified;

    Image(String path, int score, long lastModified) {
        this.srcImage = new ImageIcon(path);
        this.name = new File(path).getName();
        this.score = score;
        this.lastModified = lastModified;
    }

    void scale(int winWidth, int winHeight) {
        if (this.scaledImage == null)
            this.scaledImage = Utils.scaleImage(winWidth, winHeight, this.srcImage);
    }

    ImageIcon getScaledImage() {
        return this.scaledImage;
    }

    int getScore() {
        return this.score;
    }

    String setScore(int score) {
        if(score < Utils.getInt("minLevel"))
            return "Already minimum score";
        if(score > Utils.getInt("maxLevel"))
            return "Already maximum score";
        if(!Utils.moveImage(this.score, score, this.name))
            return "Cannot move image "+this.score+ '/' +this.name;
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
