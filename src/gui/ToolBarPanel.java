package gui;

import javax.swing.*;
import java.awt.*;

public class ToolBarPanel extends JPanel {
    public JButton buttonBike, buttonDock, buttonStat, buttonMap;

    public ToolBarPanel(Menu.PanelListener listener) {
        JToolBar tools = new JToolBar();
        //setBackground(Color.BLUE);
        //Adding buttons
        buttonBike = new JButton("BIKE");
        buttonDock = new JButton("DOCKING STATION");
        buttonStat = new JButton("STATISTICS");
        buttonMap = new JButton("MAP");
        // Look and feel
        //tools.setBackground(Color.BLUE);
        tools.setBorderPainted(false);
        /*
        buttonBike.setBackground(Color.LIGHT_GRAY);
        buttonDock.setBackground(Color.LIGHT_GRAY);
        buttonStat.setBackground(Color.LIGHT_GRAY);
        buttonMap.setBackground(Color.LIGHT_GRAY);

        buttonBike.setForeground(Color.YELLOW);
        buttonDock.setForeground(Color.YELLOW);
        buttonStat.setForeground(Color.YELLOW);
        buttonMap.setForeground(Color.YELLOW);
        */
        buttonBike.setFont(new Font("MONOSPACED", Font.BOLD, 24));
        buttonDock.setFont(new Font("MONOSPACED", Font.BOLD, 24));
        buttonStat.setFont(new Font("MONOSPACED", Font.BOLD, 24));
        buttonMap.setFont(new Font("MONOSPACED", Font.BOLD, 24));
        // Adding listener
        buttonBike.addActionListener(listener);
        buttonDock.addActionListener(listener);
        buttonStat.addActionListener(listener);
        buttonMap.addActionListener(listener);
        // Adding buttons to toolbar
        tools.add(buttonBike);
        tools.add(buttonDock);
        tools.add(buttonStat);
        tools.add(buttonMap);
        // Adding toolbar to panel
        add(tools);
    }
}
