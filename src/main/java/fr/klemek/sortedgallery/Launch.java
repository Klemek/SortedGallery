package fr.klemek.sortedgallery;

import fr.klemek.logger.Logger;

import java.awt.*;

public class Launch {

    public static void main(String... args) {
        Logger.init("logging.properties");
        Utils.loadProperties("config.properties");
        Utils.initFolders();
        EventQueue.invokeLater(MainWindow::new);
    }

}
