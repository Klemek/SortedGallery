package fr.klemek.sortedgallery;

import fr.klemek.betterlists.BetterArrayList;
import fr.klemek.logger.Logger;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

final class Utils {
    private static final List<String> typeFilter = Arrays.asList("jpg", "png", "bmp", "gif");
    private static final long DEFAULT_SIZE_THRESHOLD = 2097152L; //2MB
    private static Properties config;

    private Utils() {

    }

    private static String getNiceFileSize(long size) {
        if (size < 1024)
            return size + " B";
        size /= 1024;
        if (size < 1024)
            return size + " KB";
        size /= 1024;
        if (size < 1024)
            return size + " MB";
        size /= 1024;
        if (size < 1024)
            return size + " GB";
        size /= 1024;
        return size + " TB";
    }

    static void loadProperties(String path) {
        Utils.config = new Properties();
        try (FileInputStream file = new FileInputStream(path)) {
            Utils.config.load(file);
        } catch (IOException e) {
            Logger.log(e);
        }
    }

    private static String getString(String key) {
        return Utils.config.getProperty(key, null);
    }

    static boolean getBoolean(String key) {
        return Boolean.parseBoolean(Utils.getString(key));
    }

    static int getInt(String key) {
        String value = Utils.getString(key);
        if (value == null)
            return 0;
        Integer iValue = Utils.tryParseInt(value);
        return iValue == null ? 0 : iValue.intValue();
    }

    private static long getLong(String key) {
        String value = Utils.getString(key);
        if (value == null)
            return 0;
        Long lValue = Utils.tryParseLong(value);
        return lValue == null ? 0 : lValue.intValue();
    }

    private static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static void initFolders() {
        File rootFolder = new File(Utils.getString("rootFolder"));
        for (int i = Utils.getInt("minLevel"); i <= Utils.getInt("maxLevel"); i++) {
            File folder = new File(rootFolder, "" + i);
            if (!folder.exists())
                folder.mkdirs();
        }
    }

    static List<Image> loadImages() {
        long threshold = Utils.getLong("fileThreshold");
        if (threshold == 0)
            threshold = Utils.DEFAULT_SIZE_THRESHOLD;

        BetterArrayList<Image> output = new BetterArrayList<>();

        File rootFolder = new File(Utils.getString("rootFolder"));
        File defaultFolder = new File(rootFolder, Utils.getString("defaultLevel"));

        File[] files = rootFolder.listFiles();
        if (files != null)
            for (File file : files)
                if (file.isFile() && Utils.typeFilter.contains(Utils.getExtension(file))
                        && !output.any(img -> img.getFileName().equals(file.getName()))) {
                    try {
                        Files.move(file.toPath(), new File(defaultFolder, file.getName()).toPath());
                    } catch (IOException e) {
                        Logger.log(e);
                    }
                }


        for (int i = Utils.getInt("minLevel"); i <= Utils.getInt("maxLevel"); i++) {
            File folder = new File(rootFolder, "" + i);
            files = folder.listFiles();
            if (files != null) {
                for (File file : files)
                    if (file.isFile() && Utils.typeFilter.contains(Utils.getExtension(file))) {
                        output.add(new Image(file.getAbsolutePath(), i, file.lastModified()));
                        if (file.length() > threshold) {
                            Logger.log("Image {0} is large : {1}", file.getPath(), Utils.getNiceFileSize(file.length()));
                        }
                    }
            }

        }



        return output;
    }

    static String moveImage(int oldScore, int newScore, String name) {
        File oldScoreFolder = new File(Utils.getString("rootFolder"), oldScore + "");
        File newScoreFolder = new File(Utils.getString("rootFolder"), newScore + "");

        File srcImage = new File(oldScoreFolder, name);
        File dstImage = new File(newScoreFolder, name);

        try {
            Files.move(srcImage.toPath(), dstImage.toPath());
            return dstImage.getAbsolutePath();
        } catch (IOException e) {
            Logger.log(e);
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

        java.awt.Image tmp = img.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
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
                return 0;
            case KeyEvent.VK_1:
                return 1;
            case KeyEvent.VK_2:
                return 2;
            case KeyEvent.VK_3:
                return 3;
            case KeyEvent.VK_4:
                return 4;
            case KeyEvent.VK_5:
                return 5;
            case KeyEvent.VK_6:
                return 6;
            case KeyEvent.VK_7:
                return 7;
            case KeyEvent.VK_8:
                return 8;
            case KeyEvent.VK_9:
                return 9;
        }
        return -1;
    }
}
