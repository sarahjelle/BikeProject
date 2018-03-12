package myapp.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUItest {
    private JFrame frame = null;

    public GUItest(){
        JFrame frame = new JFrame("Bike rental");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu bike = new JMenu("Bike");
        JMenu docking_station = new JMenu("Docking station");
        JMenu statistic = new JMenu("Statistic");
        JMenu map = new JMenu("Map");
        mb.add(bike);
        mb.add(docking_station);
        mb.add(statistic);
        mb.add(map);

        //Adding options to bike
        JMenuItem bike_reg = new JMenuItem("Register");
        JMenuItem  bike_edit = new JMenuItem("Edit");
        JMenuItem bike_delete = new JMenuItem("Delete");
        JMenuItem bike_status = new JMenuItem("Show status");
        JMenuItem bike_repair_reg = new JMenuItem("Register repair");
        JMenuItem bike_repair_return = new JMenuItem("Return from repair");

        bike.add(bike_reg);
        bike.add(bike_edit);
        bike.add(bike_delete);
        bike.add(bike_status);
        bike.add(bike_repair_reg);
        bike.add(bike_repair_return);

        bike_reg.addActionListener(new MenuActionListener());
        bike_edit.addActionListener(new MenuActionListener());
        bike_delete.addActionListener(new MenuActionListener());
        bike_status.addActionListener(new MenuActionListener());
        bike_repair_reg.addActionListener(new MenuActionListener());
        bike_repair_return.addActionListener(new MenuActionListener());

        //Adding options to docking_station
        JMenuItem dock_add = new JMenuItem("Add");
        JMenuItem  dock_edit = new JMenuItem("Edit");
        JMenuItem  dock_delete = new JMenuItem("Delete");
        JMenuItem  dock_status = new JMenuItem("Status");

        docking_station.add(dock_add);
        docking_station.add(dock_edit);
        docking_station.add(dock_delete);
        docking_station.add(dock_status);

        dock_add.addActionListener(new MenuActionListener());
        dock_edit.addActionListener(new MenuActionListener());
        dock_delete.addActionListener(new MenuActionListener());
        dock_status.addActionListener(new MenuActionListener());

        //Adding options to statistic
        JMenuItem statistic1 = new JMenuItem("Statistic1");
        JMenuItem  statistic2 = new JMenuItem("Statistic2");
        JMenuItem  statistic3 = new JMenuItem("Statistic3");

        statistic.add(statistic1);
        statistic.add(statistic2);
        statistic.add(statistic3);

        //add action listener.
        statistic1.addActionListener(new MenuActionListener());
        statistic2.addActionListener(new MenuActionListener());
        statistic3.addActionListener(new MenuActionListener());

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
    }

    class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            frame = null;
        }
    }

    public static void main(String[]args) {
        GUItest test = new GUItest();
    }
}


