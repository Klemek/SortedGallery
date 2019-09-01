package fr.klemek.sortedgallery;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageContainer extends JLabel {

    private Image img;
    private Rectangle.Float zoom = new Rectangle.Float(0, 0, 1, 1);

    public ImageContainer(Icon image) {
        super(image);
    }

    public void setImg(Image img) {
        this.img = img;
        if (null == img || null == img.getImage())
            return;
        if (img.isGif()) {
            super.setIcon(img.getImage());
        } else {
            this.setIcon(null);
        }
    }

    public void setImg(Icon icon) {
        this.img = null;
        super.setIcon(icon);
    }

    @Override
    public void setIcon(Icon icon) {

    }

    public void zoom(float x, float y, float width, float height) {
        this.zoom.setRect(
                this.zoom.x + x * this.zoom.width,
                this.zoom.y + y * this.zoom.height,
                this.zoom.width * width,
                this.zoom.height * height);
    }


    public void resetZoom() {
        this.zoom = new Rectangle.Float(0, 0, 1, 1);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (this.img == null || this.img.isGif()) {
            super.paintComponent(g2);
        } else {
            int w = this.getWidth();
            int h = this.getHeight();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
            ImageIcon img = this.img.getImage();
            if (img != null) {
                BufferedImage bimg = (BufferedImage) img.getImage();
                g2.setColor(Color.WHITE);
                if (bimg.getWidth() / (float) bimg.getHeight() > w / (float) h) {
                    float queryHeight = bimg.getWidth() * h / (float) w;
                    float start = bimg.getHeight() / 2f - queryHeight / 2;
                    g2.drawImage(bimg,
                            0, 0, w, h,
                            (int) (this.zoom.x * bimg.getWidth()), (int) (start + this.zoom.y * queryHeight),
                            (int) ((this.zoom.x + this.zoom.width) * bimg.getWidth()), (int) (start + (this.zoom.y + this.zoom.height) * queryHeight),
                            this);
                } else {
                    float queryWidth = bimg.getHeight() * w / (float) h;
                    float start = bimg.getWidth() / 2f - queryWidth / 2;
                    g2.drawImage(bimg,
                            0, 0, w, h,
                            (int) (start + this.zoom.x * queryWidth), (int) (this.zoom.y * bimg.getHeight()),
                            (int) (start + (this.zoom.x + this.zoom.width) * queryWidth), (int) ((this.zoom.y + this.zoom.height) * bimg.getHeight()),
                            this);
                }
            }

        }
    }
}
