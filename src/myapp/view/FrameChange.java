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

        JLabel reg = new JLabel("Bike_reg");
        JLabel edit = new JLabel("Bike_edit");
        bike_reg.setBackground(Color.BLUE);
        bike_edit.setBackground(Color.RED);
        bike_reg.add(reg);
        bike_edit.add(edit);
    }

    private class MenuAction implements ActionListener {

        private JPanel panel;

        private MenuAction(JPanel pnl) {
            this.panel = pnl;
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
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().doLayout();
        update(getGraphics());
    }

    public static void main(String[] args) {
        FrameChange frame = new FrameChange();
        frame.setBounds(200, 200, 300, 200);
        frame.setVisible(true);

    }
}