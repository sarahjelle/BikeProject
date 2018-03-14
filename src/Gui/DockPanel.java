package gui;

import javax.swing.*;
import java.awt.*;

public class DockPanel extends JPanel{
    private JButton adddockButton =  new JButton("ADD");
    private JButton editdockButton = new JButton("EDIT");
    private JButton deletedockButton = new JButton("DELETE");
    private JButton statusdockButton = new JButton("STATUS");
    private JToolBar docktools = new JToolBar(SwingConstants.VERTICAL);
    private BorderLayout border = new BorderLayout();
    public DockPanel(){
        //-- DOCK PANEL
        setBackground(Color.BLUE);
        setLayout(border);
        // Adding buttons to toolbar
        docktools.add(adddockButton);
        docktools.add(editdockButton);
        docktools.add(deletedockButton);
        docktools.add(statusdockButton);
        // Changing look anf feel of toolbar
        docktools.setBackground(Color.BLUE);
        docktools.setBorderPainted(false);
        adddockButton.setBackground(Color.LIGHT_GRAY);
        editdockButton.setBackground(Color.LIGHT_GRAY);
        deletedockButton.setBackground(Color.LIGHT_GRAY);
        statusdockButton.setBackground(Color.LIGHT_GRAY);
        adddockButton.setForeground(Color.YELLOW);
        editdockButton.setForeground(Color.YELLOW);
        deletedockButton.setForeground(Color.YELLOW);
        statusdockButton.setForeground(Color.YELLOW);
        adddockButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        editdockButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        deletedockButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        statusdockButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        // Adding toolbar to panel
        add(docktools,border.LINE_START);
    }
}
