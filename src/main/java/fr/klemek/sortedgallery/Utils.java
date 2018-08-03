package fr.klemek.sortedgallery;

import fr.klemek.betterlists.BetterArrayList;
import fr.klemek.betterlists.BetterList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.*;

final class Utils {
    private static final List<String> typeFilter = Arrays.asList("jpg", "png", "bmp", "gif");

    private static Properties config;

    private Utils() {

    }

    static void loadProperties(String path) {
        Utils.config = new Properties();
        try (FileInputStream file = new FileInputStream(path)) {
            Utils.config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getString(String key) {
        return Utils.config.getProperty(key, null);
    }

    static boolean getBoolean(String key){
        return Boolean.parseBoolean(Utils.getString(key));
    }

    static int getInt(String key) {
        String value = Utils.getString(key);
        if (value == null)
            return 0;
        Integer iValue = Utils.tryParseInt(value);
        return iValue == null ? 0 : iValue.intValue();
    }

    static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static void initFolders() {
        File rootFolder = new File(Utils.getString("rootFolder"));
        File defaultFolder = new File(rootFolder, Utils.getString("defaultLevel"));
        for (int i = Utils.getInt("minLevel"); i <= Utils.getInt("maxLevel"); i++) {
            File folder = new File(rootFolder, "" + i);
            if (!folder.exists())
                folder.mkdirs();
        }
        File[] files = rootFolder.listFiles();
        if (files != null)
            for (File file : files)
                if (file.isFile() && Utils.typeFilter.contains(Utils.getExtension(file))) {
                    try {
                        Files.move(file.toPath(), new File(defaultFolder, file.getName()).toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
    }

    static List<Image> loadImages() {
        List<Image> output = new ArrayList<>();

        File rootFolder = new File(Utils.getString("rootFolder"));
        for (int i = Utils.getInt("minLevel"); i <= Utils.getInt("maxLevel"); i++) {
            File folder = new File(rootFolder, "" + i);
            File[] files = folder.listFiles();
            if (files != null){
                for (File file : files)
                    if (file.isFile() && Utils.typeFilter.contains(Utils.getExtension(file))) {
                        output.add(new Image(file.getAbsolutePath(), i, file.lastModified()));
                    }
            }

        }
        return output;
    }

    static String moveImage(int oldScore, int newScore, String name){
        File oldScoreFolder = new File(Utils.getString("rootFolder"), oldScore+"");
        File newScoreFolder = new File(Utils.getString("rootFolder"), newScore+"");

        File srcImage = new File(oldScoreFolder, name);
        File dstImage = new File(newScoreFolder, name);

        try {
            Files.move(srcImage.toPath(), dstImage.toPath());
            return dstImage.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static BufferedImage scaleImage(BufferedImage img, int winWidth, int winHeight) {

        float imgRatio = img.getWidth() / (float) img.getHeight();
        int newWidth;
        int newHeight;
        if (imgRatio < winWidth / (float) winHeight) {
            newHeight = winHeight;
            newWidth = (int) (newHeight * imgRatio);
        } else {
            newWidth = winWidth;
            newHeight = (int) (winWidth / imgRatio);
        }

        java.awt.Image tmp = img.getScaledInstance(newWidth, newHeight,  java.awt.Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static String getExtension(File file) {
        if (!file.isFile())
            return null;
        String[] split = file.getName().split("\\.");
        if (split.length == 1)
            return null;
        return split[split.length - 1].toLowerCase();
    }

    static <T> void shuffle(List<T> list) {
        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            int i1 = random.nextInt(list.size());
            int i2 = random.nextInt(list.size());
            T tmp = list.get(i1);
            list.set(i1, list.get(i2));
            list.set(i2, tmp);
        }
    }

    static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    static int keyCodeToNum(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_0:
            case KeyEvent.VK_NUMPAD0:
                return 0;
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                return 1;
            case KeyEvent.VK_2:
            case KeyEvent.VK_NUMPAD2:
                return 2;
            case KeyEvent.VK_3:
            case KeyEvent.VK_NUMPAD3:
                return 3;
            case KeyEvent.VK_4:
            case KeyEvent.VK_NUMPAD4:
                return 4;
            case KeyEvent.VK_5:
            case KeyEvent.VK_NUMPAD5:
                return 5;
            case KeyEvent.VK_6:
            case KeyEvent.VK_NUMPAD6:
                return 6;
            case KeyEvent.VK_7:
            case KeyEvent.VK_NUMPAD7:
                return 7;
            case KeyEvent.VK_8:
            case KeyEvent.VK_NUMPAD8:
                return 8;
            case KeyEvent.VK_9:
            case KeyEvent.VK_NUMPAD9:
                return 9;
        }
        return -1;
    }
}
