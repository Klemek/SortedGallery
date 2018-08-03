package fr.klemek.sortedgallery;

public class Launch {

    public static void main(String... args) {
        Utils.loadProperties("config.properties");
        Utils.initFolders();
        new MainWindow();
    }

}
