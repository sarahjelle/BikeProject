package gui;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private JLabel maplabel = new JLabel("Map showing bikes and docking stations");
    private BorderLayout border = new BorderLayout();

    public MapPanel() {
        //-- MAP PANEL
        setBackground(Color.BLUE);
        add(maplabel,border.CENTER);
        doLayout();
    }
}
