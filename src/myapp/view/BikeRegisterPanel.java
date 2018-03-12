package myapp.view;

import javax.swing.*;
import java.awt.*;

public class BikeRegisterPanel {
    private JPanel bike_reg = new JPanel();
    private JTextField date = new JTextField(8);
    private JTextField price = new JTextField(10);
    private JTextField make = new JTextField(20);
    private JTextField type = new JTextField(10);//should be a list

    public void addPanel() {
        bike_reg.add(date);
        bike_reg.add(price);
        bike_reg.add(make);
        bike_reg.add(type);
    }

    public JPanel getBike_reg() {
        JLabel name = new JLabel();
        name.setText("Name");
        bike_reg.add(name);
        return bike_reg;
    }
}
