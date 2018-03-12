package myapp.view;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class FrameChange extends JFrame {

    private BikeRegisterPanel test = new BikeRegisterPanel();
    private JPanel bike_reg = test.getBike_reg();
    private JPanel bike_edit = new JPanel();

    public FrameChange() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initMenu();
        setLayout(new BorderLayout());
        bike_reg.setBackground(Color.RED);
        bike_edit.setBackground(Color.BLUE);

    }


    private class MenuAction implements ActionListener {

        private JPanel panel;

        private MenuAction(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            changePanel(panel);

        }

    }

    private void initMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Bike");
        JMenuItem reg_bike = new JMenuItem("Register");
        JMenuItem edit_bike = new JMenuItem("Edit");
        menubar.add(menu);
        menu.add(reg_bike);
        menu.add(edit_bike);
        setJMenuBar(menubar);
        reg_bike.addActionListener(new MenuAction(bike_reg));
        edit_bike.addActionListener(new MenuAction(bike_edit));
    }

    private void changePanel(JPanel panel){
        /*getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().doLayout();
        update(getGraphics());*/
        setContentPane(panel);
        invalidate();
        validate();
    }

    public static void main(String[] args) {
        FrameChange frame = new FrameChange();
        frame.setBounds(200, 200, 500, 500);
        frame.setVisible(true);

    }
}