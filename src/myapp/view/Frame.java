package myapp.view;

import javax.swing.*;
import java.awt.event.*;

public class Frame extends JFrame {

    public Frame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private class MenuAction implements ActionListener{
        private JPanel panel;

        private MenuAction(JPanel panel){
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }
}
