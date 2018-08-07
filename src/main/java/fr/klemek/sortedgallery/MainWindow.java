package fr.klemek.sortedgallery;


import fr.klemek.betterlists.BetterArrayList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

class MainWindow extends JFrame {

    private static final DecimalFormat df = new DecimalFormat("#.####");
    private final transient List<Integer> selected;
    private transient List<Image> allImages;
    private transient List<Image> images;
    private transient ConcurrentMap<Integer, ImageIcon> cache;
    private transient Thread autoHideMessage;
    private transient Thread autoPlay;
    private transient Thread refreshCache;

    private JLabel message;
    private JPanel messageBox;
    private JLabel imageContainer;

    private int winWidth;
    private int winHeight;

    private int index;
    private long autoPlayDelay;

    private int cacheOffset;
    private boolean finishedLoading;
    private boolean showScore;
    private boolean shuffle;
    private boolean refreshing;

    private ImageIcon loadingImage;

    MainWindow() {

        this.autoPlayDelay = Utils.getInt("defaultDelay");
        this.finishedLoading = false;
        this.selected = new ArrayList<>();
        this.selected.add(Utils.getInt("defaultLevel"));
        this.showScore = Utils.getBoolean("defaultShowScore");
        this.shuffle = Utils.getBoolean("defaultShuffle");
        this.cacheOffset = Utils.getInt("cacheSize") / 2;

        this.initWindow();
        new Thread(this::postInit).start();
    }

