package gui;

import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    private JButton statOneButton =  new JButton("STAT 1");
    private JButton statTwoButton = new JButton("STAT 2");
    private JButton statThreeButton = new JButton("STAT 3");
    private JButton statFourButton = new JButton("STAT 4");
    private JToolBar stattools = new JToolBar(SwingConstants.VERTICAL);
    private BorderLayout border = new BorderLayout();
    public StatPanel(){
        //-- stat PANEL
        //setBackground(Color.BLUE);
        setLayout(border);
        // Adding buttons to toolbar
        stattools.add(statOneButton);
        stattools.add(statTwoButton);
        stattools.add(statThreeButton);
        stattools.add(statFourButton);
        // Changing look anf feel of toolbar
        //stattools.setBackground(Color.BLUE);
        stattools.setBorderPainted(false);
        /*
        statOneButton.setBackground(Color.LIGHT_GRAY);
        statTwoButton.setBackground(Color.LIGHT_GRAY);
        statThreeButton.setBackground(Color.LIGHT_GRAY);
        statFourButton.setBackground(Color.LIGHT_GRAY);
        statOneButton.setForeground(Color.YELLOW);
        statTwoButton.setForeground(Color.YELLOW);
        statThreeButton.setForeground(Color.YELLOW);
        statFourButton.setForeground(Color.YELLOW);
        */
        statOneButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        statTwoButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        statThreeButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        statFourButton.setFont(new Font("MONOSPACED",Font.BOLD,20));
        // Adding toolbar to panel
        add(stattools,border.LINE_START);
    }
}
