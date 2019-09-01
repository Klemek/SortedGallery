package fr.klemek.sortedgallery;

import fr.klemek.logger.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Image {

    final long lastModified;
    private String path;
    private final String name;
    private int score;
    private ImageIcon img;

    Image(String path, int score, long lastModified) {
        this.name = new File(path).getName();
        this.path = path;
        this.score = score;
        this.lastModified = lastModified;
    }

    String getFileName(){
        return this.name;
    }

    boolean isGif(){
        return this.path.endsWith(".gif");
    }

    ImageIcon getImage() {
        if (this.img == null) {
            try {
                this.img = this.isGif() ?
                        new ImageIcon(this.path) :
                        new ImageIcon(ImageIO.read(new File(this.path)));
            } catch (IOException e) {
                Logger.log(e);
            }
        }
        return this.img;
    }

    void clean() {
        this.img = null;
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
