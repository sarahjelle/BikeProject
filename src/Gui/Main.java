package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Login gui = new Login();
        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setSize(800,600);
        gui.setVisible(true);
    }
}