    private void initWindow() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);

        this.setResizable(true);

        this.setLocationRelativeTo(null);

        this.getContentPane().setLayout(new BorderLayout(0, 0));
        this.getContentPane().setBackground(Color.BLACK);

        URL imageUrl = MainWindow.class.getClassLoader().getResource("loading.gif");
        this.loadingImage = new ImageIcon(imageUrl);
        this.imageContainer = new JLabel(loadingImage);
        this.imageContainer.setBounds(0, 0, this.winWidth, this.winHeight);

        this.message = new JLabel("Test message");
        this.messageBox = new JPanel();
        this.messageBox.add(this.message);
        this.cache = new ConcurrentHashMap<>();

        EventListener listener = new EventListener(this);
        this.addKeyListener(listener);
        this.addMouseListener(listener);

        this.setVisible(true);

        this.winWidth = this.getContentPane().getWidth();
        this.winHeight = this.getContentPane().getHeight();
    }

    private void postInit() {
        this.loadWindowPanel();
        this.showMessage("Loading images...");
        this.allImages = Utils.loadImages();
        this.showMessage("Loaded " + this.allImages.size() + " images...");

        if (this.allImages.isEmpty()) {
            System.exit(1);
            return;
        }

        this.allImages = BetterArrayList.fromList(this.allImages).orderByDescending(i -> i.lastModified);
        this.showMessage("Sorted " + this.allImages.size() + " images...");

        this.applySelection();
        this.refreshCache(false);
        this.refreshImage();
        this.finishedLoading = true;
        this.showMessage(null);
    }

    private void loadWindowPanel() {
        this.winWidth = this.getContentPane().getWidth();
        this.winHeight = this.getContentPane().getHeight();

        JLayeredPane layers = new JLayeredPane();
        layers.setLayout(null);
        layers.setPreferredSize(new Dimension(this.winWidth, this.winHeight));

        JPanel bgLayer = new JPanel();
        bgLayer.setBackground(Color.BLACK);
        bgLayer.setBounds(0, 0, this.winWidth, this.winHeight);
        layers.add(bgLayer, (Integer) 0);

        JPanel imageLayer = new JPanel(new BorderLayout(0, 0));
        imageLayer.setOpaque(false);
        imageLayer.setBounds(0, 0, this.winWidth, this.winHeight);
        imageLayer.add(this.imageContainer, BorderLayout.CENTER);
        layers.add(imageLayer, (Integer) 1);

        JPanel messageLayer = new JPanel(new BorderLayout(0, 0));
        messageLayer.setBounds(0, 0, this.winWidth, this.winHeight);
        messageLayer.setOpaque(false);

        JPanel messageLayer2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        messageLayer2.setOpaque(false);
        messageLayer2.add(this.messageBox);

        messageLayer.add(messageLayer2, BorderLayout.SOUTH);
        layers.add(messageLayer, (Integer) 2);

        this.getContentPane().add(layers, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void applySelection() {
        this.images = BetterArrayList.fromList(this.allImages).where(i -> this.selected.contains(i.getScore()));
        if (this.shuffle)
            Utils.shuffle(this.images);
        this.index = 0;
    }

    private void showMessage(String message) {
        this.showMessage(message, true);
    }

    private void showMessage(String message, boolean log) {
        this.messageBox.setVisible(message != null);
        this.message.setText(message);
        this.revalidate();
        this.repaint();

        if (this.autoHideMessage != null && this.autoHideMessage.isAlive())
            this.autoHideMessage.interrupt();
        if (message != null) {
            if (log)
                System.out.println(message);
            this.autoHideMessage = new Thread(this::autoHideMessage);
            this.autoHideMessage.start();
        }
    }

    private void autoHideMessage() {
        Color color = this.messageBox.getBackground();
        try {
            TimeUnit.SECONDS.sleep(1);
            for (int i = 20; i > 0; i--) {
                TimeUnit.MILLISECONDS.sleep(50);
                this.messageBox.setBackground(Utils.setAlpha(color, 255 / 20 * i));
                this.revalidate();
                this.repaint();
            }
            this.messageBox.setVisible(false);
        } catch (InterruptedException ignored) {
        }
        this.messageBox.setBackground(color);
    }

    void computeKeyEvent(int keycode, int modifiers) {
        if (!this.finishedLoading && keycode != KeyEvent.VK_ESCAPE)
            return;
        boolean ctrl = (modifiers & KeyEvent.CTRL_MASK) != 0;
        switch (keycode) {
            case KeyEvent.VK_ESCAPE:
                if (this.autoPlay != null && this.autoPlay.isAlive())
                    this.autoPlay.interrupt();
                this.dispose();
                System.exit(0);
                break;
            case KeyEvent.VK_ADD:
                this.autoPlayDelay *= 2;
                this.showMessage("Delay : " + MainWindow.df.format(this.autoPlayDelay / 1000f) + " seconds");
                this.restartAutoplaying();
                break;
            case KeyEvent.VK_SUBTRACT:
                this.autoPlayDelay /= 2;
                this.showMessage("Delay : " + MainWindow.df.format(this.autoPlayDelay / 1000f) + " seconds");
                this.restartAutoplaying();
                break;
            case KeyEvent.VK_LEFT:
                if (!this.refreshing) {
                    this.refreshing = true;
                    new Thread(() -> {
                        this.previousImage();
                        this.restartAutoplaying();
                    }).start();
                }
                break;
            case KeyEvent.VK_SPACE:
                this.setAutoplaying(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_ENTER:
                if (!this.refreshing) {
                    this.refreshing = true;
                    new Thread(() -> {
                        this.nextImage();
                        this.restartAutoplaying();
                    }).start();
                }
                break;
            case KeyEvent.VK_BACK_SPACE:
                this.shuffle = !this.shuffle;
                this.applySelection();
                this.refreshCache(true);
                this.refreshImage();
                this.restartAutoplaying();
                if (this.shuffle)
                    this.showMessage("Shuffled images");
                else
                    this.showMessage("Images by date");
                break;
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                this.showScore = !this.showScore;
                if (this.showScore)
                    this.refreshImage();
                break;
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_PAGE_DOWN:
                if (!this.images.isEmpty()) {
                    int i = this.index;
                    Image img = this.images.get(i);
                    if (img.isGif()) //not enough
                        this.nextImage();
                    String msg = img.setScore(img.getScore() + (keycode == KeyEvent.VK_PAGE_UP ? 1 : -1));
                    if (msg != null) {
                        this.showMessage(msg);
                    } else {
                        if (!this.selected.contains(img.getScore())) {
                            this.images.remove(i);
                            if (this.index == this.images.size())
                                this.index = 0;
                            this.refreshCache(true);
                            this.refreshImage();
                        }
                        this.showMessage("New score : " + img.getScore());
                    }
                }
                break;
            case KeyEvent.VK_BEGIN:
            case KeyEvent.VK_END:
                this.index = 0;
                this.startRefreshCache();
                this.refreshImage();
                this.showMessage("Moved to first image");
                this.restartAutoplaying();
                break;
            default:
                int numValue = Utils.keyCodeToNum(keycode);
                if (keycode == KeyEvent.VK_EQUALS || (numValue >= Utils.getInt("minLevel") && numValue <= Utils.getInt("maxLevel"))) {
                    if (keycode == KeyEvent.VK_EQUALS) {
                        this.selected.clear();
                        for (int i = Utils.getInt("minLevel"); i <= Utils.getInt("maxLevel"); i++) {
                            this.selected.add(i);
                        }
                    } else if (ctrl) {
                        if (!this.selected.contains(numValue))
                            this.selected.add(numValue);
                    } else {
                        this.selected.clear();
                        this.selected.add(numValue);
                    }

                    Collections.sort(this.selected);
                    this.applySelection();
                    this.refreshCache(true);
                    this.refreshImage();
                    this.restartAutoplaying();
                    this.showMessage(String.format("Score : %s (%d images)",
                            String.join("-",
                                    BetterArrayList.fromList(this.selected)
                                            .select(Object::toString)),
                            this.images.size()));
                }
                break;
        }
    }

    void setAutoplaying(boolean showMessage) {
        if (this.autoPlay != null && this.autoPlay.isAlive()) {
            this.autoPlay.interrupt();
            if (showMessage)
                this.showMessage("Stopped autoplaying");
            this.autoPlay = null;
        } else {
            this.autoPlay = new Thread(this::autoPlay);
            this.autoPlay.start();
            if (showMessage)
                this.showMessage("Autoplaying");
        }
    }

    void restartAutoplaying() {
        if (this.autoPlay != null && this.autoPlay.isAlive()) {
            this.setAutoplaying(false);
            this.setAutoplaying(false);
        }
    }

    void computeMouseEvent(int mouseButton) {
        if (!this.finishedLoading)
            return;
        if (mouseButton == MouseEvent.BUTTON1) {
            this.nextImage();
        } else if (mouseButton == MouseEvent.BUTTON3) {
            this.previousImage();
        }
    }

    void computeResiseEvent() {
        this.winWidth = this.getContentPane().getWidth();
        this.winHeight = this.getContentPane().getHeight();
        this.refreshCache(true);
        this.refreshImage();

    }

    private void autoPlay() {
        boolean quit = false;
        while (!quit) {
            try {
                Thread.sleep(this.autoPlayDelay);
                this.nextImage();
            } catch (InterruptedException e) {
                quit = true;
            }
        }
    }

    private void nextImage() {
        if (this.images == null)
            return;
        this.refreshing = true;
        this.index++;
        if (this.index == this.images.size())
            this.index = 0;
        this.refreshImage();
        this.startRefreshCache();
    }

    private void previousImage() {
        if (this.images == null)
            return;
        this.refreshing = true;
        this.index--;
        if (this.index == -1)
            this.index = this.images.size() - 1;
        this.refreshImage();
        this.startRefreshCache();
    }

    private void startRefreshCache(){
        if(this.refreshCache != null && this.refreshCache.isAlive())
            this.refreshCache.interrupt();
        this.refreshCache = new Thread(() -> this.refreshCache(false));
        this.refreshCache.start();
    }

    private void refreshCache(boolean invalidate) {
        if (invalidate){
            this.cache.clear();
            this.startRefreshCache();
            return;
        }
        //add images
        int size = this.images.size();
        List<Integer> valid = new ArrayList<>(this.cacheOffset * 2 + 1);
        for (int i = this.index - this.cacheOffset; i <= this.index + this.cacheOffset; i++) {
            int j = ((i < 0) ? (i + size) : ((i >= size) ? (i - size) : i));
            valid.add(j);
            if (!this.cache.containsKey(j)) {
                try {
                    this.cache.put(j, this.images.get(j).getScaledImage(this.winWidth, this.winHeight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //remove unused images
        Set<Integer> keys = new HashSet<>(this.cache.keySet()); // clone
        for (int id : keys)
            if (!valid.contains(id)) {
                this.cache.remove(id);
            }

    }

    private void refreshImage() {
        if (this.images == null || this.winWidth == 0)
            return;

        this.refreshing = true;
        if (this.index < 0 || this.images.isEmpty())
            this.imageContainer.setVisible(false);
        else {
            if (this.cache.containsKey(this.index)) {
                this.imageContainer.setVisible(true);
                this.imageContainer.setIcon(this.cache.get(this.index));
            } else {
                this.imageContainer.setIcon(this.loadingImage);
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                    this.refreshImage();
                }).start();
            }
            if (this.showScore)
                this.showMessage(this.images.get(this.index).getScore() + " (" + (this.index + 1) + '/' + this.images.size() + ')', false);
        }

        this.revalidate();
        this.repaint();
        this.refreshing = false;
    }


}
