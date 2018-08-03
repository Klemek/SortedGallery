package fr.klemek.sortedgallery;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EventListener implements MouseListener, KeyListener{

    private final MainWindow win;

    public EventListener(MainWindow win){
        this.win = win;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //ignore
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.win.computeKeyEvent(e.getKeyCode(), e.getModifiers());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //ignored
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.win.computeMouseEvent(e.getButton());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //ignore
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //ignore
    }
}
