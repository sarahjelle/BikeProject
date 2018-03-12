package myapp.view;

import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class MenuActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        System.out.println("Selected: " + e.getActionCommand());
    }
}
